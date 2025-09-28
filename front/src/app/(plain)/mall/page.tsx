import { notFound } from "next/navigation";
import SellerMall from "@/components/mall/SellerMall";

type Product = {
  id: string;
  name: string;
  price: number;
  imageUrl: string;
  createdAt?: string;
  sales?: number;
};

type SellerData = {
  seller: string;
  sellerName: string;
  bannerImageUrl?: string;
  products: Product[];
};

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
  const base = process.env.NEXT_PUBLIC_API_BASE_URL;

  if (base) {
    try {
      const res = await fetch(
        `${base}/sellers/${encodeURIComponent(seller)}/mall`,
        { cache: "no-store" },
      );
      if (res.ok) {
        data = (await res.json()) as SellerData;
      }
    } catch {
      // Silently fall back to mock when API is unreachable in local dev
    }
  }

  if (!data) data = mockData(seller);

  return <SellerMall data={data} />;
}
