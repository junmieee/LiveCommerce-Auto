export default function OrderSuccessPage() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-gray-50 px-4 text-center text-gray-700">
      <div className="rounded-3xl bg-white p-10 shadow-sm">
        <h1 className="text-2xl font-semibold text-gray-900">
          결제가 완료되었습니다.
        </h1>
        <p className="mt-4 text-sm text-gray-500">
          창을 닫고 진행 중인 주문 화면에서 완료 상태를 확인해주세요.
        </p>
      </div>
    </div>
  );
}
