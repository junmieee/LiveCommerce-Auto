import {
  OrderStatusPayload,
  PaymentDevice,
  PaymentProvider,
  PaymentSession,
} from "@/types/mall";

export type CreatePaymentSessionParams = {
  provider: PaymentProvider;
  productId: string;
  productName: string;
  quantity: number;
  amount: number;
  buyerName?: string;
  buyerEmail?: string;
  buyerPhone?: string;
  device?: PaymentDevice;
};

export async function createPaymentSession(
  params: CreatePaymentSessionParams,
): Promise<PaymentSession> {
  const response = await fetch("/api/payments/session", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      ...params,
    }),
  });

  if (!response.ok) {
    const payload = await safeJson(response);
    const message =
      (payload as { error?: { message?: string } })?.error?.message ??
      `결제 세션 생성 실패 (${response.status})`;
    throw new Error(message);
  }

  const payload = (await response.json()) as {
    success: boolean;
    session: PaymentSession;
  };

  if (!payload.success || !payload.session) {
    throw new Error("결제 세션 응답이 올바르지 않습니다.");
  }

  return payload.session;
}

export async function fetchPaymentStatus(
  provider: PaymentProvider,
  orderId: string,
  reference: string,
): Promise<OrderStatusPayload> {
  const url = new URL("/api/payments/status", window.location.origin);
  url.searchParams.set("provider", provider);
  url.searchParams.set("orderId", orderId);
  url.searchParams.set("reference", reference);

  const response = await fetch(url.toString(), {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
    cache: "no-store",
  });

  if (!response.ok) {
    const payload = await safeJson(response);
    const message =
      (payload as { error?: { message?: string } })?.error?.message ??
      `결제 상태 조회 실패 (${response.status})`;
    throw new Error(message);
  }

  const payload = (await response.json()) as {
    success: boolean;
    status: OrderStatusPayload;
  };

  if (!payload.success || !payload.status) {
    throw new Error("결제 상태 응답이 올바르지 않습니다.");
  }

  return payload.status;
}

async function safeJson(response: Response): Promise<unknown> {
  try {
    return await response.json();
  } catch {
    return null;
  }
}
