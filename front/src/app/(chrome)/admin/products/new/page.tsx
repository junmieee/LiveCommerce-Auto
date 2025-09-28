import SellerRouteGuard from "@/components/auth/SellerRouteGuard";
import ProductCreateForm from "@/components/seller/ProductCreateForm";

export default function NewProductPage() {
  return (
    <SellerRouteGuard>
      <div className="py-12">
        <div className="mx-auto max-w-6xl px-6 lg:px-0">
          <div className="mb-10">
            <div className="flex flex-wrap items-center gap-2 text-sm text-gray-400">
              <span>상품 관리</span>
              <span className="text-gray-300">/</span>
              <span className="text-gray-500">상품 등록</span>
            </div>
            <h1 className="mt-3 text-3xl font-semibold text-gray-900">
              상품 등록
            </h1>
            <p className="mt-2 text-sm text-gray-500">
              신규 상품 정보를 입력하고, 노출 상태까지 한 번에 설정하세요.
            </p>
          </div>
          <ProductCreateForm />
        </div>
      </div>
    </SellerRouteGuard>
  );
}
