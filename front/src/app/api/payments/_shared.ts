import { OrderStatus, PaymentSession } from "@/types/mall";

const sessionKey = "__mockPaymentSessions";
const statusKey = "__mockPaymentStatuses";

type MockStatus = {
  status: OrderStatus;
  updatedAt: number;
};

declare global {
  var __mockPaymentSessions: Map<string, PaymentSession> | undefined;
  var __mockPaymentStatuses: Map<string, MockStatus> | undefined;
}

export function getMockStore(): Map<string, PaymentSession> {
  if (!globalThis[sessionKey]) {
    globalThis[sessionKey] = new Map<string, PaymentSession>();
  }
  return globalThis[sessionKey]!;
}

function getStatusStore(): Map<string, MockStatus> {
  if (!globalThis[statusKey]) {
    globalThis[statusKey] = new Map<string, MockStatus>();
  }
  return globalThis[statusKey]!;
}

export function saveMockSession(
  orderId: string,
  session: PaymentSession,
): void {
  const store = getMockStore();
  store.set(orderId, session);
  const statuses = getStatusStore();
  statuses.set(orderId, { status: "PENDING", updatedAt: Date.now() });
}

export function getMockSession(orderId: string): PaymentSession | undefined {
  const store = getMockStore();
  return store.get(orderId);
}

export function updateMockStatus(orderId: string, status: OrderStatus): void {
  const statuses = getStatusStore();
  const existing = statuses.get(orderId);
  statuses.set(orderId, {
    status,
    updatedAt: Date.now(),
    ...(existing ?? {}),
  });
}

export function getMockStatus(
  orderId: string,
): { status: OrderStatus; updatedAt: number } | undefined {
  const statuses = getStatusStore();
  return statuses.get(orderId);
}

export function isMockMode(): boolean {
  if (process.env.PAYMENT_FORCE_MOCK === "true") return true;
  const tossKey = process.env.TOSS_SECRET_KEY;
  const kakaoKey = process.env.KAKAOPAY_ADMIN_KEY;
  return !tossKey || !kakaoKey;
}
