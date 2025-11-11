import { DashboardCard } from "@/components/admin/DashboardCard";

export default function Page() {
  return (
    <div className="grid gap-8 py-10">
      <div className="grid gap-6 lg:grid-cols-2">
        <DashboardCard title="주문배송">1</DashboardCard>
        <DashboardCard title="공지사항">1</DashboardCard>
      </div>
      <DashboardCard title="통계">1</DashboardCard>
      <DashboardCard title="최근주문">1</DashboardCard>
    </div>
  );
}
