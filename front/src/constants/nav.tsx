import { NavItem } from "@/components/Sidebar";
import {
  House,
  SquareKanban,
  Package,
  FileText,
  IdCard,
  Megaphone,
} from "lucide-react";

export const navItems: NavItem[] = [
  { label: "대시보드", href: "/admin/dashboard", icon: <House size={24} /> },
  {
    label: "판매관리",
    href: "/admin/sales",
    icon: <SquareKanban size={24} />,
  },
  { label: "배송관리", href: "/admin/shipping", icon: <Package size={24} /> },
  { label: "주문관리", href: "/admin/orders", icon: <FileText size={24} /> },
  {
    label: "판매물 정보관리",
    href: "/admin/products",
    icon: <IdCard size={24} />,
  },
  { label: "공지사항", href: "/admin/notice", icon: <Megaphone size={24} /> },
];
