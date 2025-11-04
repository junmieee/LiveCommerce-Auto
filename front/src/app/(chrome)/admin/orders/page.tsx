"use client";

import { useCallback, useEffect, useMemo, useState } from "react";
import { MockOrder, OrderStatus, PaymentProvider } from "@/types/mall";
import { cn } from "@/libs/utils";
import {
  Loader2,
  RefreshCcw,
  Search,
  Filter,
  PackageCheck,
} from "lucide-react";

type StatusFilter = OrderStatus | "ALL";
type ProviderFilter = PaymentProvider | "ALL";
type ToneKey = "indigo" | "emerald" | "orange" | "rose";

const STATUS_OPTIONS: { label: string; value: StatusFilter }[] = [
  { label: "전체", value: "ALL" },
  { label: "진행중", value: "PENDING" },
  { label: "완료", value: "COMPLETED" },
  { label: "취소", value: "CANCELED" },
  { label: "실패", value: "FAILED" },
];

const PROVIDER_OPTIONS: { label: string; value: ProviderFilter }[] = [
  { label: "전체", value: "ALL" },
  { label: "토스페이", value: "toss" },
  { label: "카카오페이", value: "kakaopay" },
];

const PLACEHOLDER_IMAGE =
  "https://images.unsplash.com/photo-1512436991641-6745cdb1723f?q=80&w=800&auto=format&fit=crop";

const TONE_CLASS_MAP: Record<ToneKey, string> = {
  indigo: "bg-indigo-50 text-indigo-600",
  emerald: "bg-emerald-50 text-emerald-600",
  orange: "bg-orange-50 text-orange-600",
  rose: "bg-rose-50 text-rose-600",
};

type OrdersResponse = {
  success: boolean;
  orders?: MockOrder[];
};

export default function AdminOrdersPage() {
  const [orders, setOrders] = useState<MockOrder[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [statusFilter, setStatusFilter] = useState<StatusFilter>("ALL");
  const [providerFilter, setProviderFilter] = useState<ProviderFilter>("ALL");
  const [search, setSearch] = useState("");

  const fetchOrders = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const response = await fetch("/api/orders/mock", { cache: "no-store" });
      if (!response.ok) {
        throw new Error("주문 정보를 불러오지 못했습니다.");
      }
      const payload = (await response.json()) as OrdersResponse;
      if (!payload.success || !payload.orders) {
        throw new Error("주문 데이터가 비어 있습니다.");
      }
      setOrders(payload.orders);
    } catch (err) {
      const message =
        err instanceof Error ? err.message : "주문 정보를 불러오지 못했습니다.";
      setError(message);
      setOrders([]);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchOrders();
  }, [fetchOrders]);

  const filteredOrders = useMemo(() => {
    const term = search.trim().toLowerCase();
    return orders.filter((order) => {
      if (statusFilter !== "ALL" && order.status !== statusFilter) return false;
      if (providerFilter !== "ALL" && order.provider !== providerFilter) {
        return false;
      }
      if (!term) return true;
      return (
        order.productName.toLowerCase().includes(term) ||
        order.orderId.toLowerCase().includes(term)
      );
    });
  }, [orders, statusFilter, providerFilter, search]);

  const summary = useMemo(() => {
    const total = orders.length;
    const canceled = orders.filter(
      (order) => order.status === "CANCELED",
    ).length;
    const completed = orders.filter(
      (order) => order.status === "COMPLETED",
    ).length;
    const failed = orders.filter((order) => order.status === "FAILED").length;
    return {
      total,
      canceled,
      completed,
      failed,
    };
  }, [orders]);

  const handleRefresh = () => {
    fetchOrders();
  };

  return (
    <div className="space-y-8">
      <header className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-3xl font-semibold text-gray-900">주문관리</h1>
          <p className="mt-1 text-sm text-gray-500">
            실 결제 대신 임시 확정된 주문 데이터를 관리할 수 있습니다.
          </p>
        </div>
        <button
          type="button"
          onClick={handleRefresh}
          className="flex items-center gap-2 rounded-full border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 transition hover:border-gray-400 hover:text-gray-900"
        >
          {isLoading ? (
            <Loader2 className="h-4 w-4 animate-spin" />
          ) : (
            <RefreshCcw className="h-4 w-4" />
          )}
          새로고침
        </button>
      </header>

      <section className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        <SummaryCard
          label="전체주문"
          value={summary.total}
          tone="indigo"
          description="임시 확정된 주문 건수"
        />
        <SummaryCard
          label="완료"
          value={summary.completed}
          tone="emerald"
          description="완료 처리된 주문"
        />
        <SummaryCard
          label="취소"
          value={summary.canceled}
          tone="orange"
          description="취소된 주문"
        />
        <SummaryCard
          label="실패"
          value={summary.failed}
          tone="rose"
          description="실패/확정되지 않은 주문"
        />
      </section>

      <section className="space-y-5 rounded-3xl bg-white p-6 shadow-sm">
        <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
          <div className="flex flex-wrap items-center gap-2">
            {STATUS_OPTIONS.map((option) => (
              <button
                key={option.value}
                type="button"
                onClick={() => setStatusFilter(option.value)}
                className={cn(
                  "rounded-full border px-3 py-1.5 text-sm transition",
                  statusFilter === option.value
                    ? "border-orange-400 bg-orange-50 text-orange-600"
                    : "border-gray-200 bg-gray-50 text-gray-600 hover:border-gray-300",
                )}
              >
                {option.label}
              </button>
            ))}
          </div>
          <div className="flex flex-wrap items-center gap-3">
            <div className="relative flex items-center">
              <Search className="absolute left-3 h-4 w-4 text-gray-400" />
              <input
                type="search"
                placeholder="상품명 또는 주문번호"
                value={search}
                onChange={(event) => setSearch(event.target.value)}
                className="w-full rounded-full border border-gray-200 bg-gray-50 py-2 pl-9 pr-4 text-sm text-gray-700 placeholder:text-gray-400 focus:border-orange-400 focus:outline-none focus:ring-2 focus:ring-orange-100"
              />
            </div>
            <div className="flex items-center gap-2 rounded-full border border-gray-200 bg-gray-50 px-3 py-1.5 text-sm text-gray-600">
              <Filter className="h-4 w-4" />
              <select
                value={providerFilter}
                onChange={(event) =>
                  setProviderFilter(event.target.value as ProviderFilter)
                }
                className="bg-transparent focus:outline-none"
              >
                {PROVIDER_OPTIONS.map((option) => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </select>
            </div>
          </div>
        </div>

        {error ? (
          <div className="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-600">
            {error}
          </div>
        ) : null}

        <div className="overflow-hidden rounded-2xl border border-gray-100">
          <table className="min-w-full divide-y divide-gray-100">
            <thead className="bg-gray-50">
              <tr className="text-left text-sm font-medium text-gray-500">
                <th className="px-6 py-4">상품 정보</th>
                <th className="px-6 py-4">수량</th>
                <th className="px-6 py-4">결제 금액</th>
                <th className="px-6 py-4">결제수단</th>
                <th className="px-6 py-4">주문 상태</th>
                <th className="px-6 py-4">확정일시</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100 bg-white text-sm text-gray-700">
              {isLoading ? (
                <tr>
                  <td
                    colSpan={6}
                    className="px-6 py-10 text-center text-gray-500"
                  >
                    <Loader2 className="mx-auto h-5 w-5 animate-spin" />
                  </td>
                </tr>
              ) : filteredOrders.length === 0 ? (
                <tr>
                  <td
                    colSpan={6}
                    className="px-6 py-16 text-center text-gray-400"
                  >
                    <PackageCheck className="mx-auto h-8 w-8 text-gray-300" />
                    <p className="mt-3 text-sm">표시할 주문이 없습니다.</p>
                  </td>
                </tr>
              ) : (
                filteredOrders.map((order) => (
                  <tr key={order.id} className="transition hover:bg-gray-50">
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-4">
                        <div className="h-14 w-14 overflow-hidden rounded-xl bg-gray-100">
                          {/* eslint-disable-next-line @next/next/no-img-element */}
                          <img
                            src={order.productImage || PLACEHOLDER_IMAGE}
                            alt={order.productName}
                            className="h-full w-full object-cover"
                          />
                        </div>
                        <div className="space-y-1">
                          <div className="text-sm font-semibold text-gray-900">
                            {order.productName}
                          </div>
                          <div className="text-xs text-gray-400">
                            주문번호 {order.orderId}
                          </div>
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4">
                      <div className="font-medium text-gray-900">
                        {order.quantity.toLocaleString()}개
                      </div>
                      <div className="text-xs text-gray-400">
                        단가 {formatCurrency(order.unitPrice)}원
                      </div>
                    </td>
                    <td className="px-6 py-4 font-semibold text-gray-900">
                      {formatCurrency(order.amount)}원
                    </td>
                    <td className="px-6 py-4 text-gray-600">
                      {order.provider === "toss" ? "토스페이" : "카카오페이"}
                    </td>
                    <td className="px-6 py-4">
                      <StatusChip status={order.status} />
                    </td>
                    <td className="px-6 py-4 text-gray-600">
                      {order.confirmedAt ? formatDate(order.confirmedAt) : "-"}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  );
}

function SummaryCard({
  label,
  value,
  tone,
  description,
}: {
  label: string;
  value: number;
  tone: ToneKey;
  description?: string;
}) {
  return (
    <div className="rounded-3xl border border-gray-100 bg-white p-6 shadow-sm">
      <div
        className={cn(
          "inline-flex rounded-full px-3 py-1 text-xs font-semibold",
          TONE_CLASS_MAP[tone],
        )}
      >
        {label}
      </div>
      <div className="mt-4 text-3xl font-bold text-gray-900">{value}</div>
      {description ? (
        <p className="mt-1 text-sm text-gray-500">{description}</p>
      ) : null}
    </div>
  );
}

function StatusChip({ status }: { status: OrderStatus }) {
  switch (status) {
    case "COMPLETED":
      return (
        <span className="inline-flex rounded-full bg-emerald-50 px-3 py-1 text-xs font-semibold text-emerald-600">
          완료
        </span>
      );
    case "CANCELED":
      return (
        <span className="inline-flex rounded-full bg-orange-50 px-3 py-1 text-xs font-semibold text-orange-600">
          취소
        </span>
      );
    case "FAILED":
      return (
        <span className="inline-flex rounded-full bg-rose-50 px-3 py-1 text-xs font-semibold text-rose-600">
          실패
        </span>
      );
    default:
      return (
        <span className="inline-flex rounded-full bg-gray-100 px-3 py-1 text-xs font-semibold text-gray-600">
          진행중
        </span>
      );
  }
}

function formatCurrency(value: number): string {
  return new Intl.NumberFormat("ko-KR").format(value);
}

function formatDate(value: string): string {
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "-";
  const pad = (input: number) => input.toString().padStart(2, "0");
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`;
}
