"use client";

import Sidebar from "@/components/Sidebar";
import { customerNavItems } from "@/constants/customerNav";
import { useState } from "react";
import CustomerHeader from "@/components/headers/CustomerHeader";
import { cn } from "@/libs/utils";

export default function CustomerLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const [isOpen, setIsOpen] = useState(true);
  const [isDrawerOpen, setIsDrawerOpen] = useState(false);

  const openDrawer = () => setIsDrawerOpen(true);
  const closeDrawer = () => setIsDrawerOpen(false);

  return (
    <div className="flex min-h-screen bg-[#EDEFF2]">
      <Sidebar
        isOpen={isOpen}
        setIsOpen={setIsOpen}
        items={customerNavItems}
        variant="customer"
        isDrawerOpen={isDrawerOpen}
        onDrawerClose={closeDrawer}
      />
      {isDrawerOpen ? (
        <div
          className="fixed inset-0 z-30 bg-black/40 backdrop-blur-sm lg:hidden"
          onClick={closeDrawer}
        />
      ) : null}
      <div className="flex min-h-screen flex-1 flex-col">
        <CustomerHeader
          title="찜 상품"
          isSidebarOpen={isOpen}
          onOpenDrawer={openDrawer}
        />
        <main
          className={cn(
            "flex-1 px-4 pb-10 pt-24 sm:px-6 md:px-8 lg:px-10",
            isOpen ? "lg:ml-64" : "lg:ml-20",
          )}
        >
          {children}
        </main>
      </div>
    </div>
  );
}
