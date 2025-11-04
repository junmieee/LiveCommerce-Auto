export const runtime = "nodejs";

import { NextResponse } from "next/server";
import { MockOrder, OrderStatus, PaymentProvider } from "@/types/mall";
import { randomUUID } from "node:crypto";

const storeKey = "__mockOrders";

type CreateMockOrderRequest = {
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
  status?: OrderStatus;
};

declare global {
  var __mockOrders: MockOrder[] | undefined;
}

function getStore(): MockOrder[] {
  if (!globalThis[storeKey]) {
    globalThis[storeKey] = [];
  }
  return globalThis[storeKey]!;
}

function upsertOrder(payload: CreateMockOrderRequest): MockOrder {
  const store = getStore();
  const now = new Date().toISOString();
  const status: OrderStatus = payload.status ?? "COMPLETED";

  const existing = store.find((item) => item.orderId === payload.orderId);
  if (existing) {
    Object.assign(existing, {
      provider: payload.provider,
      productId: payload.productId,
      productName: payload.productName,
      productImage: payload.productImage,
      sellerName: payload.sellerName ?? existing.sellerName,
      buyerName: payload.buyerName ?? existing.buyerName,
      quantity: payload.quantity,
      unitPrice: payload.unitPrice,
      amount: payload.amount,
      status,
      updatedAt: now,
      confirmedAt: status === "COMPLETED" ? now : existing.confirmedAt,
    });
    return existing;
  }

  const order: MockOrder = {
    id: randomUUID(),
    orderId: payload.orderId,
    provider: payload.provider,
    productId: payload.productId,
    productName: payload.productName,
    productImage: payload.productImage,
    sellerName: payload.sellerName ?? null,
    buyerName: payload.buyerName ?? null,
    quantity: payload.quantity,
    unitPrice: payload.unitPrice,
    amount: payload.amount,
    status,
    createdAt: now,
    confirmedAt: status === "COMPLETED" ? now : undefined,
    updatedAt: now,
  };
  store.unshift(order);
  return order;
}

export async function GET() {
  const orders = getStore();
  return NextResponse.json(
    { success: true, orders },
    {
      status: 200,
    },
  );
}

export async function POST(request: Request) {
  let body: CreateMockOrderRequest;
  try {
    body = (await request.json()) as CreateMockOrderRequest;
  } catch {
    return NextResponse.json(
      { success: false, error: { message: "잘못된 요청 본문입니다." } },
      { status: 400 },
    );
  }

  if (
    !body.orderId ||
    !body.productId ||
    !body.productName ||
    !Number.isFinite(body.quantity) ||
    !Number.isFinite(body.unitPrice) ||
    !Number.isFinite(body.amount)
  ) {
    return NextResponse.json(
      { success: false, error: { message: "필수 값이 누락되었습니다." } },
      { status: 400 },
    );
  }

  const order = upsertOrder(body);
  return NextResponse.json(
    { success: true, order },
    {
      status: 200,
    },
  );
}
