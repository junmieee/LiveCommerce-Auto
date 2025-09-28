"use client";

import { navItems as adminNavItems } from "@/constants/nav";
import { cn } from "@/libs/utils";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { ChevronsLeft, ChevronsRight } from "lucide-react";
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
}: {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
  items?: NavItem[];
  variant?: "admin" | "customer";
}) {
  // test
  const pathname = usePathname();

  const toggleSidebar = () => {
    setIsOpen(!isOpen);
  };
  return (
    <aside
      className={`h-screen transition-all duration-300 flex flex-col justify-between fixed left-0 top-0 z-40 ${
        variant === "customer"
          ? "bg-sidebarCustomer text-gray-900"
          : "bg-gray-900 text-white"
      } ${isOpen ? "w-64" : "w-20"}`}
    >
      <div>
        <div onClick={toggleSidebar}>
          <div className="h-24 flex items-center justify-center">
            <span className="text-xl font-bold">서비스명</span>
          </div>
          <div className="flex items-center justity-center w-30 px-4 group">
            <span className="flex-grow border-b border-gray-100"></span>
            {isOpen ? (
              <ChevronsLeft
                size={40}
                className="ml-2 text-gray-100 group-hover:animate-wiggleLeft transition-transform"
              />
            ) : (
              <ChevronsRight
                size={40}
                className="ml-2 text-gray-100 group-hover:animate-wiggleRight transition-transform"
              />
            )}
          </div>
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
            >
              <span className="w-8 shrink-0 px-1">{item.icon}</span>
              <span className="ml-2 min-w-0 overflow-hidden whitespace-nowrap text-lg">
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
