"use client";

import SellerRouteGuard from "@/components/auth/SellerRouteGuard";
import { apiFetch } from "@/lib/apiClient";
import { getSellerId } from "@/lib/auth";
import {
  CalendarClock,
  ChevronDown,
  Loader2,
  Package,
  PenSquare,
  Search,
  Tag,
  Trash2,
} from "lucide-react";
import { useCallback, useEffect, useMemo, useState } from "react";
import type { ReactNode } from "react";

const numberFormatter = new Intl.NumberFormat("ko-KR");

interface SaleItem {
  id: number;
  name: string;
  price: number;
  stockQuantity: number;
  createdAt: string;
  isActive: boolean;
  thumbnailImageKey?: string | null;
}

interface ProductListResponse {
  success?: boolean;
  data?: ProductResponseDto[];
  total?: number;
  page?: number;
  limit?: number;
  message?: string;
}

interface ProductResponseDto {
  id?: number | string;
  name?: string;
  price?: number | string;
  stockQuantity?: number | string;
  createdAt?: string;
  isActive?: boolean;
  thumbnailImageKey?: string | null;
}

type SortKey =
  | "created_desc"
  | "created_asc"
  | "price_desc"
  | "price_asc"
  | "stock_desc"
  | "stock_asc";

type SortSelections = {
  price: "" | "price_desc" | "price_asc";
  stock: "" | "stock_desc" | "stock_asc";
  createdAt: "created_desc" | "created_asc";
};

const DEFAULT_DATE_SORT: SortSelections["createdAt"] = "created_desc";
const PRICE_SORT_OPTIONS = [
  { value: "price_desc", label: "가격 높은순" },
  { value: "price_asc", label: "가격 낮은순" },
] as const;
const STOCK_SORT_OPTIONS = [
  { value: "stock_desc", label: "잔여수량 높은순" },
  { value: "stock_asc", label: "잔여수량 낮은순" },
] as const;
const DATE_SORT_OPTIONS = [
  { value: "created_desc", label: "추가일자 최신순" },
  { value: "created_asc", label: "추가일자 오래된순" },
] as const;

export default function SalesManagementPage() {
  const sellerId = useMemo(() => getSellerId(), []);
  const [items, setItems] = useState<SaleItem[]>([]);
  const [selectedIds, setSelectedIds] = useState<number[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [searchQuery, setSearchQuery] = useState("");
  const [activeSort, setActiveSort] = useState<SortKey>(DEFAULT_DATE_SORT);
  const [sortSelections, setSortSelections] = useState<SortSelections>({
    price: "",
    stock: "",
    createdAt: DEFAULT_DATE_SORT,
  });
  const [page, setPage] = useState(1);
  const [total, setTotal] = useState(0);
  const [lastFetchedAt, setLastFetchedAt] = useState<Date | null>(null);
  const [processing, setProcessing] = useState(false);

  const limit = 10;

  const fetchProducts = useCallback(async () => {
    if (!sellerId) return;
    setLoading(true);
    setError(null);
    try {
      const params = new URLSearchParams({
        sellerId,
        page: String(page),
        limit: String(limit),
      });
      if (searchQuery) params.set("search", searchQuery.trim());
      const apiSort = mapSortToApi(activeSort);
      if (apiSort) params.set("sort", apiSort);

      const baseUrl = resolveApiBaseUrl();

      if (!baseUrl) {
        throw new Error("API 주소가 설정되어 있지 않습니다.");
      }

      const target = `${baseUrl}/seller/products?${params.toString()}`;
      const res = await apiFetch(target);
      const data = (await res.json().catch(() => ({}))) as ProductListResponse;
      if (!res.ok || data?.success === false) {
        throw new Error(data?.message || "상품 목록을 불러오지 못했습니다.");
      }
      const list = Array.isArray(data?.data) ? data.data : [];
      setItems(list.map(normalizeProduct));
      setTotal(typeof data?.total === "number" ? data.total : list.length);
      setSelectedIds([]);
      setLastFetchedAt(new Date());
    } catch (err) {
      setError(err instanceof Error ? err.message : "오류가 발생했습니다.");
      setItems([]);
      setTotal(0);
    } finally {
      setLoading(false);
    }
  }, [sellerId, page, limit, searchQuery, activeSort]);

  useEffect(() => {
    fetchProducts();
  }, [fetchProducts]);

  const displayedItems = useMemo(() => {
    const cloned = [...items];
    cloned.sort((a, b) => compareBySort(a, b, activeSort));
    return cloned;
  }, [items, activeSort]);

  const allSelected =
    displayedItems.length > 0 &&
    displayedItems.every((item) => selectedIds.includes(item.id));

  const totalPages = Math.max(1, Math.ceil(total / limit));
  const pageNumbers = useMemo(
    () => buildPagination(page, totalPages),
    [page, totalPages],
  );

  const handleSearchSubmit = (event?: React.FormEvent) => {
    event?.preventDefault();
    setPage(1);
    setSearchQuery(searchTerm.trim());
  };

  const handlePriceSortChange = (value: SortSelections["price"]) => {
    setSortSelections({
      price: value,
      stock: "",
      createdAt: DEFAULT_DATE_SORT,
    });
    setActiveSort(value || DEFAULT_DATE_SORT);
    setPage(1);
  };

  const handleStockSortChange = (value: SortSelections["stock"]) => {
    setSortSelections({
      price: "",
      stock: value,
      createdAt: DEFAULT_DATE_SORT,
    });
    setActiveSort(value || DEFAULT_DATE_SORT);
    setPage(1);
  };

  const handleDateSortChange = (value: SortSelections["createdAt"]) => {
    setSortSelections({ price: "", stock: "", createdAt: value });
    setActiveSort(value);
    setPage(1);
  };

  const toggleSelectAll = (checked: boolean) => {
    setSelectedIds(checked ? displayedItems.map((item) => item.id) : []);
  };

  const toggleSelectItem = (id: number) => {
    setSelectedIds((prev) =>
      prev.includes(id)
        ? prev.filter((itemId) => itemId !== id)
        : [...prev, id],
    );
  };

  const handleDeleteSelected = () => {
    if (selectedIds.length === 0) return;
    setItems((prev) => prev.filter((item) => !selectedIds.includes(item.id)));
    setSelectedIds([]);
  };

  const handleDeleteAll = () => {
    if (displayedItems.length === 0) return;
    setItems([]);
    setSelectedIds([]);
  };

  const handleToggleActive = async (id: number) => {
    setProcessing(true);
    setItems((prev) =>
      prev.map((item) =>
        item.id === id ? { ...item, isActive: !item.isActive } : item,
      ),
    );
    setProcessing(false);
  };

  const handlePageChange = (nextPage: number) => {
    if (nextPage < 1 || nextPage > totalPages || nextPage === page) return;
    setPage(nextPage);
  };

  if (!sellerId) {
    return (
      <SellerRouteGuard>
        <div className="p-6 text-gray-500">
          판매자 정보를 확인할 수 없습니다. 다시 로그인해주세요.
        </div>
      </SellerRouteGuard>
    );
  }

  return (
    <SellerRouteGuard>
      <div className="space-y-8">
        <div className="flex flex-wrap items-center justify-between gap-4">
          <div className="flex items-center gap-2 text-gray-900">
            <h1 className="text-3xl font-semibold">판매관리</h1>
            <ChevronDown className="h-6 w-6 text-gray-400" />
          </div>
          <div className="text-sm text-gray-500">
            마지막 업데이트
            <span className="ml-2 font-medium text-gray-700">
              {formatDateTime(lastFetchedAt)}
            </span>
          </div>
        </div>

        <section className="rounded-2xl bg-white shadow-sm ring-1 ring-gray-100">
          <div className="flex flex-wrap items-center gap-3 border-b border-gray-100 px-6 py-4">
            <button
              type="button"
              onClick={handleDeleteAll}
              className="rounded-full border border-gray-300 px-6 py-2 text-sm font-medium text-gray-600 transition hover:border-gray-400 hover:text-gray-900"
            >
              전체삭제
            </button>
            <button
              type="button"
              onClick={handleDeleteSelected}
              className="rounded-full border border-gray-300 px-6 py-2 text-sm font-medium text-gray-600 transition hover:border-gray-400 hover:text-gray-900 disabled:cursor-not-allowed disabled:opacity-50"
              disabled={selectedIds.length === 0}
            >
              선택상품 삭제
            </button>
            {error ? (
              <span className="ml-auto text-sm text-red-500">{error}</span>
            ) : null}
          </div>

          <div className="space-y-6 px-6 pb-6 pt-5">
            <div className="grid grid-cols-1 gap-4 md:grid-cols-4">
              <SearchInput
                value={searchTerm}
                onChange={setSearchTerm}
                onSubmit={handleSearchSubmit}
              />
              <SortSelect
                icon={<Tag className="h-5 w-5" />}
                placeholder="가격"
                value={sortSelections.price}
                options={PRICE_SORT_OPTIONS}
                onChange={(value) =>
                  handlePriceSortChange(value as SortSelections["price"])
                }
              />
              <SortSelect
                icon={<Package className="h-5 w-5" />}
                placeholder="잔여수량"
                value={sortSelections.stock}
                options={STOCK_SORT_OPTIONS}
                onChange={(value) =>
                  handleStockSortChange(value as SortSelections["stock"])
                }
              />
              <SortSelect
                icon={<CalendarClock className="h-5 w-5" />}
                placeholder="추가일자"
                value={sortSelections.createdAt}
                options={DATE_SORT_OPTIONS}
                onChange={(value) =>
                  handleDateSortChange(
                    (value as SortSelections["createdAt"]) || DEFAULT_DATE_SORT,
                  )
                }
                includePlaceholder={false}
              />
            </div>

            <div className="overflow-hidden rounded-2xl border border-gray-100">
              <table className="min-w-full divide-y divide-gray-100 text-sm">
                <thead className="bg-gray-50 text-xs font-medium uppercase text-gray-500">
                  <tr>
                    <th scope="col" className="w-12 px-4 py-4 text-left">
                      <input
                        type="checkbox"
                        className="h-4 w-4 rounded border-gray-300 text-orange-400 focus:ring-orange-400"
                        checked={allSelected}
                        onChange={(event) =>
                          toggleSelectAll(event.target.checked)
                        }
                        disabled={displayedItems.length === 0}
                      />
                    </th>
                    <th
                      scope="col"
                      className="px-4 py-4 text-left text-gray-600"
                    >
                      상품정보
                    </th>
                    <th
                      scope="col"
                      className="px-4 py-4 text-right text-gray-600"
                    >
                      가격
                    </th>
                    <th
                      scope="col"
                      className="px-4 py-4 text-center text-gray-600"
                    >
                      잔여수량
                    </th>
                    <th
                      scope="col"
                      className="px-4 py-4 text-center text-gray-600"
                    >
                      추가일자
                    </th>
                    <th
                      scope="col"
                      className="px-4 py-4 text-center text-gray-600"
                    >
                      수정버튼
                    </th>
                    <th
                      scope="col"
                      className="px-4 py-4 text-center text-gray-600"
                    >
                      삭제버튼
                    </th>
                    <th
                      scope="col"
                      className="px-4 py-4 text-center text-gray-600"
                    >
                      활성버튼
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100 bg-white">
                  {loading ? (
                    <tr>
                      <td
                        colSpan={8}
                        className="px-6 py-12 text-center text-sm text-gray-500"
                      >
                        <div className="flex items-center justify-center gap-3">
                          <Loader2 className="h-5 w-5 animate-spin" />
                          <span>불러오는 중...</span>
                        </div>
                      </td>
                    </tr>
                  ) : displayedItems.length === 0 ? (
                    <tr>
                      <td
                        colSpan={8}
                        className="px-6 py-12 text-center text-sm text-gray-400"
                      >
                        표시할 판매 상품이 없습니다.
                      </td>
                    </tr>
                  ) : (
                    displayedItems.map((item) => (
                      <tr key={item.id} className="hover:bg-gray-50">
                        <td className="px-4 py-5">
                          <input
                            type="checkbox"
                            className="h-4 w-4 rounded border-gray-300 text-orange-400 focus:ring-orange-400"
                            checked={selectedIds.includes(item.id)}
                            onChange={() => toggleSelectItem(item.id)}
                          />
                        </td>
                        <td className="px-4 py-5">
                          <div className="flex items-center gap-4">
                            <div className="flex h-14 w-14 items-center justify-center overflow-hidden rounded-xl bg-gray-100 text-xs text-gray-500">
                              {item.thumbnailImageKey ? (
                                // eslint-disable-next-line @next/next/no-img-element
                                <img
                                  src={item.thumbnailImageKey}
                                  alt={item.name}
                                  className="h-full w-full object-cover"
                                  onError={(event) => {
                                    event.currentTarget.style.display = "none";
                                  }}
                                />
                              ) : (
                                <span>IMG</span>
                              )}
                            </div>
                            <div>
                              <p className="text-base font-medium text-gray-900">
                                {item.name}
                              </p>
                              <p className="text-sm text-gray-500">
                                상품 ID #{item.id}
                              </p>
                            </div>
                          </div>
                        </td>
                        <td className="px-4 py-5 text-right text-base font-semibold text-gray-900">
                          {numberFormatter.format(item.price)}원
                        </td>
                        <td className="px-4 py-5 text-center text-base text-gray-700">
                          {item.stockQuantity}개
                        </td>
                        <td className="px-4 py-5 text-center text-sm text-gray-600">
                          {formatDateTime(item.createdAt)}
                        </td>
                        <td className="px-4 py-5 text-center">
                          <button
                            type="button"
                            className="inline-flex h-10 w-10 items-center justify-center rounded-full border border-gray-200 text-gray-500 transition hover:border-gray-300 hover:text-gray-900"
                            aria-label="상품 수정"
                          >
                            <PenSquare className="h-5 w-5" />
                          </button>
                        </td>
                        <td className="px-4 py-5 text-center">
                          <button
                            type="button"
                            onClick={() =>
                              setItems((prev) =>
                                prev.filter((x) => x.id !== item.id),
                              )
                            }
                            className="inline-flex h-10 w-10 items-center justify-center rounded-full border border-gray-200 text-gray-500 transition hover:border-red-200 hover:text-red-500"
                            aria-label="상품 삭제"
                          >
                            <Trash2 className="h-5 w-5" />
                          </button>
                        </td>
                        <td className="px-4 py-5 text-center">
                          <button
                            type="button"
                            onClick={() => handleToggleActive(item.id)}
                            className={`relative inline-flex h-7 w-14 items-center rounded-full px-1 transition ${
                              item.isActive ? "bg-orange-400" : "bg-gray-200"
                            }`}
                            aria-pressed={item.isActive}
                            disabled={processing}
                          >
                            <span
                              className={`h-5 w-5 rounded-full bg-white shadow transition-transform ${
                                item.isActive
                                  ? "translate-x-7"
                                  : "translate-x-0"
                              }`}
                            />
                          </button>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>

            <div className="flex flex-wrap items-center justify-between gap-4 pt-2 text-sm text-gray-500">
              <div>
                총 <span className="font-semibold text-gray-700">{total}</span>
                건
              </div>
              <div className="flex items-center gap-2">
                <button
                  type="button"
                  onClick={() => handlePageChange(page - 1)}
                  disabled={page === 1}
                  className="rounded-full border border-gray-200 px-3 py-1 text-gray-500 transition hover:border-gray-300 hover:text-gray-700 disabled:cursor-not-allowed disabled:opacity-50"
                >
                  &lt;
                </button>
                {pageNumbers.map((pageNumber) => (
                  <button
                    key={pageNumber}
                    type="button"
                    onClick={() => handlePageChange(pageNumber)}
                    className={`h-8 w-8 rounded-full text-sm font-medium transition ${
                      pageNumber === page
                        ? "bg-gray-900 text-white"
                        : "border border-gray-200 text-gray-600 hover:border-gray-300 hover:text-gray-800"
                    }`}
                  >
                    {pageNumber}
                  </button>
                ))}
                <button
                  type="button"
                  onClick={() => handlePageChange(page + 1)}
                  disabled={page === totalPages}
                  className="rounded-full border border-gray-200 px-3 py-1 text-gray-500 transition hover:border-gray-300 hover:text-gray-700 disabled:cursor-not-allowed disabled:opacity-50"
                >
                  &gt;
                </button>
              </div>
            </div>
          </div>
        </section>
      </div>
    </SellerRouteGuard>
  );
}

function SearchInput({
  value,
  onChange,
  onSubmit,
}: {
  value: string;
  onChange: (value: string) => void;
  onSubmit: (event?: React.FormEvent) => void;
}) {
  return (
    <form
      onSubmit={onSubmit}
      className="flex items-center gap-3 rounded-full border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-500"
    >
      <Search className="h-5 w-5" />
      <input
        type="text"
        value={value}
        onChange={(event) => onChange(event.target.value)}
        placeholder="search"
        className="w-full bg-transparent text-sm text-gray-700 placeholder:text-gray-400 focus:outline-none"
      />
    </form>
  );
}

function resolveApiBaseUrl(): string {
  const raw =
    process.env.NEXT_PUBLIC_API_BASE_URL ??
    process.env.NEXT_PUBLIC_JAVA_API_BASE ??
    "";

  if (!raw) return "";

  try {
    const url = new URL(raw);
    if (typeof window !== "undefined") {
      const currentHost = window.location.hostname;
      if (url.hostname === "backend-java" && currentHost) {
        url.hostname = currentHost;
      }
    }
    return url.toString().replace(/\/$/, "");
  } catch {
    if (typeof window !== "undefined") {
      const origin = window.location.origin;
      const normalized = raw.startsWith("/") ? raw : `/${raw}`;
      return `${origin}${normalized}`.replace(/\/$/, "");
    }
    return raw.replace(/\/$/, "");
  }
}

function SortSelect({
  icon,
  placeholder,
  value,
  options,
  onChange,
  includePlaceholder = true,
}: {
  icon: ReactNode;
  placeholder: string;
  value: string;
  options: readonly { value: string; label: string }[];
  onChange: (value: string) => void;
  includePlaceholder?: boolean;
}) {
  return (
    <div className="flex items-center gap-3 rounded-full border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-500">
      {icon}
      <select
        value={value}
        onChange={(event) => onChange(event.target.value)}
        className="w-full bg-transparent text-sm text-gray-700 focus:outline-none"
      >
        {includePlaceholder ? (
          <option value="" className="text-gray-400">
            {placeholder}
          </option>
        ) : null}
        {options.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
    </div>
  );
}

function mapSortToApi(sort: SortKey): string | null {
  switch (sort) {
    case "price_desc":
      return "price_desc";
    case "price_asc":
      return "price_asc";
    case "created_asc":
      return "created_at_asc";
    case "created_desc":
      return "created_at_desc";
    default:
      return null;
  }
}

function compareBySort(a: SaleItem, b: SaleItem, sort: SortKey): number {
  switch (sort) {
    case "price_desc":
      return (b.price ?? 0) - (a.price ?? 0);
    case "price_asc":
      return (a.price ?? 0) - (b.price ?? 0);
    case "stock_desc":
      return (b.stockQuantity ?? 0) - (a.stockQuantity ?? 0);
    case "stock_asc":
      return (a.stockQuantity ?? 0) - (b.stockQuantity ?? 0);
    case "created_asc":
      return new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime();
    case "created_desc":
    default:
      return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
  }
}

function normalizeProduct(product: ProductResponseDto): SaleItem {
  return {
    id: Number(product?.id ?? 0),
    name: product?.name ?? "-",
    price:
      typeof product?.price === "number"
        ? product.price
        : Number(product?.price ?? 0),
    stockQuantity: Number(product?.stockQuantity ?? 0),
    createdAt: product?.createdAt ?? "",
    isActive: Boolean(product?.isActive),
    thumbnailImageKey: product?.thumbnailImageKey ?? null,
  };
}

function formatDateTime(value: string | Date | null | undefined): string {
  if (!value) return "-";
  const date = value instanceof Date ? value : new Date(value);
  if (Number.isNaN(date.getTime())) return "-";
  const pad = (input: number) => input.toString().padStart(2, "0");
  return `${date.getFullYear()}/${pad(date.getMonth() + 1)}/${pad(date.getDate())} ${pad(
    date.getHours(),
  )}:${pad(date.getMinutes())}`;
}

function buildPagination(current: number, totalPages: number): number[] {
  const pages: number[] = [];
  const windowSize = 5;
  const maxPage = Math.max(1, totalPages);
  const half = Math.floor(windowSize / 2);
  let start = Math.max(1, current - half);
  const end = Math.min(maxPage, start + windowSize - 1);
  if (end - start + 1 < windowSize) {
    start = Math.max(1, end - windowSize + 1);
  }
  for (let page = start; page <= end; page += 1) {
    pages.push(page);
  }
  return pages;
}
