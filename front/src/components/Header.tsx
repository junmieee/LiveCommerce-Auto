import { Bell, CirclePlus } from "lucide-react";
export default function Header({ isOpen }) {
  return (
    <header className="flex items-center justify-between px-6 py-3 border-b bg-white fixed w-full top-0 right-0">
      <div className="flex items-center gap-1">
        <span className="text-lg font-semibold">대시보드</span>
      </div>

      <div className="flex items-center gap-3">
        {/* <div className="w-8 h-8 rounded-full border flex items-center justify-center"> */}
        {/* <User className="w-4 h-4 text-gray-500" /> */}
        {/* </div> */}
        <div className="w-26 h-12 rounded-full border flex items-center justify-between px-4">
          <div className="relative">
            <Bell className="w-6 h-6 text-gray-500" />
            <span className="absolute top-1 right-1 w-2 h-2 bg-red-500 rounded-full" />
          </div>
          <span className="text-gray-400 whitespace-nowrap">알림</span>
        </div>
        <div>
          <button className="flex items-center justify-center w-36 h-12 gap-1 px-2 py-1 text-lg text-white bg-[#F7A072] rounded-full hover:bg-orange-500 whitespace-nowrap duration-300">
            <span>상품 추가</span>
            <CirclePlus fill="white" color="#F7A072" size={40} />
          </button>
        </div>
      </div>
    </header>
  );
}
