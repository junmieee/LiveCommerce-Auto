import { MallProductDetail } from "@/types/mall";

type ApiProductResponse = {
  id: number;
  sellerId?: number | null;
  name: string;
  description?: string | null;
  descriptionHtml?: string | null;
  price: number | string;
  stockQuantity?: number | null;
  isActive?: boolean;
  createdAt?: string | null;
  updatedAt?: string | null;
  thumbnailImageKey?: string | null;
  mainImageKeys?: string[] | null;
};

function parsePrice(value: number | string): number {
  if (typeof value === "number") {
    return Number.isFinite(value) ? value : 0;
  }
  const parsed = Number.parseFloat(value);
  return Number.isFinite(parsed) ? parsed : 0;
}

function sanitizeImageList(list?: string[] | null): string[] {
  if (!Array.isArray(list)) return [];
  return list.filter((item) => item && item.trim().length > 0);
}

function formatSellerName(
  raw?: string,
  sellerId?: number | null,
): string | undefined {
  if (raw && raw.length > 0) return `${raw} 스토어`;
  if (sellerId !== undefined && sellerId !== null) {
    return `판매자 #${sellerId} 스토어`;
  }
  return undefined;
}

export async function fetchMallProductDetail(
  productId: string,
  options?: { sellerSlug?: string },
): Promise<MallProductDetail | null> {
  const base = process.env.NEXT_PUBLIC_API_BASE_URL;
  if (!base) return null;

  try {
    const res = await fetch(`${base}/products/${productId}`, {
      cache: "no-store",
    });
    if (!res.ok) return null;

    const data = (await res.json()) as ApiProductResponse;
    const priceNumber = parsePrice(data.price);
    const mainImages = sanitizeImageList(data.mainImageKeys);
    const primaryImage =
      (data.thumbnailImageKey && data.thumbnailImageKey.trim().length > 0
        ? data.thumbnailImageKey
        : mainImages[0]) ?? null;
    const gallery = mainImages.length
      ? mainImages
      : primaryImage
        ? [primaryImage]
        : undefined;

    return {
      id: String(data.id),
      name: data.name,
      price: priceNumber,
      description: data.descriptionHtml ?? data.description,
      sellerId: data.sellerId ?? null,
      sellerName: formatSellerName(options?.sellerSlug, data.sellerId),
      sellerCode: data.sellerId ? `S${data.sellerId}-${data.id}` : undefined,
      shippingFee: 3000,
      shippingInfo: "평일 오후 2시 이전 주문 시 당일 출고",
      stockQuantity: data.stockQuantity ?? null,
      imageUrl: primaryImage ?? undefined,
      gallery,
      badges: data.isActive === false ? ["일시 품절"] : undefined,
      createdAt: data.createdAt ?? null,
    };
  } catch (error) {
    console.info("상품 상세 API 호출 실패", error);
    return null;
  }
}

export function buildMockMallProduct(
  productId: string,
  sellerSlug?: string,
): MallProductDetail {
  return {
    id: productId,
    name: "방가가방 Bag",
    price: 32000,
    description:
      "가볍고 튼튼한 데일리 토트백입니다.\n부드러운 면 소재로 제작되어 데일리룩에 잘 어울립니다.\nA4 서류와 노트북을 여유롭게 수납할 수 있어 출퇴근용으로도 좋아요.",
    sellerName: sellerSlug ? `${sellerSlug} 스토어` : "방가방 공식 스토어",
    sellerCode: `MOCK-${productId}`,
    shippingFee: 3000,
    shippingInfo: "CJ대한통운 / 당일배송(평일 14시 이전 주문)",
    stockQuantity: 47,
    imageUrl:
      "https://images.unsplash.com/photo-1542291026-7eec264c27ff?q=80&w=1600&auto=format&fit=crop",
    gallery: [
      "https://images.unsplash.com/photo-1542291026-7eec264c27ff?q=80&w=1600&auto=format&fit=crop",
      "https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?q=80&w=1600&auto=format&fit=crop",
      "https://images.unsplash.com/photo-1514996937319-344454492b37?q=80&w=1600&auto=format&fit=crop",
      "https://images.unsplash.com/photo-1578898886200-486e7e3b9bc5?q=80&w=1600&auto=format&fit=crop",
    ],
    badges: ["MD 추천", "FAST 배송"],
    createdAt: new Date().toISOString(),
  };
}
