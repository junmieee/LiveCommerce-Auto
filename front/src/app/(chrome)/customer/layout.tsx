"use client";

import Sidebar from "@/components/Sidebar";
import { customerNavItems } from "@/constants/customerNav";
import { useState } from "react";
import CustomerHeader from "@/components/headers/CustomerHeader";

export default function CustomerLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const [isOpen, setIsOpen] = useState(true);
  return (
    <div className="flex min-h-screen">
      <Sidebar
        isOpen={isOpen}
        setIsOpen={setIsOpen}
        items={customerNavItems}
        variant="customer"
      />
      <main className="flex-1 bg-[#EDEFF2]">
        <CustomerHeader title="찜 상품" />
        <main className={`${isOpen ? "ml-60" : "ml-20"} pt-20 px-10 py-14`}>
          {children}
        </main>
      </main>
    </div>
  );
}
