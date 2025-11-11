"use client";

import Sidebar from "@/components/Sidebar";
import Header from "@/components/Header";
import { cn } from "@/libs/utils";
import { useState } from "react";

export default function AdminLayout({
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
        <Header isOpen={isOpen} onOpenDrawer={openDrawer} />
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
