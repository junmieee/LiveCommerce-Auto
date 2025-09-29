import { DashboardCard } from "@/components/admin/DashboardCard";

export default function Page() {
  return (
    <div className="grid grid-rows-3 gap-12 py-14 px-20 ">
      <div className="h-[400px] grid grid-cols-2 gap-12">
        <DashboardCard title="주문배송">1</DashboardCard>
        <DashboardCard title="공지사항">1</DashboardCard>
      </div>
      <DashboardCard title="통계">1</DashboardCard>
      <DashboardCard title="최근주문">1</DashboardCard>
    </div>
  );
}
