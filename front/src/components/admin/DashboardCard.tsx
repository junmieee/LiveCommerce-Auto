export function DashboardCard({
  children,
  title,
}: {
  children: React.ReactNode;
  title: string;
}) {
  return (
    <div className="bg-white rounded-lg px-4">
      <div className="border-b border-[#E5E5E5] h-14 text-lg text-gray-700 flex items-center pl-3">
        {title}
      </div>
      <div>{children}</div>
    </div>
  );
}
