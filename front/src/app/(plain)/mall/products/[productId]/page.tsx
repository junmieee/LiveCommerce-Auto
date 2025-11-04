import ProductDetailView from "@/components/mall/ProductDetailView";
import { buildMockMallProduct, fetchMallProductDetail } from "@/lib/products";
import { MallProductDetail } from "@/types/mall";
import { notFound } from "next/navigation";

export const dynamic = "force-dynamic";

export default async function ProductDetailPage({
  params,
  searchParams,
}: {
  params: { productId?: string };
  searchParams?: { seller?: string };
}) {
  const productId = params.productId?.trim();
  if (!productId) notFound();

  const sellerSlug = searchParams?.seller?.trim() || undefined;

  let detail: MallProductDetail | null = await fetchMallProductDetail(
    productId,
    { sellerSlug },
  );

  if (!detail) {
    detail = buildMockMallProduct(productId, sellerSlug);
  }

  return <ProductDetailView detail={detail} sellerSlug={sellerSlug} />;
}
