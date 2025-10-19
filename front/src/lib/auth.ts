// Client-side auth helpers for access/refresh token handling
// Note: In this project, backend expects POST /api/users/refresh with { userId, refreshToken }
// and returns { success, token } where token is the new access token.

export const ACCESS_TOKEN_KEY = "auth_token";
export const REFRESH_TOKEN_KEY = "refresh_token";
export const USER_ID_KEY = "user_id";
export const SELLER_ID_KEY = "seller_id";

type JwtPayload = {
  sub?: string; // userId stored in subject
  email?: string;
  exp?: number;
  [key: string]: unknown;
};

export function decodeJwtPayload(token: string): JwtPayload | null {
  try {
    const parts = token.split(".");
    if (parts.length !== 3) return null;
    const payload = parts[1].replace(/-/g, "+").replace(/_/g, "/");
    const decoded =
      typeof atob === "function"
        ? atob(payload)
        : Buffer.from(payload, "base64").toString("utf8");
    return JSON.parse(decoded);
  } catch {
    return null;
  }
}

export function setTokens(accessToken?: string, refreshToken?: string) {
  if (typeof window === "undefined") return;
  try {
    if (accessToken) localStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
    if (refreshToken) {
      localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
      const payload = decodeJwtPayload(refreshToken);
      const sub = payload?.sub;
      if (sub) localStorage.setItem(USER_ID_KEY, sub);
    }
  } catch {
    // ignore storage errors (e.g., Safari private mode)
  }
}

export function setActiveSellerId(sellerId?: string | number | null) {
  if (typeof window === "undefined") return;
  try {
    if (sellerId === undefined || sellerId === null) {
      localStorage.removeItem(SELLER_ID_KEY);
      return;
    }
    localStorage.setItem(SELLER_ID_KEY, String(sellerId));
  } catch {
    // ignore storage errors (e.g., Safari private mode)
  }
}

export function getAccessToken(): string | null {
  if (typeof window === "undefined") return null;
  try {
    return localStorage.getItem(ACCESS_TOKEN_KEY);
  } catch {
    return null;
  }
}

export function getRefreshToken(): string | null {
  if (typeof window === "undefined") return null;
  try {
    return localStorage.getItem(REFRESH_TOKEN_KEY);
  } catch {
    return null;
  }
}

export function getUserId(): string | null {
  if (typeof window === "undefined") return null;
  try {
    const rt = getRefreshToken();
    if (rt) {
      const sub = decodeJwtPayload(rt)?.sub;
      if (sub) return String(sub);
    }

    return localStorage.getItem(USER_ID_KEY);
  } catch {
    return null;
  }
}

export function getSellerId(): string | null {
  if (typeof window === "undefined") return null;
  try {
    return localStorage.getItem(SELLER_ID_KEY);
  } catch {
    return null;
  }
}

export function clearTokens() {
  if (typeof window === "undefined") return;
  try {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    localStorage.removeItem(USER_ID_KEY);
    localStorage.removeItem(SELLER_ID_KEY);
  } catch {
    // ignore
  }
}

export async function refreshAccessToken(): Promise<string | null> {
  const refreshToken = getRefreshToken();
  const userId = getUserId();
  if (!refreshToken || !userId) return null;

  try {
    const res = await fetch("/api/users/refresh", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ userId: Number(userId), refreshToken }),
    });
    if (!res.ok) return null;
    const data = (await res.json()) as { success?: boolean; token?: string };
    const newAccess = data?.token;
    if (newAccess) setTokens(newAccess, undefined);
    return newAccess ?? null;
  } catch {
    return null;
  }
}
