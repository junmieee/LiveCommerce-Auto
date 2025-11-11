"use client";

import { navItems as adminNavItems } from "@/constants/nav";
import { cn } from "@/libs/utils";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { ChevronsLeft, ChevronsRight, X } from "lucide-react";
export type NavItem = {
  label: string;
  href: string;
  icon: React.ReactNode;
};

export default function Sidebar({
  isOpen,
  setIsOpen,
  items,
  variant = "admin",
  isDrawerOpen = false,
  onDrawerClose,
}: {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
  items?: NavItem[];
  variant?: "admin" | "customer";
  isDrawerOpen?: boolean;
  onDrawerClose?: () => void;
}) {
  // test
  const pathname = usePathname();

  const toggleSidebar = () => {
    setIsOpen(!isOpen);
  };
  const handleNavigate = () => {
    if (onDrawerClose) {
      onDrawerClose();
    }
  };

  const dividerColor =
    variant === "customer" ? "border-gray-300" : "border-gray-100";
  const chevronColor =
    variant === "customer" ? "text-gray-600" : "text-gray-100";

  return (
    <aside
      className={cn(
        "fixed inset-y-0 left-0 z-40 flex h-screen flex-col justify-between border-r border-black/10 transition-all duration-300",
        variant === "customer"
          ? "bg-sidebarCustomer text-gray-900"
          : "bg-gray-900 text-white",
        isOpen ? "lg:w-64" : "lg:w-20",
        "w-64 shadow-xl lg:shadow-none",
        isDrawerOpen ? "translate-x-0" : "-translate-x-full lg:translate-x-0",
      )}
    >
      <div>
        <div className="flex h-24 items-center justify-between px-4">
          <span className="text-xl font-bold">서비스명</span>
          <button
            type="button"
            className="rounded-full p-2 text-white transition hover:bg-white/10 lg:hidden"
            onClick={() => onDrawerClose?.()}
            aria-label="사이드바 닫기"
          >
            <X className="h-5 w-5" />
          </button>
        </div>
        <div
          className="group hidden cursor-pointer items-center justify-center px-4 lg:flex"
          onClick={toggleSidebar}
        >
          <span className={cn("flex-grow border-b", dividerColor)} />
          {isOpen ? (
            <ChevronsLeft
              size={32}
              className={cn(
                "ml-2 transition-transform",
                chevronColor,
                "group-hover:animate-wiggleLeft",
              )}
            />
          ) : (
            <ChevronsRight
              size={32}
              className={cn(
                "ml-2 transition-transform",
                chevronColor,
                "group-hover:animate-wiggleRight",
              )}
            />
          )}
        </div>
        <nav
          className={`mt-6 px-4 space-y-6 ${variant === "customer" ? "text-gray-600" : "text-[#818490]"}`}
        >
          {(items ?? adminNavItems).map((item) => (
            <Link
              key={item.href}
              href={item.href}
              className={cn(
                "flex items-center justify-start gap-2 p-2 rounded",
                pathname.startsWith(item.href)
                  ? "bg-gray-700 font-semibold text-white"
                  : "",
                isOpen
                  ? " hover:bg-gray-700 text-base transition-all hover:text-white"
                  : "hover:bg-gray-700 transition-all hover:text-white",
              )}
              onClick={handleNavigate}
            >
              <span className="w-8 shrink-0 px-1">{item.icon}</span>
              <span
                className={cn(
                  "ml-2 min-w-0 overflow-hidden whitespace-nowrap text-lg",
                  !isOpen && "lg:hidden",
                )}
              >
                {item.label}
              </span>
            </Link>
          ))}
        </nav>
      </div>
      <div className="text-sm p-4 border-t border-gray-700">
        <div>
          <div className="text-gray-400">쭌미 님</div>
          <div className="text-gray-500">junmieee@gmail.com</div>
        </div>
      </div>
    </aside>
  );
}
