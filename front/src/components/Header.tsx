"use client";

import { cn } from "@/libs/utils";
import { Bell, CirclePlus, Menu } from "lucide-react";
import { useRouter } from "next/navigation";
import type { ReactNode } from "react";

export default function Header({
  isOpen,
  title,
  rightSlot,
  onOpenDrawer,
}: {
  isOpen: boolean;
  title?: string;
  rightSlot?: ReactNode;
  onOpenDrawer?: () => void;
}) {
  const router = useRouter();
  return (
    <header
      className={cn(
        "fixed top-0 left-0 right-0 z-30 flex flex-wrap items-center justify-between gap-3 border-b bg-white/95 px-4 py-3 backdrop-blur transition-all duration-300 sm:px-6",
        isOpen ? "lg:left-64" : "lg:left-20",
      )}
    >
      <div className="flex flex-1 items-center gap-3">
        <button
          type="button"
          className="inline-flex h-10 w-10 items-center justify-center rounded-full border border-gray-200 text-gray-600 transition hover:bg-gray-100 lg:hidden"
          onClick={() => onOpenDrawer?.()}
          aria-label="사이드바 열기"
        >
          <Menu className="h-5 w-5" />
        </button>
        <span className="text-lg font-semibold">{title ?? "대시보드"}</span>
      </div>

      {rightSlot ?? (
        <div className="flex flex-wrap items-center justify-end gap-3">
          <div className="flex h-12 min-w-[120px] items-center justify-between gap-3 rounded-full border px-4">
            <div className="relative">
              <Bell className="w-6 h-6 text-gray-500" />
              <span className="absolute top-1 right-1 w-2 h-2 bg-red-500 rounded-full" />
            </div>
            <span className="text-gray-400 whitespace-nowrap">알림</span>
          </div>
          <div>
            <button
              onClick={() => router.push("/admin/products/new")}
              className="flex h-12 w-full min-w-[150px] items-center justify-center gap-1 rounded-full bg-[#F7A072] px-4 text-base text-white transition hover:bg-orange-500"
            >
              <span>상품 추가</span>
              <CirclePlus fill="white" color="#F7A072" size={24} />
            </button>
          </div>
        </div>
      )}
    </header>
  );
}
