"use client";

import { useEffect, useState } from "react";
import { useRouter, usePathname } from "next/navigation";
import {
  decodeJwtPayload,
  getAccessToken,
  refreshAccessToken,
} from "@/lib/auth";

export default function SellerRouteGuard({
  children,
}: {
  children: React.ReactNode;
}) {
  const router = useRouter();
  const pathname = usePathname();
  const [ok, setOk] = useState(false);

  useEffect(() => {
    let cancelled = false;
    async function check() {
      let token = getAccessToken();
      if (!token) {
        const refreshed = await refreshAccessToken();
        if (!refreshed) return redirectToLogin();
        token = refreshed;
      }

      const payload = decodeJwtPayload(token);
      const expMs = payload?.exp ? payload.exp * 1000 : undefined;
      const isExpired = !!expMs && Date.now() >= expMs;
      if (isExpired) {
        const refreshed = await refreshAccessToken();
        if (!refreshed) return redirectToLogin();
        token = refreshed;
      }

      // 백엔드 JWT에는 판매자 여부 클레임이 아직 없으므로, 액세스 토큰이 유효하면 진입을 허용한다.
      // 실제 권한 검증은 API 호출 시 서버가 담당한다.
      if (!cancelled) setOk(true);
    }
    function redirectToLogin() {
      if (!cancelled)
        router.replace(
          "/admin/auth/login?next=" + encodeURIComponent(pathname),
        );
    }
    check();
    return () => {
      cancelled = true;
    };
  }, [router, pathname]);

  if (!ok) return <div className="p-6 text-gray-500">확인 중...</div>;
  return <>{children}</>;
}
