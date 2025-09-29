export default function ChromeGroupLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  // Shared wrapper without sidebars/headers; specific routes add their own
  return <>{children}</>;
}
