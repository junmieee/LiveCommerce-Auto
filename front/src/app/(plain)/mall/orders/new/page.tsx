import OrderCheckout from "@/components/mall/OrderCheckout";
import { buildMockMallProduct, fetchMallProductDetail } from "@/lib/products";
import { MallProductDetail } from "@/types/mall";
import { notFound } from "next/navigation";

type SearchParams = {
  productId?: string;
  quantity?: string;
  seller?: string;
  provider?: string;
};

export const dynamic = "force-dynamic";

export default async function MallOrderNewPage({
  searchParams,
}: {
  searchParams?: SearchParams;
}) {
  const productId = searchParams?.productId?.trim();
  if (!productId) notFound();

  const sellerSlug = searchParams?.seller?.trim() || undefined;
  const quantityParam = searchParams?.quantity?.trim();
  const defaultProvider = searchParams?.provider?.trim();

  let product: MallProductDetail | null = await fetchMallProductDetail(
    productId,
    { sellerSlug },
  );

  if (!product) {
    product = buildMockMallProduct(productId, sellerSlug);
  }

  const initialQuantity = normalizeQuantity(quantityParam);

  return (
    <OrderCheckout
      product={product}
      initialQuantity={initialQuantity}
      defaultProvider={defaultProvider}
    />
  );
}

function normalizeQuantity(value?: string): number {
  if (!value) return 1;
  const parsed = Number.parseInt(value, 10);
  if (!Number.isFinite(parsed)) return 1;
  if (parsed <= 0) return 1;
  return Math.min(parsed, 99);
}
