"use client";

import { useCallback, useEffect, useMemo, useState } from "react";
import CustomerHeader from "@/components/headers/CustomerHeader";
import {
  OrderStatusPayload,
  OrderStatus,
  PaymentProvider,
  PaymentSession,
  MallProductDetail,
  MockOrder,
} from "@/types/mall";
import { createPaymentSession, fetchPaymentStatus } from "@/lib/payments";
import { cn } from "@/libs/utils";
import {
  AlertCircle,
  CheckCircle,
  Copy,
  CreditCard,
  ExternalLink,
  Loader2,
  Minus,
  Phone,
  Plus,
  QrCode,
  Smartphone,
} from "lucide-react";
import { toDataURL } from "qrcode";

type Props = {
  product: MallProductDetail;
  initialQuantity?: number;
  defaultProvider?: string;
};

type ProviderOption = {
  id: PaymentProvider;
  label: string;
  description: string;
};

const PROVIDERS: ProviderOption[] = [
  {
    id: "toss",
    label: "토스페이",
    description: "토스 앱/카드 간편 결제",
  },
  {
    id: "kakaopay",
    label: "카카오페이",
    description: "카카오톡 간편 결제",
  },
];

const FINAL_STATUSES = new Set(["COMPLETED", "FAILED", "CANCELED"]);

export default function OrderCheckout({
  product,
  initialQuantity = 1,
  defaultProvider,
}: Props) {
  const [quantity, setQuantity] = useState(clampQuantity(initialQuantity));
  const [selectedProvider, setSelectedProvider] = useState<PaymentProvider>(
    normalizeProvider(defaultProvider) ?? "toss",
  );
  const [buyerName, setBuyerName] = useState("");
  const [buyerPhone, setBuyerPhone] = useState("");
  const [session, setSession] = useState<PaymentSession | null>(null);
  const [status, setStatus] = useState<OrderStatusPayload | null>(null);
  const [statusError, setStatusError] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isMobile, setIsMobile] = useState(false);
  const [qrImage, setQrImage] = useState<string | null>(null);
  const [copyFeedback, setCopyFeedback] = useState<string | null>(null);
  const [isConfirming, setIsConfirming] = useState(false);
  const [confirmFeedback, setConfirmFeedback] = useState<string | null>(null);

  const productPrice = product.price ?? 0;
  const shippingFee = product.shippingFee ?? 0;
  const totalPrice = productPrice * quantity + shippingFee;
  const formattedProductPrice = formatCurrency(productPrice);
  const formattedTotalPrice = formatCurrency(totalPrice);

  const maxQuantity = useMemo(() => {
    const limit = product.stockQuantity ?? 99;
    if (!Number.isFinite(limit)) return 99;
    return Math.max(1, Math.min(limit, 99));
  }, [product.stockQuantity]);

  useEffect(() => {
    setIsMobile(checkIsMobile());
  }, []);

  useEffect(() => {
    setQuantity((prev) => Math.min(prev, maxQuantity));
  }, [maxQuantity]);

  useEffect(() => {
    if (!session || isMobile) {
      setQrImage(null);
      return;
    }
    const payload =
      session.appScheme ||
      session.mobileUrl ||
      session.pcUrl ||
      session.qrPayload;
    if (!payload) {
      setQrImage(null);
      return;
    }

    let active = true;

    toDataURL(payload, { width: 320, margin: 1 })
      .then((url) => {
        if (active) setQrImage(url);
      })
      .catch(() => {
        if (active) setQrImage(null);
      });

    return () => {
      active = false;
    };
  }, [session, isMobile]);

  useEffect(() => {
    if (!session) return;
    if (status && FINAL_STATUSES.has(status.status)) return;

    let cancelled = false;
    let timer: ReturnType<typeof setTimeout> | undefined;

    const poll = async () => {
      if (cancelled) return;
      try {
        const nextStatus = await fetchPaymentStatus(
          session.provider,
          session.orderId,
          session.referenceId,
        );
        if (cancelled) return;
        setStatus(nextStatus);
        setStatusError(null);
        if (!FINAL_STATUSES.has(nextStatus.status)) {
          timer = setTimeout(poll, 3000);
        }
      } catch (err) {
        if (cancelled) return;
        setStatusError(
          err instanceof Error
            ? err.message
            : "결제 상태를 확인할 수 없습니다.",
        );
        timer = setTimeout(poll, 5000);
      }
    };

    timer = setTimeout(poll, 2000);

    return () => {
      cancelled = true;
      if (timer) clearTimeout(timer);
    };
  }, [session, status]);

  const handleDecrease = () => setQuantity((prev) => Math.max(1, prev - 1));
  const handleIncrease = () =>
    setQuantity((prev) => (prev >= maxQuantity ? prev : prev + 1));

  const resetSession = useCallback((options?: { keepError?: boolean }) => {
    setSession(null);
    setStatus(null);
    setStatusError(null);
    setQrImage(null);
    setCopyFeedback(null);
    setConfirmFeedback(null);
    setIsConfirming(false);
    if (!options?.keepError) {
      setError(null);
    }
  }, []);

  const startCheckout = async () => {
    resetSession();
    setIsSubmitting(true);
    setError(null);
    setStatusError(null);
    setCopyFeedback(null);
    setConfirmFeedback(null);

    try {
      const nextSession = await createPaymentSession({
        provider: selectedProvider,
        productId: product.id,
        productName: product.name,
        quantity,
        amount: totalPrice,
        buyerName: buyerName.trim() || undefined,
        buyerPhone: buyerPhone.trim() || undefined,
        device: isMobile ? "mobile" : "desktop",
      });

      setSession(nextSession);
      setStatus({
        orderId: nextSession.orderId,
        provider: nextSession.provider,
        status: "PENDING",
        updatedAt: new Date().toISOString(),
      });

      const redirectTarget =
        (isMobile ? nextSession.appScheme : nextSession.mobileUrl) ??
        nextSession.pcUrl ??
        nextSession.qrPayload;

      if (redirectTarget && isMobile) {
        setTimeout(() => {
          window.location.href = redirectTarget;
        }, 150);
      }
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "주문 연동 중 문제가 발생했습니다.",
      );
      resetSession({ keepError: true });
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleMockConfirm = async () => {
    if (!session || isConfirming) return;
    setIsConfirming(true);
    setConfirmFeedback(null);
    try {
      const response = await fetch("/api/orders/mock", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          orderId: session.orderId,
          provider: session.provider,
          productId: session.productId,
          productName: session.productName,
          productImage: product.imageUrl ?? product.gallery?.[0] ?? null,
          sellerName: product.sellerName ?? null,
          buyerName: buyerName.trim() || null,
          quantity: session.quantity,
          unitPrice: productPrice,
          amount: session.amount,
          status: "COMPLETED",
        }),
      });

      if (!response.ok) {
        let message = "임시 주문 확정에 실패했습니다.";
        try {
          const payload = (await response.json()) as {
            error?: { message?: string };
          };
          message = payload.error?.message ?? message;
        } catch {
          // ignore json parse errors
        }
        throw new Error(message);
      }

      const payload = (await response.json()) as {
        success: boolean;
        order?: MockOrder;
      };

      if (!payload.success || !payload.order) {
        throw new Error("임시 주문 데이터를 저장하지 못했습니다.");
      }

      const order = payload.order;
      setStatus({
        orderId: order.orderId,
        provider: order.provider,
        status: order.status,
        approvedAt: order.confirmedAt,
        updatedAt: order.updatedAt ?? order.confirmedAt,
      });
      setStatusError(null);
      setConfirmFeedback("임시로 주문을 완료 처리했습니다.");
    } catch (err) {
      const message =
        err instanceof Error ? err.message : "임시 주문 확정에 실패했습니다.";
      setStatusError(message);
      setConfirmFeedback(null);
    } finally {
      setIsConfirming(false);
    }
  };

  const handleCopyLink = async () => {
    if (!session) return;
    const target =
      session.appScheme ||
      session.mobileUrl ||
      session.pcUrl ||
      session.qrPayload;
    if (!target) {
      setCopyFeedback("복사할 링크가 없습니다.");
      return;
    }
    try {
      await navigator.clipboard.writeText(target);
      setCopyFeedback("링크가 복사되었습니다.");
    } catch {
      setCopyFeedback("클립보드 복사에 실패했습니다. 직접 복사해주세요.");
    }
  };

  const sessionLink =
    session?.appScheme ||
    session?.mobileUrl ||
    session?.pcUrl ||
    session?.qrPayload;

  const currentStatus = status?.status ?? "PENDING";
  const canMockConfirm =
    Boolean(session) && !FINAL_STATUSES.has(currentStatus as OrderStatus);

  return (
    <div className="min-h-screen bg-gray-50">
      <CustomerHeader title="주문하기" />
      <main className="mx-auto w-full max-w-5xl px-4 pb-24 pt-24">
        <div className="grid gap-6 lg:grid-cols-[1.1fr,0.9fr]">
          <section className="space-y-6 rounded-3xl bg-white p-8 shadow-sm">
            <header className="flex flex-col gap-2">
              <span className="text-sm font-medium text-orange-500">
                판매자 주문
              </span>
              <h1 className="text-2xl font-semibold text-gray-900 sm:text-3xl">
                {product.name}
              </h1>
              {product.sellerName ? (
                <p className="text-sm text-gray-500">{product.sellerName}</p>
              ) : null}
            </header>

            <div className="flex flex-wrap items-center gap-6 rounded-2xl bg-gray-100/60 p-5">
              <div className="flex flex-col gap-2">
                <span className="text-xs text-gray-500">상품 가격</span>
                <strong className="text-xl text-gray-900">
                  {formattedProductPrice}원
                </strong>
              </div>
              <div className="hidden h-10 w-px bg-gray-300 sm:block" />
              <div className="flex flex-col gap-2">
                <span className="text-xs text-gray-500">배송비</span>
                <strong className="text-xl text-gray-900">
                  {shippingFee === 0
                    ? "무료"
                    : `${formatCurrency(shippingFee)}원`}
                </strong>
              </div>
              <div className="hidden h-10 w-px bg-gray-300 sm:block" />
              <div className="flex flex-col gap-2">
                <span className="text-xs text-gray-500">총 결제금액</span>
                <strong className="text-xl text-gray-900">
                  {formattedTotalPrice}원
                </strong>
              </div>
            </div>

            <div className="space-y-4">
              <label className="flex items-center justify-between text-sm font-medium text-gray-700">
                주문 수량
                <span className="text-xs font-normal text-gray-500">
                  재고 {product.stockQuantity ?? "-"}개
                </span>
              </label>
              <div className="flex items-center gap-3">
                <div className="flex items-center overflow-hidden rounded-full border border-gray-200 bg-gray-50">
                  <button
                    type="button"
                    onClick={handleDecrease}
                    className="flex h-10 w-10 items-center justify-center text-gray-500 transition hover:bg-gray-100 disabled:cursor-not-allowed disabled:text-gray-300"
                    disabled={quantity <= 1 || Boolean(session)}
                    aria-label="수량 감소"
                  >
                    <Minus className="h-4 w-4" />
                  </button>
                  <span className="min-w-[2.5rem] text-center text-lg font-semibold text-gray-900">
                    {quantity}
                  </span>
                  <button
                    type="button"
                    onClick={handleIncrease}
                    className="flex h-10 w-10 items-center justify-center text-gray-500 transition hover:bg-gray-100 disabled:cursor-not-allowed disabled:text-gray-300"
                    disabled={quantity >= maxQuantity || Boolean(session)}
                    aria-label="수량 증가"
                  >
                    <Plus className="h-4 w-4" />
                  </button>
                </div>
              </div>
            </div>

            <div className="space-y-3">
              <span className="text-sm font-medium text-gray-700">
                결제 수단 선택
              </span>
              <div className="grid gap-3 sm:grid-cols-2">
                {PROVIDERS.map((option) => (
                  <button
                    key={option.id}
                    type="button"
                    onClick={() => {
                      setSelectedProvider(option.id);
                      resetSession();
                    }}
                    className={cn(
                      "rounded-2xl border p-4 text-left transition",
                      selectedProvider === option.id
                        ? "border-orange-400 bg-orange-50 shadow-sm"
                        : "border-gray-200 bg-white hover:border-gray-300 hover:bg-gray-50",
                      session ? "opacity-70" : "",
                    )}
                    disabled={Boolean(session)}
                  >
                    <div className="flex items-center justify-between">
                      <div>
                        <div className="text-base font-semibold text-gray-900">
                          {option.label}
                        </div>
                        <p className="mt-1 text-sm text-gray-500">
                          {option.description}
                        </p>
                      </div>
                      <CreditCard className="h-6 w-6 text-orange-500" />
                    </div>
                  </button>
                ))}
              </div>
            </div>

            <div className="space-y-4">
              <span className="text-sm font-medium text-gray-700">
                구매자 연락처
              </span>
              <div className="grid gap-3 sm:grid-cols-2">
                <label className="flex flex-col gap-2 text-sm">
                  <span className="flex items-center gap-2 text-gray-600">
                    <Smartphone className="h-4 w-4" />
                    연락처 (선택)
                  </span>
                  <input
                    type="tel"
                    value={buyerPhone}
                    onChange={(event) => setBuyerPhone(event.target.value)}
                    placeholder="01012345678"
                    className="rounded-xl border border-gray-200 px-4 py-2.5 text-sm text-gray-800 placeholder:text-gray-400 focus:border-orange-400 focus:outline-none focus:ring-2 focus:ring-orange-100"
                    disabled={Boolean(session)}
                  />
                </label>
                <label className="flex flex-col gap-2 text-sm">
                  <span className="flex items-center gap-2 text-gray-600">
                    <Phone className="h-4 w-4" />
                    주문자명 (선택)
                  </span>
                  <input
                    type="text"
                    value={buyerName}
                    onChange={(event) => setBuyerName(event.target.value)}
                    placeholder="홍길동"
                    className="rounded-xl border border-gray-200 px-4 py-2.5 text-sm text-gray-800 placeholder:text-gray-400 focus:border-orange-400 focus:outline-none focus:ring-2 focus:ring-orange-100"
                    disabled={Boolean(session)}
                  />
                </label>
              </div>
            </div>

            <div className="space-y-2 rounded-2xl border border-orange-100 bg-orange-50 p-4 text-sm text-orange-700">
              <div className="flex items-center gap-2 font-medium">
                <Smartphone className="h-4 w-4" />
                모바일 결제 안내
              </div>
              <p className="leading-relaxed">
                스마트폰에서는 해당 버튼을 터치하면 선택한 결제 앱으로 자동
                연결됩니다. 데스크탑에서는 QR을 통해 휴대폰으로 결제를 완료하면
                이 화면에서 상태가 실시간으로 업데이트됩니다.
              </p>
            </div>

            <button
              type="button"
              onClick={startCheckout}
              className="flex h-12 w-full items-center justify-center rounded-full bg-orange-500 text-sm font-semibold text-white transition hover:bg-orange-600 disabled:cursor-not-allowed disabled:bg-orange-300"
              disabled={isSubmitting || Boolean(session)}
            >
              {isSubmitting ? (
                <Loader2 className="h-5 w-5 animate-spin" />
              ) : (
                "선택한 수단으로 주문 연동하기"
              )}
            </button>

            {error ? (
              <div className="flex items-center gap-2 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                <AlertCircle className="h-4 w-4" />
                <span>{error}</span>
              </div>
            ) : null}
          </section>

          <aside className="space-y-6">
            <StatusPanel
              session={session}
              status={status}
              statusError={statusError}
              isMobile={isMobile}
              qrImage={qrImage}
              onCopyLink={handleCopyLink}
              copyFeedback={copyFeedback}
              sessionLink={sessionLink}
              currentStatus={currentStatus}
              onMockConfirm={handleMockConfirm}
              canConfirm={canMockConfirm}
              isConfirming={isConfirming}
              confirmFeedback={confirmFeedback}
            />
          </aside>
        </div>
      </main>
    </div>
  );
}

function StatusPanel({
  session,
  status,
  statusError,
  isMobile,
  qrImage,
  onCopyLink,
  copyFeedback,
  sessionLink,
  currentStatus,
  onMockConfirm,
  canConfirm,
  isConfirming,
  confirmFeedback,
}: {
  session: PaymentSession | null;
  status: OrderStatusPayload | null;
  statusError: string | null;
  isMobile: boolean;
  qrImage: string | null;
  onCopyLink: () => void;
  copyFeedback: string | null;
  sessionLink: string | null | undefined;
  currentStatus: string;
  onMockConfirm: () => void;
  canConfirm: boolean;
  isConfirming: boolean;
  confirmFeedback: string | null;
}) {
  if (!session) {
    return (
      <div className="rounded-3xl bg-white p-8 shadow-sm">
        <div className="flex flex-col items-start gap-4">
          <h2 className="text-lg font-semibold text-gray-900">
            주문 연동 안내
          </h2>
          <div className="space-y-3 text-sm leading-relaxed text-gray-600">
            <p className="flex items-start gap-3">
              <Smartphone className="mt-0.5 h-4 w-4 text-orange-500" />
              <span>
                스마트폰에서는 결제 버튼 터치 시 토스/카카오페이 앱으로 자동
                이동합니다.
              </span>
            </p>
            <p className="flex items-start gap-3">
              <QrCode className="mt-0.5 h-4 w-4 text-orange-500" />
              <span>
                데스크탑에서는 QR을 휴대폰으로 스캔하면 동일한 결제 화면으로
                이동합니다.
              </span>
            </p>
            <p className="flex items-start gap-3">
              <CreditCard className="mt-0.5 h-4 w-4 text-orange-500" />
              <span>
                결제 완료 후 이 화면이 자동 갱신되어 주문 완료 여부를
                표시합니다.
              </span>
            </p>
          </div>
        </div>
      </div>
    );
  }

  const isFinal = FINAL_STATUSES.has(currentStatus);

  return (
    <div className="space-y-4">
      {!isMobile ? (
        <div className="rounded-3xl bg-white p-8 shadow-sm">
          <div className="flex flex-col items-center gap-6 text-center">
            <div className="flex flex-col items-center gap-2 text-gray-900">
              <span className="text-sm font-medium text-orange-500">
                휴대폰으로 결제 진행
              </span>
              <h2 className="text-xl font-semibold">
                QR 스캔으로 토스/카카오페이 실행
              </h2>
            </div>
            {qrImage ? (
              /* eslint-disable-next-line @next/next/no-img-element */
              <img
                src={qrImage}
                alt="결제 QR"
                className="h-56 w-56 rounded-3xl border border-gray-200 object-contain bg-white p-4 shadow-md"
              />
            ) : (
              <div className="flex h-56 w-56 items-center justify-center rounded-3xl border border-dashed border-gray-300 bg-gray-50 text-sm text-gray-400">
                QR 이미지를 준비 중입니다.
              </div>
            )}
            <button
              type="button"
              onClick={onCopyLink}
              className="flex items-center gap-2 rounded-full border border-gray-200 px-4 py-2 text-sm text-gray-700 transition hover:border-gray-300 hover:bg-gray-50"
            >
              <Copy className="h-4 w-4" />
              결제 링크 복사
            </button>
            {copyFeedback ? (
              <p className="text-xs text-gray-500">{copyFeedback}</p>
            ) : null}
            {canConfirm ? (
              <button
                type="button"
                onClick={onMockConfirm}
                className="flex w-full items-center justify-center gap-2 rounded-full bg-gray-900 px-4 py-2 text-sm font-semibold text-white transition hover:bg-gray-800 disabled:cursor-not-allowed disabled:opacity-80"
                disabled={isConfirming}
              >
                {isConfirming ? (
                  <Loader2 className="h-4 w-4 animate-spin" />
                ) : null}
                임시 주문 확정
              </button>
            ) : null}
            {confirmFeedback ? (
              <p className="text-xs text-green-600">{confirmFeedback}</p>
            ) : null}
          </div>
        </div>
      ) : (
        <div className="rounded-3xl bg-white p-6 shadow-sm">
          <div className="flex items-start gap-3 text-sm text-gray-700">
            <Smartphone className="h-5 w-5 text-orange-500" />
            <p>
              결제 앱으로 이동 중입니다. 화면이 전환되지 않으면 아래 버튼으로
              다시 이동해주세요.
            </p>
          </div>
          <button
            type="button"
            onClick={() => {
              if (sessionLink) {
                window.location.href = sessionLink;
              }
            }}
            className="mt-4 flex w-full items-center justify-center gap-2 rounded-full border border-orange-200 bg-orange-50 px-4 py-2 text-sm font-medium text-orange-600 transition hover:bg-orange-100"
          >
            <ExternalLink className="h-4 w-4" />
            결제 페이지로 이동
          </button>
          {canConfirm ? (
            <button
              type="button"
              onClick={onMockConfirm}
              className="mt-3 flex w-full items-center justify-center gap-2 rounded-full bg-gray-900 px-4 py-2 text-sm font-semibold text-white transition hover:bg-gray-800 disabled:cursor-not-allowed disabled:opacity-80"
              disabled={isConfirming}
            >
              {isConfirming ? (
                <Loader2 className="h-4 w-4 animate-spin" />
              ) : null}
              임시 주문 확정
            </button>
          ) : null}
          {confirmFeedback ? (
            <p className="mt-2 text-xs text-green-600">{confirmFeedback}</p>
          ) : null}
        </div>
      )}

      <div className="rounded-3xl bg-white p-8 shadow-sm">
        <div className="flex items-center gap-3">
          <div className="flex h-12 w-12 items-center justify-center rounded-full bg-orange-100 text-orange-600">
            <Loader2
              className={cn("h-6 w-6", isFinal ? "hidden" : "animate-spin")}
            />
            {isFinal ? <CheckCircle className="h-6 w-6" /> : null}
          </div>
          <div>
            <h3 className="text-lg font-semibold text-gray-900">
              결제 상태 확인 중
            </h3>
            <p className="text-sm text-gray-500">
              주문번호 {session.orderId} •{" "}
              {session.provider === "toss" ? "토스페이" : "카카오페이"}
            </p>
          </div>
        </div>

        <div className="mt-6 space-y-2 text-sm">
          <StatusBadge status={currentStatus} />
          {status?.updatedAt ? (
            <p className="text-xs text-gray-400">
              마지막 갱신 {formatDate(status.updatedAt)}
            </p>
          ) : null}
          {statusError ? (
            <p className="flex items-center gap-2 text-xs text-red-500">
              <AlertCircle className="h-3.5 w-3.5" />
              {statusError}
            </p>
          ) : null}
        </div>
      </div>
    </div>
  );
}

function StatusBadge({ status }: { status: string }) {
  switch (status) {
    case "COMPLETED":
      return (
        <div className="flex items-center gap-2 rounded-full bg-green-50 px-3 py-1 text-xs font-medium text-green-600">
          <CheckCircle className="h-4 w-4" />
          결제가 완료되었습니다.
        </div>
      );
    case "FAILED":
      return (
        <div className="flex items-center gap-2 rounded-full bg-red-50 px-3 py-1 text-xs font-medium text-red-600">
          <AlertCircle className="h-4 w-4" />
          결제에 실패했습니다. 다시 시도해주세요.
        </div>
      );
    case "CANCELED":
      return (
        <div className="flex items-center gap-2 rounded-full bg-gray-100 px-3 py-1 text-xs font-medium text-gray-600">
          <AlertCircle className="h-4 w-4" />
          사용자가 결제를 취소했습니다.
        </div>
      );
    default:
      return (
        <div className="flex items-center gap-2 rounded-full bg-blue-50 px-3 py-1 text-xs font-medium text-blue-600">
          <Loader2 className="h-4 w-4 animate-spin" />
          결제 진행 중입니다. 휴대폰에서 완료해주세요.
        </div>
      );
  }
}

function clampQuantity(value: number): number {
  if (!Number.isFinite(value)) return 1;
  if (value <= 0) return 1;
  return Math.min(99, Math.floor(value));
}

function normalizeProvider(value?: string | null): PaymentProvider | null {
  if (!value) return null;
  if (value === "toss" || value === "kakaopay") return value;
  return null;
}

function checkIsMobile(): boolean {
  if (typeof window === "undefined") return false;
  const agent = window.navigator?.userAgent ?? "";
  return /Android|iPhone|iPad|iPod|Windows Phone/i.test(agent);
}

function formatCurrency(value: number): string {
  return new Intl.NumberFormat("ko-KR").format(value);
}

function formatDate(value: string): string {
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  const pad = (num: number) => num.toString().padStart(2, "0");
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`;
}
