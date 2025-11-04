export default function OrderFailPage() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-gray-50 px-4 text-center text-gray-700">
      <div className="rounded-3xl bg-white p-10 shadow-sm">
        <h1 className="text-2xl font-semibold text-gray-900">
          결제에 실패했습니다.
        </h1>
        <p className="mt-4 text-sm text-gray-500">
          네트워크 상태를 확인한 뒤 주문 화면으로 돌아가 다시 시도해주세요.
        </p>
      </div>
    </div>
  );
}
