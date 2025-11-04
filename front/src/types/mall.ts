export type MallProductDetail = {
  id: string;
  name: string;
  price: number;
  description?: string | null;
  sellerName?: string | null;
  sellerCode?: string | null;
  sellerId?: number | null;
  shippingFee?: number | null;
  shippingInfo?: string | null;
  stockQuantity?: number | null;
  imageUrl?: string | null;
  gallery?: string[];
  badges?: string[];
  createdAt?: string | null;
};

export type PaymentProvider = "toss" | "kakaopay";

export type PaymentDevice = "mobile" | "desktop";

export type PaymentSession = {
  orderId: string;
  provider: PaymentProvider;
  productId: string;
  productName: string;
  quantity: number;
  amount: number;
  currency?: string;
  referenceId: string;
  mobileUrl?: string;
  pcUrl?: string;
  appScheme?: string;
  qrPayload?: string;
  expiresAt?: string;
  statusEndpoint?: string;
  tid?: string;
  paymentKey?: string;
};

export type OrderStatus = "PENDING" | "COMPLETED" | "FAILED" | "CANCELED";

export type OrderStatusPayload = {
  orderId: string;
  provider: PaymentProvider;
  status: OrderStatus;
  message?: string;
  approvedAt?: string;
  updatedAt?: string;
};

export type MockOrder = {
  id: string;
  orderId: string;
  provider: PaymentProvider;
  productId: string;
  productName: string;
  productImage?: string | null;
  sellerName?: string | null;
  buyerName?: string | null;
  quantity: number;
  unitPrice: number;
  amount: number;
  status: OrderStatus;
  createdAt: string;
  confirmedAt?: string;
  updatedAt?: string;
};
