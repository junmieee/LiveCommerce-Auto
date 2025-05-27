"use client";

import Sidebar from "@/components/Sidebar";
import Header from "@/components/Header";
import { useState } from "react";

export default function AdminLpagayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const [isOpen, setIsOpen] = useState(true);
  return (
    <div className="flex min-h-screen">
      <Sidebar isOpen={isOpen} setIsOpen={setIsOpen} />

      <main className="flex-1 bg-[#EDEFF2]">
        <Header isOpen={isOpen} />
        <main className={`${isOpen ? "ml-60" : "ml-20"} pt-20 px-10 py-14`}>
          {children}
        </main>
      </main>
    </div>
  );
}
