export const runtime = "nodejs";

import { NextResponse } from "next/server";
import { OrderStatus, OrderStatusPayload, PaymentProvider } from "@/types/mall";
import {
  getMockSession,
  getMockStatus,
  isMockMode,
  updateMockStatus,
} from "../_shared";

type KakaoOrderResponse = {
  status?: string;
  approved_at?: string;
  canceled_at?: string;
  created_at?: string;
};

type TossPaymentLinkStatusResponse = {
  paymentLinkId: string;
  orderId: string;
  status: string;
  approvedAt?: string;
  cancelledAt?: string;
  expiresAt?: string;
};

export async function GET(request: Request) {
  const url = new URL(request.url);
  const provider = url.searchParams.get("provider") as PaymentProvider | null;
  const orderId = url.searchParams.get("orderId");
  const reference = url.searchParams.get("reference");

  if (!provider || (provider !== "kakaopay" && provider !== "toss")) {
    return NextResponse.json(
      { success: false, error: { message: "provider 파라미터 오류" } },
      { status: 400 },
    );
  }

  if (!orderId || !reference) {
    return NextResponse.json(
      { success: false, error: { message: "orderId/reference 누락" } },
      { status: 400 },
    );
  }

  if (isMockMode()) {
    const session = getMockSession(orderId);
    if (!session) {
      return NextResponse.json(
        { success: false, error: { message: "세션을 찾을 수 없습니다." } },
        { status: 404 },
      );
    }
    let record = getMockStatus(orderId);
    if (record?.status === "PENDING") {
      const elapsed = Date.now() - record.updatedAt;
      if (elapsed > 5000) {
        updateMockStatus(orderId, "COMPLETED");
        record = getMockStatus(orderId);
      }
    }
    const status = record?.status ?? "PENDING";
    const payload: OrderStatusPayload = {
      orderId,
      provider: session.provider,
      status,
      approvedAt: status === "COMPLETED" ? new Date().toISOString() : undefined,
      updatedAt: new Date().toISOString(),
    };
    return NextResponse.json({ success: true, status: payload });
  }

  try {
    let payload: OrderStatusPayload;
    if (provider === "kakaopay") {
      payload = await queryKakaoStatus(orderId, reference);
    } else {
      payload = await queryTossStatus(orderId, reference);
    }
    return NextResponse.json({ success: true, status: payload });
  } catch (error) {
    console.error("결제 상태 조회 실패", error);
    return NextResponse.json(
      {
        success: false,
        error: {
          message:
            error instanceof Error
              ? error.message
              : "결제 상태 조회 중 오류가 발생했습니다.",
        },
      },
      { status: 500 },
    );
  }
}

async function queryKakaoStatus(
  orderId: string,
  tid: string,
): Promise<OrderStatusPayload> {
  const adminKey = process.env.KAKAOPAY_ADMIN_KEY;
  const cid = process.env.KAKAOPAY_CID ?? "TC0ONETIME";
  if (!adminKey) {
    throw new Error("카카오페이 Admin Key가 설정되지 않았습니다.");
  }

  const form = new URLSearchParams();
  form.append("cid", cid);
  form.append("tid", tid);

  const response = await fetch("https://kapi.kakao.com/v1/payment/order", {
    method: "POST",
    headers: {
      Authorization: `KakaoAK ${adminKey}`,
      "Content-Type": "application/x-www-form-urlencoded;charset=utf-8",
    },
    body: form.toString(),
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(`카카오페이 결제 상태 조회 실패: ${text}`);
  }

  const data = (await response.json()) as KakaoOrderResponse;
  const resolvedStatus = mapKakaoStatus(data.status);

  return {
    orderId,
    provider: "kakaopay",
    status: resolvedStatus,
    approvedAt: data.approved_at,
    updatedAt: data.canceled_at ?? data.approved_at ?? data.created_at,
  };
}

async function queryTossStatus(
  orderId: string,
  paymentLinkId: string,
): Promise<OrderStatusPayload> {
  const secretKey = process.env.TOSS_SECRET_KEY;
  if (!secretKey) {
    throw new Error("토스페이먼츠 Secret Key가 설정되지 않았습니다.");
  }

  const auth = Buffer.from(`${secretKey}:`).toString("base64");

  const response = await fetch(
    `https://api.tosspayments.com/v1/payment-links/${paymentLinkId}`,
    {
      method: "GET",
      headers: {
        Authorization: `Basic ${auth}`,
        "Content-Type": "application/json",
      },
    },
  );

  if (!response.ok) {
    const text = await response.text();
    throw new Error(`토스페이먼츠 결제 상태 조회 실패: ${text}`);
  }

  const data = (await response.json()) as TossPaymentLinkStatusResponse;
  const resolvedStatus = mapTossStatus(data.status);

  return {
    orderId: data.orderId ?? orderId,
    provider: "toss",
    status: resolvedStatus,
    approvedAt: data.approvedAt,
    updatedAt: data.cancelledAt ?? data.approvedAt ?? data.expiresAt,
  };
}

function mapKakaoStatus(status?: string | null): OrderStatus {
  switch (status) {
    case "SUCCESS_PAYMENT":
    case "PART_CANCEL_PAYMENT":
      return "COMPLETED";
    case "CANCEL_PAYMENT":
      return "CANCELED";
    case "FAIL_PAYMENT":
      return "FAILED";
    default:
      return "PENDING";
  }
}

function mapTossStatus(status?: string | null): OrderStatus {
  switch (status) {
    case "PAID":
    case "DONE":
      return "COMPLETED";
    case "CANCELED":
    case "CANCELLED":
      return "CANCELED";
    case "EXPIRED":
    case "FAILED":
      return "FAILED";
    default:
      return "PENDING";
  }
}
