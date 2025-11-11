"use client";

import { cn } from "@/libs/utils";
import { Bell, Menu } from "lucide-react";
import React from "react";

export default function CustomerHeader({
  title,
  isSidebarOpen,
  onOpenDrawer,
}: {
  title?: string;
  isSidebarOpen: boolean;
  onOpenDrawer: () => void;
}) {
  return (
    <header
      className={cn(
        "fixed top-0 left-0 right-0 z-30 flex items-center justify-between gap-3 border-b bg-white/95 px-4 py-3 backdrop-blur sm:px-6",
        isSidebarOpen ? "lg:left-64" : "lg:left-20",
      )}
    >
      <div className="flex items-center gap-3">
        <button
          type="button"
          onClick={onOpenDrawer}
          className="inline-flex h-10 w-10 items-center justify-center rounded-full border border-gray-200 text-gray-600 transition hover:bg-gray-100 lg:hidden"
          aria-label="사이드바 열기"
        >
          <Menu className="h-5 w-5" />
        </button>
        <div className="flex h-10 w-10 items-center justify-center rounded-full bg-orange-200 text-orange-700 font-bold">
          ㅇㅇ
        </div>
        <div className="text-lg font-semibold">{title ?? "서비스명"}</div>
      </div>
      <div className="relative flex items-center gap-2 rounded-full border px-4 py-2 text-gray-500">
        <Bell className="h-5 w-5" />
        <span>알림</span>
        <span className="absolute right-3 top-2 h-2 w-2 rounded-full bg-red-500" />
      </div>
    </header>
  );
}
