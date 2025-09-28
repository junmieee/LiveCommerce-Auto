"use client";

import { useMemo, useState } from "react";
import CustomerHeader from "@/components/headers/CustomerHeader";
import { Minus, Plus, ShoppingCart, Zap } from "lucide-react";

type ProductDetail = {
  id: string;
  name: string;
  price: number;
  description?: string | null;
  sellerName?: string | null;
  sellerCode?: string | null;
  shippingFee?: number | null;
  shippingInfo?: string | null;
  stockQuantity?: number | null;
  imageUrl?: string | null;
  gallery?: string[];
  badges?: string[];
  createdAt?: string | null;
};

type Props = {
  detail: ProductDetail;
  fallbackImage?: string;
};

const DEFAULT_IMAGE =
  "https://images.unsplash.com/photo-1542291026-7eec264c27ff?q=80&w=800&auto=format&fit=crop";

export default function ProductDetailView({
  detail,
  fallbackImage = DEFAULT_IMAGE,
}: Props) {
  const [quantity, setQuantity] = useState(1);
  const [active, setActive] = useState(0);

  const images = useMemo(() => {
    const list = detail.gallery?.length ? detail.gallery : [];
    if (list.length > 0) return list;
    if (detail.imageUrl) return [detail.imageUrl];
    return [fallbackImage];
  }, [detail.gallery, detail.imageUrl, fallbackImage]);

  const activeImage = images[active] ?? images[0];

  const price = detail.price ?? 0;
  const formattedPrice = new Intl.NumberFormat("ko-KR").format(price);
  const shippingFee = detail.shippingFee ?? 3000;
  const formattedShipping =
    shippingFee === 0
      ? "무료"
      : `${new Intl.NumberFormat("ko-KR").format(shippingFee)}원`;
  const stock = detail.stockQuantity ?? undefined;
  const maxQuantity = stock && stock > 0 ? stock : 99;

  const decrease = () => setQuantity((prev) => Math.max(1, prev - 1));
  const increase = () =>
    setQuantity((prev) => (prev >= maxQuantity ? prev : prev + 1));

  const handleAddToCart = () => {
    // TODO: 장바구니 연동 시 교체
    console.log("add-to-cart", { productId: detail.id, quantity });
  };

  const handleBuyNow = () => {
    // TODO: 결제 플로우 연결
    console.log("buy-now", { productId: detail.id, quantity });
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <CustomerHeader title={detail.sellerName ?? "스토어"} />
      <main className="mx-auto w-full max-w-6xl px-4 pb-20 pt-24">
        <div className="grid gap-10 lg:grid-cols-[1.1fr,0.9fr]">
          <div className="space-y-6">
            <div className="overflow-hidden rounded-3xl bg-white shadow-sm">
              <div className="relative aspect-square bg-gray-100">
                {/* eslint-disable-next-line @next/next/no-img-element */}
                <img
                  src={activeImage}
                  alt={detail.name}
                  className="h-full w-full object-cover"
                />
              </div>
            </div>

            {images.length > 1 && (
              <div className="flex gap-3 overflow-x-auto pb-1">
                {images.map((img, idx) => (
                  <button
                    key={img + idx}
                    type="button"
                    className={`relative h-20 w-20 shrink-0 overflow-hidden rounded-xl border transition ${
                      active === idx
                        ? "border-orange-500 ring-2 ring-orange-200"
                        : "border-transparent"
                    }`}
                    onClick={() => setActive(idx)}
                  >
                    {/* eslint-disable-next-line @next/next/no-img-element */}
                    <img
                      src={img}
                      alt={`${detail.name} ${idx + 1}`}
                      className="h-full w-full object-cover"
                    />
                  </button>
                ))}
              </div>
            )}
          </div>

          <div className="space-y-8">
            <section className="rounded-3xl bg-white p-8 shadow-sm">
              <div className="flex items-center justify-between gap-4">
                <div className="flex flex-col gap-2">
                  {detail.badges?.length ? (
                    <div className="flex flex-wrap gap-2 text-xs font-medium text-orange-600">
                      {detail.badges.map((badge) => (
                        <span
                          key={badge}
                          className="rounded-full bg-orange-50 px-3 py-1"
                        >
                          {badge}
                        </span>
                      ))}
                    </div>
                  ) : null}
                  <h1 className="text-2xl font-semibold text-gray-900 sm:text-3xl">
                    {detail.name}
                  </h1>
                  {detail.sellerName ? (
                    <p className="text-sm text-gray-500">
                      {detail.sellerName}의 스토어
                    </p>
                  ) : null}
                </div>
                <div className="hidden text-right sm:flex sm:flex-col sm:items-end">
                  <span className="text-sm text-gray-500">상품 코드</span>
                  <span className="font-medium text-gray-800">
                    {detail.sellerCode ?? `P-${detail.id}`}
                  </span>
                </div>
              </div>

              <div className="mt-8 flex flex-col gap-6">
                <div className="text-3xl font-bold text-gray-900">
                  {formattedPrice}원
                </div>

                <dl className="grid gap-4 text-sm text-gray-600">
                  <div className="flex items-center justify-between">
                    <dt className="text-gray-500">배송비</dt>
                    <dd>
                      <span className="font-medium text-gray-800">
                        {formattedShipping}
                      </span>
                      {shippingFee !== 0 ? (
                        <span className="ml-2 text-xs text-gray-400">
                          (3만원 이상 무료)
                        </span>
                      ) : null}
                    </dd>
                  </div>
                  <div className="flex items-start justify-between">
                    <dt className="text-gray-500">배송 안내</dt>
                    <dd className="max-w-[70%] text-right leading-relaxed text-gray-700">
                      {detail.shippingInfo ??
                        "평일 오후 2시 이전 결제 시 당일 출고됩니다."}
                    </dd>
                  </div>
                  {typeof stock === "number" ? (
                    <div className="flex items-center justify-between">
                      <dt className="text-gray-500">재고</dt>
                      <dd className="font-medium text-gray-800">
                        {stock.toLocaleString()}개 남음
                      </dd>
                    </div>
                  ) : null}
                </dl>

                <div className="flex flex-col gap-3">
                  <span className="text-sm font-medium text-gray-700">
                    수량
                  </span>
                  <div className="flex items-center gap-4">
                    <div className="flex items-center overflow-hidden rounded-full border border-gray-200 bg-gray-50">
                      <button
                        type="button"
                        onClick={decrease}
                        className="flex h-10 w-10 items-center justify-center text-gray-500 transition hover:bg-gray-100 disabled:cursor-not-allowed disabled:text-gray-300"
                        disabled={quantity <= 1}
                        aria-label="수량 감소"
                      >
                        <Minus className="h-4 w-4" />
                      </button>
                      <span className="min-w-[2.5rem] text-center text-lg font-semibold text-gray-900">
                        {quantity}
                      </span>
                      <button
                        type="button"
                        onClick={increase}
                        className="flex h-10 w-10 items-center justify-center text-gray-500 transition hover:bg-gray-100 disabled:cursor-not-allowed disabled:text-gray-300"
                        disabled={quantity >= maxQuantity}
                        aria-label="수량 증가"
                      >
                        <Plus className="h-4 w-4" />
                      </button>
                    </div>
                    {stock ? (
                      <span className="text-xs text-gray-500">
                        최대 {maxQuantity.toLocaleString()}개까지 구매
                        가능합니다.
                      </span>
                    ) : null}
                  </div>
                </div>

                <div className="mt-4 grid gap-3 sm:grid-cols-[1fr,1fr]">
                  <button
                    type="button"
                    onClick={handleAddToCart}
                    className="flex h-12 items-center justify-center gap-2 rounded-full border border-gray-200 bg-white text-sm font-medium text-gray-700 transition hover:border-gray-300 hover:bg-gray-50"
                  >
                    <ShoppingCart className="h-4 w-4" />
                    장바구니 담기
                  </button>
                  <button
                    type="button"
                    onClick={handleBuyNow}
                    className="flex h-12 items-center justify-center gap-2 rounded-full bg-orange-500 text-sm font-semibold text-white transition hover:bg-orange-600"
                  >
                    <Zap className="h-4 w-4" />
                    바로 구매하기
                  </button>
                </div>
              </div>
            </section>

            <section className="rounded-3xl bg-white p-8 shadow-sm">
              <h2 className="text-lg font-semibold text-gray-900">상품 설명</h2>
              <p className="mt-4 whitespace-pre-line text-sm leading-relaxed text-gray-700">
                {detail.description?.trim() ||
                  "판매자가 곧 상품 설명을 업데이트할 예정입니다."}
              </p>
            </section>

            <section className="rounded-3xl bg-white p-8 shadow-sm">
              <h2 className="text-lg font-semibold text-gray-900">기본 정보</h2>
              <dl className="mt-4 grid gap-3 text-sm text-gray-600 sm:grid-cols-2">
                <div className="flex items-center justify-between gap-3">
                  <dt className="text-gray-500">상품 코드</dt>
                  <dd className="font-medium text-gray-800">
                    {detail.sellerCode ?? `P-${detail.id}`}
                  </dd>
                </div>
                {detail.createdAt ? (
                  <div className="flex items-center justify-between gap-3">
                    <dt className="text-gray-500">등록일</dt>
                    <dd className="font-medium text-gray-800">
                      {new Date(detail.createdAt).toLocaleDateString("ko-KR")}
                    </dd>
                  </div>
                ) : null}
                <div className="flex items-center justify-between gap-3">
                  <dt className="text-gray-500">판매자</dt>
                  <dd className="font-medium text-gray-800">
                    {detail.sellerName ?? "판매자 정보 미확인"}
                  </dd>
                </div>
                <div className="flex items-center justify-between gap-3">
                  <dt className="text-gray-500">교환/반품</dt>
                  <dd className="max-w-[70%] text-right text-sm text-gray-700">
                    {"상품 수령 후 7일 이내 고객센터로 문의해주세요."}
                  </dd>
                </div>
              </dl>
            </section>
          </div>
        </div>
      </main>
    </div>
  );
}
