export const runtime = "nodejs";

import { NextResponse } from "next/server";
import { PaymentDevice, PaymentProvider, PaymentSession } from "@/types/mall";
import { headers } from "next/headers";
import { randomUUID } from "node:crypto";
import { isMockMode, saveMockSession } from "../_shared";

type CreatePaymentSessionRequest = {
  provider: PaymentProvider;
  productId: string;
  productName: string;
  quantity: number;
  amount: number;
  buyerName?: string;
  buyerEmail?: string;
  buyerPhone?: string;
  device?: PaymentDevice;
  successPath?: string;
  cancelPath?: string;
  failPath?: string;
  orderId?: string;
};

type KakaoReadyResponse = {
  tid: string;
  next_redirect_app_url?: string;
  next_redirect_mobile_url?: string;
  next_redirect_pc_url?: string;
  created_at?: string;
};

type TossPaymentLinkResponse = {
  paymentLinkId: string;
  orderId: string;
  status: string;
  amount: number;
  mobileUrl?: string;
  webUrl?: string;
  appScheme?: string;
  expiresAt?: string;
};

function resolveBaseUrl(): string {
  const explicit =
    process.env.PAYMENTS_RETURN_URL_BASE ?? process.env.NEXT_PUBLIC_BASE_URL;
  if (explicit) return explicit.replace(/\/$/, "");
  const headerList = headers();
  const origin = headerList.get("origin") ?? headerList.get("referer");
  if (origin) return origin.replace(/\/$/, "");
  return "";
}

function buildUrl(base: string, path?: string): string {
  if (!path) return base;
  if (path.startsWith("http")) return path;
  if (!base) return path;
  const normalized = path.startsWith("/") ? path : `/${path}`;
  return `${base}${normalized}`;
}

function ensurePositiveInt(value: number): number {
  if (!Number.isFinite(value)) return 0;
  if (value < 0) return 0;
  return Math.round(value);
}

export async function POST(request: Request) {
  let payload: CreatePaymentSessionRequest;
  try {
    payload = (await request.json()) as CreatePaymentSessionRequest;
  } catch {
    return NextResponse.json(
      { success: false, error: { message: "잘못된 요청 본문입니다." } },
      { status: 400 },
    );
  }

  const provider = payload.provider;
  if (provider !== "kakaopay" && provider !== "toss") {
    return NextResponse.json(
      { success: false, error: { message: "지원하지 않는 결제 수단입니다." } },
      { status: 400 },
    );
  }

  const productId = payload.productId?.trim();
  const productName = payload.productName?.trim();
  const quantity = ensurePositiveInt(payload.quantity);
  const amount = ensurePositiveInt(payload.amount);

  if (!productId || !productName || quantity <= 0 || amount <= 0) {
    return NextResponse.json(
      { success: false, error: { message: "상품 정보가 올바르지 않습니다." } },
      { status: 400 },
    );
  }

  const baseUrl = resolveBaseUrl();
  const successUrl = buildUrl(
    baseUrl,
    payload.successPath ?? "/mall/orders/success",
  );
  const cancelUrl = buildUrl(
    baseUrl,
    payload.cancelPath ?? "/mall/orders/cancel",
  );
  const failUrl = buildUrl(baseUrl, payload.failPath ?? "/mall/orders/fail");

  const orderId =
    payload.orderId ??
    `ORD-${Date.now()}-${Math.random().toString(36).slice(2, 8).toUpperCase()}`;

  const referenceId = randomUUID();
  const device: PaymentDevice =
    payload.device === "mobile" || payload.device === "desktop"
      ? payload.device
      : "desktop";

  if (isMockMode()) {
    const mockMobileUrl = `${baseUrl || "https://example.com"}/mock/pay/${referenceId}`;
    const session: PaymentSession = {
      orderId,
      provider,
      productId,
      productName,
      quantity,
      amount,
      currency: "KRW",
      referenceId,
      mobileUrl: mockMobileUrl,
      pcUrl: mockMobileUrl,
      appScheme: mockMobileUrl,
      qrPayload: mockMobileUrl,
      expiresAt: new Date(Date.now() + 10 * 60 * 1000).toISOString(),
      statusEndpoint: `/api/payments/status?provider=${provider}&orderId=${orderId}&reference=${referenceId}`,
    };
    saveMockSession(orderId, session);
    return NextResponse.json({ success: true, session });
  }

  try {
    let session: PaymentSession;

    if (provider === "kakaopay") {
      session = await createKakaoPaySession({
        orderId,
        productId,
        productName,
        quantity,
        amount,
        buyerName: payload.buyerName,
        buyerPhone: payload.buyerPhone,
        successUrl,
        cancelUrl,
        failUrl,
        device,
        referenceId,
      });
    } else {
      session = await createTossPaymentSession({
        orderId,
        productId,
        productName,
        quantity,
        amount,
        successUrl,
        failUrl,
        device,
        referenceId,
      });
    }

    return NextResponse.json({ success: true, session });
  } catch (error) {
    console.error("결제 세션 생성 실패", error);
    return NextResponse.json(
      {
        success: false,
        error: {
          message:
            error instanceof Error
              ? error.message
              : "결제 세션 생성 중 오류가 발생했습니다.",
        },
      },
      { status: 500 },
    );
  }
}

async function createKakaoPaySession(params: {
  orderId: string;
  productId: string;
  productName: string;
  quantity: number;
  amount: number;
  buyerName?: string;
  buyerPhone?: string;
  successUrl: string;
  cancelUrl: string;
  failUrl: string;
  device: PaymentDevice;
  referenceId: string;
}): Promise<PaymentSession> {
  const adminKey = process.env.KAKAOPAY_ADMIN_KEY;
  const cid = process.env.KAKAOPAY_CID ?? "TC0ONETIME";

  if (!adminKey) {
    throw new Error("카카오페이 Admin Key가 설정되지 않았습니다.");
  }

  const form = new URLSearchParams();
  form.append("cid", cid);
  form.append("partner_order_id", params.orderId);
  form.append("partner_user_id", params.buyerName || "GUEST");
  form.append("item_name", params.productName);
  form.append("quantity", String(params.quantity));
  form.append("total_amount", String(params.amount));
  form.append("tax_free_amount", "0");
  form.append("approval_url", params.successUrl);
  form.append("cancel_url", params.cancelUrl);
  form.append("fail_url", params.failUrl);

  if (params.buyerPhone) {
    form.append("phone_number", params.buyerPhone);
  }

  const response = await fetch("https://kapi.kakao.com/v1/payment/ready", {
    method: "POST",
    headers: {
      Authorization: `KakaoAK ${adminKey}`,
      "Content-Type": "application/x-www-form-urlencoded;charset=utf-8",
    },
    body: form.toString(),
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(`카카오페이 결제 준비 실패: ${text}`);
  }

  const data = (await response.json()) as KakaoReadyResponse;

  return {
    orderId: params.orderId,
    provider: "kakaopay",
    productId: params.productId,
    productName: params.productName,
    quantity: params.quantity,
    amount: params.amount,
    currency: "KRW",
    referenceId: data.tid,
    mobileUrl: data.next_redirect_mobile_url ?? undefined,
    pcUrl: data.next_redirect_pc_url ?? undefined,
    appScheme: data.next_redirect_app_url ?? undefined,
    qrPayload:
      params.device === "desktop"
        ? (data.next_redirect_mobile_url ?? data.next_redirect_pc_url)
        : (data.next_redirect_app_url ?? data.next_redirect_mobile_url),
    expiresAt: data.created_at ?? undefined,
    statusEndpoint: `/api/payments/status?provider=kakaopay&orderId=${params.orderId}&reference=${encodeURIComponent(data.tid)}`,
    tid: data.tid,
  };
}

async function createTossPaymentSession(params: {
  orderId: string;
  productId: string;
  productName: string;
  quantity: number;
  amount: number;
  successUrl: string;
  failUrl: string;
  device: PaymentDevice;
  referenceId: string;
}): Promise<PaymentSession> {
  const secretKey = process.env.TOSS_SECRET_KEY;
  if (!secretKey) {
    throw new Error("토스페이먼츠 Secret Key가 설정되지 않았습니다.");
  }

  const auth = Buffer.from(`${secretKey}:`).toString("base64");

  const response = await fetch(
    "https://api.tosspayments.com/v1/payment-links",
    {
      method: "POST",
      headers: {
        Authorization: `Basic ${auth}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        orderId: params.orderId,
        orderName: params.productName,
        amount: params.amount,
        currency: "KRW",
        description: `상품 ${params.productId}`,
        successUrl: params.successUrl,
        failUrl: params.failUrl,
      }),
    },
  );

  if (!response.ok) {
    const text = await response.text();
    throw new Error(`토스페이먼츠 결제 링크 생성 실패: ${text}`);
  }

  const data = (await response.json()) as TossPaymentLinkResponse;

  return {
    orderId: params.orderId,
    provider: "toss",
    productId: params.productId,
    productName: params.productName,
    quantity: params.quantity,
    amount: params.amount,
    currency: "KRW",
    referenceId: data.paymentLinkId,
    mobileUrl: data.mobileUrl ?? data.webUrl ?? undefined,
    pcUrl: data.webUrl ?? data.mobileUrl ?? undefined,
    appScheme: data.appScheme ?? data.mobileUrl ?? undefined,
    qrPayload:
      params.device === "desktop"
        ? (data.mobileUrl ?? data.webUrl)
        : (data.appScheme ?? data.mobileUrl),
    expiresAt: data.expiresAt ?? undefined,
    statusEndpoint: `/api/payments/status?provider=toss&orderId=${params.orderId}&reference=${encodeURIComponent(data.paymentLinkId)}`,
    paymentKey: data.paymentLinkId,
  };
}
