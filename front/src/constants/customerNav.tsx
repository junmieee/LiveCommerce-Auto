import { NavItem } from "@/components/Sidebar";
import {
  ClipboardList,
  ShoppingCart,
  Store,
  Heart,
  Megaphone,
} from "lucide-react";

export const customerNavItems: NavItem[] = [
  {
    label: "주문/배송",
    href: "/customer/orders",
    icon: <ClipboardList size={24} />,
  },
  {
    label: "장바구니",
    href: "/customer/cart",
    icon: <ShoppingCart size={24} />,
  },
  {
    label: "찜 스토어",
    href: "/customer/wish-stores",
    icon: <Store size={24} />,
  },
  { label: "찜 상품", href: "/customer/wishlist", icon: <Heart size={24} /> },
  {
    label: "공지사항",
    href: "/customer/notice",
    icon: <Megaphone size={24} />,
  },
];
