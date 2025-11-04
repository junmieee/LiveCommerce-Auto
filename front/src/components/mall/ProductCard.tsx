"use client";

import Link from "next/link";

type Props = {
  id: string;
  name: string;
  price: number;
  imageUrl?: string | null;
  href?: string;
  orderHref?: string;
  onAddToCart?: (id: string) => void;
};

const PLACEHOLDER_IMAGE =
  "https://images.unsplash.com/photo-1512436991641-6745cdb1723f?q=80&w=800&auto=format&fit=crop";

export default function ProductCard({
  id,
  name,
  price,
  imageUrl,
  href,
  orderHref,
  onAddToCart,
}: Props) {
  const detailHref = href ?? `/mall/products/${id}`;
  const checkoutHref =
    orderHref ?? `/mall/orders/new?productId=${encodeURIComponent(id)}`;

  const displayImage =
    imageUrl && imageUrl.trim().length > 0 ? imageUrl : PLACEHOLDER_IMAGE;

  return (
    <div className="flex flex-col rounded-2xl bg-white p-4 shadow-sm transition hover:-translate-y-1 hover:shadow-md">
      <Link href={detailHref} className="group block">
        <div className="aspect-[4/5] w-full overflow-hidden rounded-xl bg-gray-100">
          {/* eslint-disable-next-line @next/next/no-img-element */}
          <img
            src={displayImage}
            alt={name}
            className="h-full w-full object-cover transition duration-300 group-hover:scale-105"
          />
        </div>
        <div className="mt-4">
          <div className="text-base font-medium text-gray-800">{name}</div>
          <div className="mt-1 text-xs text-orange-500">자세히 보기 &gt;</div>
          <div className="mt-2 text-lg font-semibold text-gray-900">
            {price.toLocaleString()}원
          </div>
        </div>
      </Link>
      <div className="mt-4 flex gap-2">
        <Link
          href={checkoutHref}
          className="flex h-10 flex-1 items-center justify-center rounded-full border border-orange-200 bg-orange-50 text-sm font-medium text-orange-600 transition hover:bg-orange-100"
        >
          주문하기
        </Link>
        <button
          className="flex h-10 flex-1 items-center justify-center rounded-full bg-[#F7A072] text-sm font-medium text-white transition hover:bg-orange-500"
          onClick={() => onAddToCart?.(id)}
          type="button"
        >
          장바구니
        </button>
      </div>
    </div>
  );
}
