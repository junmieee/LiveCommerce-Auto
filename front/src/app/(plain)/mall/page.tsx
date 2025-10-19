import { notFound } from "next/navigation";
import SellerMall from "@/components/mall/SellerMall";

type Product = {
  id: string;
  name: string;
  price: number;
  imageUrl?: string | null;
  createdAt?: string;
  sales?: number;
};

type SellerData = {
  seller: string;
  sellerName: string;
  bannerImageUrl?: string;
  products: Product[];
};

type ProductListApiResponse = {
  success?: boolean;
  data?: Array<{
    id: number;
    sellerId?: number | null;
    name: string;
    price: number | string;
    thumbnailImageKey?: string | null;
    mainImageKeys?: string[] | null;
    createdAt?: string | null;
    updatedAt?: string | null;
    isActive?: boolean | null;
  }>;
  total?: number;
  page?: number;
  limit?: number;
};

function parsePrice(value: number | string | undefined): number {
  if (typeof value === "number") {
    return Number.isFinite(value) ? value : 0;
  }
  if (typeof value === "string") {
    const parsed = Number.parseFloat(value);
    return Number.isFinite(parsed) ? parsed : 0;
  }
  return 0;
}

function pickImage(product: {
  thumbnailImageKey?: string | null;
  mainImageKeys?: string[] | null;
}): string | null {
  if (
    product.thumbnailImageKey &&
    product.thumbnailImageKey.trim().length > 0
  ) {
    return product.thumbnailImageKey;
  }
  const firstMain = product.mainImageKeys?.find(
    (img) => img && img.trim().length > 0,
  );
  return firstMain ?? null;
}

function deriveSellerId(raw: string): number | null {
  const direct = Number.parseInt(raw, 10);
  if (Number.isFinite(direct)) return direct;
  const matched = raw.match(/(\d+)/);
  if (!matched) return null;
  const extracted = Number.parseInt(matched[1] ?? "", 10);
  return Number.isFinite(extracted) ? extracted : null;
}

async function fetchSellerData(
  sellerParam: string,
): Promise<SellerData | null> {
  const base = process.env.NEXT_PUBLIC_API_BASE_URL;
  if (!base) return null;

  const sellerId = deriveSellerId(sellerParam);
  const perPage = 100;
  let page = 1;
  let total = Number.POSITIVE_INFINITY;
  const collected: Product[] = [];

  while ((page - 1) * perPage < total) {
    const url = new URL(`${base}/products`);
    url.searchParams.set("page", String(page));
    url.searchParams.set("limit", String(perPage));
    url.searchParams.set("sort", "created_at_desc");

    let response: Response;
    try {
      response = await fetch(url.toString(), { cache: "no-store" });
    } catch {
      break;
    }

    if (!response.ok) break;

    let payload: ProductListApiResponse;
    try {
      payload = (await response.json()) as ProductListApiResponse;
    } catch {
      break;
    }

    total = payload.total ?? 0;
    const items = payload.data ?? [];

    const matches = items.filter((item) => {
      if (item.sellerId === undefined || item.sellerId === null) return false;
      if (sellerId !== null) return item.sellerId === sellerId;
      return String(item.sellerId) === sellerParam;
    });

    collected.push(
      ...matches.map((item) => ({
        id: String(item.id),
        name: item.name,
        price: parsePrice(item.price),
        imageUrl: pickImage(item) ?? undefined,
        createdAt: item.createdAt ?? undefined,
        sales: undefined,
      })),
    );

    if (items.length < perPage) break;
    if (page * perPage >= total) break;
    page += 1;
  }

  if (!collected.length) {
    return {
      seller: sellerParam,
      sellerName: formatSellerName(sellerParam, sellerId),
      bannerImageUrl: undefined,
      products: [],
    };
  }

  return {
    seller: sellerParam,
    sellerName: formatSellerName(sellerParam, sellerId),
    bannerImageUrl: undefined,
    products: collected,
  };
}

function formatSellerName(raw: string, sellerId: number | null): string {
  if (sellerId !== null) {
    return `판매자 #${sellerId} 스토어`;
  }
  return `${raw} 스토어`;
}

function mockData(seller: string): SellerData {
  return {
    seller,
    sellerName: `${seller} 스토어`,
    bannerImageUrl: undefined,
    products: Array.from({ length: 8 }).map((_, i) => ({
      id: `${i + 1}`,
      name: "가방방가",
      price: 20000,
      imageUrl:
        "https://images.unsplash.com/photo-1547949003-9792a18a2601?q=80&w=800&auto=format&fit=crop",
      createdAt: new Date(Date.now() - i * 86400000).toISOString(),
      sales: 100 - i * 3,
    })),
  };
}

export const dynamic = "force-dynamic";

export default async function MallPage({
  searchParams,
}: {
  searchParams: { seller?: string };
}) {
  const seller = searchParams?.seller?.trim();
  if (!seller) notFound();

  let data: SellerData | null = null;

  try {
    data = await fetchSellerData(seller);
  } catch {
    // ignore errors and fall back to mock data below
  }

  if (!data) {
    data = mockData(seller);
  }

  return <SellerMall data={data} />;
}
