export function DashboardCard({
  children,
  title,
}: {
  children: React.ReactNode;
  title: string;
}) {
  return (
    <div className="rounded-2xl bg-white px-4 py-4 shadow-sm sm:px-6">
      <div className="flex h-14 items-center border-b border-[#E5E5E5] text-lg text-gray-700">
        {title}
      </div>
      <div className="py-4 text-gray-800">{children}</div>
    </div>
  );
}
