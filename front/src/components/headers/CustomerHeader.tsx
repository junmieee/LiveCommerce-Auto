"use client";

import { Bell } from "lucide-react";
import React from "react";

export default function CustomerHeader({ title }: { title?: string }) {
  return (
    <header className="flex items-center justify-between px-6 py-3 border-b bg-white fixed w-full top-0 right-0 z-30">
      <div className="flex items-center gap-3">
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
