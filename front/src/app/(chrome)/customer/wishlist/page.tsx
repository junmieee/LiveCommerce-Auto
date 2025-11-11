"use client";

import { Heart, Search } from "lucide-react";
import { useMemo, useState } from "react";

type WishItem = {
  id: string;
  shopName: string;
  name: string;
  price: number; // KRW
  shippingFee: number; // KRW
  imageUrl: string;
  inStock: boolean;
  liked: boolean;
  lowStockNote?: string; // e.g., "*5개이하"
};

function formatKRW(n: number) {
  return new Intl.NumberFormat("ko-KR").format(n) + "원";
}

export default function WishlistPage() {
  const [query, setQuery] = useState("");
  const [items, setItems] = useState<WishItem[]>([
    {
      id: "1",
      shopName: "ㅇㅇ샵",
      name: "푸드 스웨트셔츠",
      price: 10000,
      shippingFee: 2000,
      imageUrl:
        "https://images.unsplash.com/photo-1512436991641-6745cdb1723f?q=80&w=320&auto=format&fit=crop",
      inStock: true,
      liked: true,
      lowStockNote: "*5개이하",
    },
    {
      id: "2",
      shopName: "ㅇㅇ샵",
      name: "너무 비싼 놈",
      price: 10000,
      shippingFee: 2000,
      imageUrl:
        "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?q=80&w=320&auto=format&fit=crop",
      inStock: false,
      liked: true,
    },
    {
      id: "3",
      shopName: "ㅇㅇ샵",
      name: "너무 비싼 놈",
      price: 10000,
      shippingFee: 2000,
      imageUrl:
        "https://images.unsplash.com/photo-1518779578993-ec3579fee39f?q=80&w=320&auto=format&fit=crop",
      inStock: true,
      liked: false,
    },
  ]);

  const filtered = useMemo(() => {
    const q = query.trim();
    if (!q) return items;
    return items.filter((it) => it.name.includes(q) || it.shopName.includes(q));
  }, [query, items]);

  const toggleLike = (id: string) => {
    setItems((prev) =>
      prev.map((it) => (it.id === id ? { ...it, liked: !it.liked } : it)),
    );
  };

  const addToCart = (id: string) => {
    // TODO: implement cart add API
    console.log("장바구니 담기:", id);
  };

  const removeSelected = () => {
    // TODO: implement batch remove API
    console.log("선택상품 삭제");
  };

  const removeAll = () => {
    // TODO: implement clear API
    console.log("전체상품 삭제");
  };

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-semibold">찜 상품</h1>

      <div className="relative">
        <input
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          className="w-full rounded-full border border-gray-300 bg-white py-3 pl-12 pr-4 outline-none focus:ring-2 focus:ring-blue-400"
          placeholder="검색하세요"
        />
        <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400" />
      </div>

      <div className="rounded-2xl bg-white px-4 py-3">
        <div className="flex gap-4">
          <button
            onClick={removeSelected}
            className="rounded-xl bg-neutral-100 px-4 py-2 text-gray-700 hover:bg-neutral-200"
          >
            선택상품 삭제
          </button>
          <button
            onClick={removeAll}
            className="rounded-xl bg-neutral-100 px-4 py-2 text-gray-700 hover:bg-neutral-200"
          >
            전체상품 삭제
          </button>
        </div>
      </div>

      <div className="overflow-hidden rounded-2xl bg-white">
        <div className="hidden lg:block">
          <div className="grid [grid-template-columns:36px_1fr_160px_120px_120px_140px_40px] items-center gap-x-8 rounded-full border-b bg-neutral-100 px-5 py-3 text-sm font-medium text-gray-700">
            <div className="flex items-center justify-center">
              <input type="checkbox" aria-label="전체 선택" />
            </div>
            <div>상품정보</div>
            <div className="text-left">재고유무</div>
            <div className="text-center">가격</div>
            <div className="text-center">배송비</div>
            <div />
            <div />
          </div>

          {filtered.map((it) => (
            <div key={it.id} className="border-b px-3 py-2 last:border-b-0">
              <div className="px-3 pb-2 text-sm font-medium text-gray-700">
                {it.shopName}
              </div>
              <div className="grid [grid-template-columns:36px_1fr_160px_120px_120px_140px_40px] items-center gap-x-8 rounded-xl border border-gray-200 bg-white px-5 py-4">
                <div className="flex items-center justify-center">
                  <input type="checkbox" />
                </div>
                <div className="flex min-w-0 items-center gap-3">
                  <div className="h-14 w-14 shrink-0 overflow-hidden rounded bg-gray-100">
                    {/* eslint-disable-next-line @next/next/no-img-element */}
                    <img
                      src={it.imageUrl}
                      alt={it.name}
                      className="h-full w-full object-cover"
                    />
                  </div>
                  <div className="min-w-0">
                    <div className="truncate font-medium">{it.name}</div>
                  </div>
                </div>
                <div className="text-left text-sm">
                  <div>
                    {it.inStock ? (
                      <span className="text-green-600">재고 있음</span>
                    ) : (
                      <span className="text-red-500">재고 없음</span>
                    )}
                  </div>
                  {it.lowStockNote ? (
                    <div className="mt-1 text-xs text-red-500">
                      {it.lowStockNote}
                    </div>
                  ) : null}
                </div>
                <div className="text-center font-medium">
                  {formatKRW(it.price)}
                </div>
                <div className="text-center text-gray-700">
                  {formatKRW(it.shippingFee)}
                </div>
                <div className="flex items-center justify-center">
                  <button
                    onClick={() => addToCart(it.id)}
                    className="rounded-full bg-[#F7A072] px-4 py-2 text-sm text-white hover:bg-orange-500"
                  >
                    장바구니 담기
                  </button>
                </div>
                <div className="flex items-center justify-center">
                  <button
                    onClick={() => toggleLike(it.id)}
                    aria-label="찜 토글"
                  >
                    <Heart
                      size={20}
                      className={it.liked ? "text-red-500" : "text-gray-400"}
                      fill={it.liked ? "currentColor" : "none"}
                    />
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>

        <div className="space-y-4 border-t border-gray-100 p-4 lg:hidden">
          {filtered.map((it) => (
            <div
              key={it.id}
              className="space-y-3 rounded-2xl border border-gray-100 p-4 shadow-sm"
            >
              <div className="flex items-center justify-between text-sm text-gray-700">
                <span className="font-semibold">{it.shopName}</span>
                <button onClick={() => toggleLike(it.id)} aria-label="찜 토글">
                  <Heart
                    size={22}
                    className={it.liked ? "text-red-500" : "text-gray-400"}
                    fill={it.liked ? "currentColor" : "none"}
                  />
                </button>
              </div>
              <div className="flex items-center gap-3">
                <input
                  type="checkbox"
                  className="h-4 w-4 rounded border-gray-300"
                />
                <div className="h-20 w-20 shrink-0 overflow-hidden rounded-xl bg-gray-100">
                  {/* eslint-disable-next-line @next/next/no-img-element */}
                  <img
                    src={it.imageUrl}
                    alt={it.name}
                    className="h-full w-full object-cover"
                  />
                </div>
                <div className="flex-1 space-y-1">
                  <p className="text-base font-semibold text-gray-900">
                    {it.name}
                  </p>
                  <p className="text-sm text-gray-500">
                    {formatKRW(it.price)} · 배송 {formatKRW(it.shippingFee)}
                  </p>
                  <p className="text-xs">
                    {it.inStock ? (
                      <span className="text-green-600">재고 있음</span>
                    ) : (
                      <span className="text-red-500">재고 없음</span>
                    )}
                    {it.lowStockNote ? (
                      <span className="ml-2 text-red-500">
                        {it.lowStockNote}
                      </span>
                    ) : null}
                  </p>
                </div>
              </div>
              <div className="flex flex-wrap items-center gap-3 pt-2">
                <button
                  onClick={() => addToCart(it.id)}
                  className="flex-1 rounded-full bg-[#F7A072] px-4 py-2 text-sm font-medium text-white hover:bg-orange-500"
                >
                  장바구니 담기
                </button>
                <button
                  onClick={() => toggleLike(it.id)}
                  className="rounded-full border border-gray-200 px-4 py-2 text-sm text-gray-600"
                >
                  찜 해제
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>

      <div className="flex justify-center gap-2">
        {[1, 2, 3, 4, 5].map((p) => (
          <button
            key={p}
            className={
              p === 1
                ? "h-8 w-8 rounded border border-gray-300 bg-gray-800 text-white"
                : "h-8 w-8 rounded border border-gray-300 bg-white text-gray-700"
            }
          >
            {p}
          </button>
        ))}
      </div>
    </div>
  );
}
