"use client";

import { Search, ChevronsUpDown } from "lucide-react";
import ProductCard from "@/components/mall/ProductCard";
import { useMemo, useState, useEffect, useRef } from "react";
import CustomerHeader from "@/components/headers/CustomerHeader";

type Product = {
  id: string;
  name: string;
  price: number;
  imageUrl?: string | null;
  createdAt?: string; // ISO date
  sales?: number; // 누적 판매량
};

type SellerData = {
  seller: string;
  sellerName: string;
  bannerImageUrl?: string;
  products: Product[];
};

export default function SellerMall({ data }: { data: SellerData }) {
  const [q, setQ] = useState("");
  // 정렬 키와 각 키의 오더 상태 관리
  const [sortKey, setSortKey] = useState<"date" | "sales" | "price">("date");
  const [dateOrder, setDateOrder] = useState<"desc" | "asc">("desc"); // 최신순 기본
  const [salesOrder, setSalesOrder] = useState<"desc" | "asc">("desc"); // 많이 팔린 순 기본
  const [priceOrder, setPriceOrder] = useState<"desc" | "asc">("asc"); // 가격 낮은순 기본
  // 열려있는 필터 키 (다른 필터 클릭 시 자동으로 닫히도록 단일 상태로 관리)
  const [openKey, setOpenKey] = useState<null | "date" | "sales" | "price">(
    null,
  );

  const filtered = useMemo(() => {
    const base = data.products.filter((p) =>
      q ? p.name.toLowerCase().includes(q.toLowerCase()) : true,
    );
    const copy = [...base];
    switch (sortKey) {
      case "sales": {
        const dir = salesOrder === "desc" ? -1 : 1;
        return copy.sort((a, b) => ((a.sales ?? 0) - (b.sales ?? 0)) * dir);
      }
      case "price": {
        const dir = priceOrder === "desc" ? -1 : 1;
        return copy.sort((a, b) => (a.price - b.price) * dir);
      }
      case "date":
      default: {
        const dir = dateOrder === "desc" ? -1 : 1;
        return copy.sort(
          (a, b) =>
            (new Date(a.createdAt ?? 0).getTime() -
              new Date(b.createdAt ?? 0).getTime()) *
            dir,
        );
      }
    }
  }, [data.products, q, sortKey, dateOrder, salesOrder, priceOrder]);

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Top bar (shared customer header) */}
      <CustomerHeader />

      {/* Search */}
      <div className="mx-auto max-w-6xl px-4 py-4">
        <div className="flex items-center gap-2 rounded-full border bg-white px-4 py-3">
          <Search className="h-5 w-5 text-gray-400" />
          <input
            value={q}
            onChange={(e) => setQ(e.target.value)}
            placeholder="검색하세요"
            className="w-full outline-none"
          />
        </div>
      </div>

      {/* Banner */}
      <div className="mx-auto max-w-6xl px-4">
        <div className="h-44 w-full overflow-hidden rounded-md bg-green-50">
          {data.bannerImageUrl ? (
            // eslint-disable-next-line @next/next/no-img-element
            <img
              src={data.bannerImageUrl}
              alt="banner"
              className="h-full w-full object-cover"
            />
          ) : (
            <div className="flex h-full w-full items-center justify-between px-10">
              <div>
                <div className="text-2xl font-bold text-green-900">
                  Grab Upto 50% Off On
                </div>
                <div className="text-2xl font-bold text-green-900">
                  Selected Headphone
                </div>
                <button className="mt-4 rounded-full bg-green-800 px-5 py-2 text-white">
                  Buy Now
                </button>
              </div>
              {/* eslint-disable-next-line @next/next/no-img-element */}
              <img
                src="https://images.unsplash.com/photo-1518442072033-88e5f6120ce4?q=80&w=800&auto=format&fit=crop"
                alt="banner"
                className="h-full w-64 object-cover"
              />
            </div>
          )}
        </div>
      </div>

      {/* Sort filters (검색 필터 UI) */}
      <div className="mx-auto mt-6 max-w-6xl px-4">
        <div className="border-t pt-3">
          <div className="flex flex-wrap items-center gap-2 sm:gap-3">
            {/* 날짜 */}
            <div className="shrink-0">
              <div className="mb-2 text-sm text-gray-600">날짜</div>
              <FilterSelect
                value={dateOrder}
                displayValue={dateOrder === "desc" ? "최신순" : "오래된순"}
                options={[
                  { value: "desc", label: "최신순" },
                  { value: "asc", label: "오래된순" },
                ]}
                onChange={(v) => {
                  setDateOrder(v as "desc" | "asc");
                  setSortKey("date");
                }}
                isOpen={openKey === "date"}
                onToggle={() =>
                  setOpenKey((k) => (k === "date" ? null : "date"))
                }
                onClose={() => setOpenKey(null)}
              />
            </div>

            {/* 판매량 */}
            <div className="shrink-0">
              <div className="mb-2 text-sm text-gray-600">판매량</div>
              <FilterSelect
                value={salesOrder}
                displayValue={salesOrder === "desc" ? "많은순" : "적은순"}
                options={[
                  { value: "desc", label: "많은순" },
                  { value: "asc", label: "적은순" },
                ]}
                onChange={(v) => {
                  setSalesOrder(v as "desc" | "asc");
                  setSortKey("sales");
                }}
                isOpen={openKey === "sales"}
                onToggle={() =>
                  setOpenKey((k) => (k === "sales" ? null : "sales"))
                }
                onClose={() => setOpenKey(null)}
              />
            </div>

            {/* 가격 */}
            <div className="shrink-0">
              <div className="mb-2 text-sm text-gray-600">가격</div>
              <FilterSelect
                value={priceOrder}
                displayValue={priceOrder === "asc" ? "낮은순" : "높은순"}
                options={[
                  { value: "asc", label: "낮은순" },
                  { value: "desc", label: "높은순" },
                ]}
                onChange={(v) => {
                  setPriceOrder(v as "desc" | "asc");
                  setSortKey("price");
                }}
                isOpen={openKey === "price"}
                onToggle={() =>
                  setOpenKey((k) => (k === "price" ? null : "price"))
                }
                onClose={() => setOpenKey(null)}
              />
            </div>
          </div>
        </div>
      </div>

      {/* Product grid */}
      <div className="mx-auto max-w-6xl px-4 py-8">
        {filtered.length === 0 ? (
          <div className="rounded-3xl bg-white py-16 text-center text-gray-500 shadow-sm">
            등록된 상품이 없습니다.
          </div>
        ) : (
          <div className="grid grid-cols-2 gap-6 sm:grid-cols-3 lg:grid-cols-4">
            {filtered.map((p) => (
              <ProductCard
                key={p.id}
                {...p}
                href={`/mall/products/${p.id}${data.seller ? "?seller=" + encodeURIComponent(data.seller) : ""}`}
                onAddToCart={() => {}}
              />
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

type Option = { value: string; label: string };

function FilterSelect({
  value,
  displayValue,
  options,
  onChange,
  isOpen,
  onToggle,
  onClose,
}: {
  value: string;
  displayValue: string;
  options: Option[];
  onChange: (value: string) => void;
  isOpen: boolean;
  onToggle: () => void;
  onClose: () => void;
}) {
  const rootRef = useRef<HTMLDivElement | null>(null);

  // 바깥 영역 클릭 시 닫기
  useEffect(() => {
    function handleDown(e: MouseEvent) {
      if (!isOpen) return;
      const el = rootRef.current;
      if (el && !el.contains(e.target as Node)) {
        onClose();
      }
    }
    document.addEventListener("mousedown", handleDown);
    return () => document.removeEventListener("mousedown", handleDown);
  }, [isOpen, onClose]);

  return (
    <div className="relative inline-block" ref={rootRef}>
      <button
        type="button"
        className="flex min-w-[160px] items-center justify-between rounded-full bg-gray-100 px-5 py-2 text-gray-800 hover:bg-gray-200"
        onClick={onToggle}
        aria-haspopup="listbox"
        aria-expanded={isOpen}
      >
        <span>{displayValue}</span>
        <ChevronsUpDown className="h-4 w-4 text-gray-700" />
      </button>
      {isOpen && (
        <div className="absolute left-0 z-20 mt-2 w-full overflow-hidden rounded-xl border bg-white shadow">
          <ul role="listbox">
            {options.map((opt) => (
              <li key={opt.value}>
                <button
                  className={`block w-full px-4 py-2 text-left hover:bg-gray-50 ${
                    value === opt.value ? "bg-gray-50 font-medium" : ""
                  }`}
                  onClick={() => {
                    onChange(opt.value);
                    onClose();
                  }}
                  role="option"
                  aria-selected={value === opt.value}
                >
                  {opt.label}
                </button>
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}
