"use client";

import Link from "next/link";

type Props = {
  id: string;
  name: string;
  price: number;
  imageUrl: string;
  href?: string;
  onAddToCart?: (id: string) => void;
};

export default function ProductCard({
  id,
  name,
  price,
  imageUrl,
  href,
  onAddToCart,
}: Props) {
  const detailHref = href ?? `/mall/products/${id}`;

  return (
    <div className="flex flex-col rounded-2xl bg-white p-4 shadow-sm transition hover:-translate-y-1 hover:shadow-md">
      <Link href={detailHref} className="group block">
        <div className="aspect-[4/5] w-full overflow-hidden rounded-xl bg-gray-100">
          {/* eslint-disable-next-line @next/next/no-img-element */}
          <img
            src={imageUrl}
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
      <button
        className="mt-4 flex h-10 items-center justify-center rounded-full bg-[#F7A072] text-sm font-medium text-white transition hover:bg-orange-500"
        onClick={() => onAddToCart?.(id)}
        type="button"
      >
        장바구니 담기
      </button>
    </div>
  );
}
