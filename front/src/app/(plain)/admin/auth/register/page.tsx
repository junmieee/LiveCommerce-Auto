import SellerRegisterForm from "@/components/auth/seller/SellerRegisterForm";

export default function SellerRegisterPage() {
  return (
    <div className="min-h-screen bg-gray-50 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
          판매자 회원가입
        </h2>
      </div>
      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
        <div className="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10">
          {/* 간단 폼으로 1차 연동. 이후 필요 시 단계형 폼 복구 */}
          <SellerRegisterForm />
        </div>
      </div>
    </div>
  );
}
