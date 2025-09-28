// Lightweight fetch wrapper that attaches access token and auto-refreshes on 401
import { getAccessToken, refreshAccessToken } from "./auth";

export async function apiFetch(
  input: RequestInfo | URL,
  init?: RequestInit,
): Promise<Response> {
  const url =
    typeof input === "string" || input instanceof URL ? input : input.url;

  const target =
    typeof url === "string" && url.startsWith("http") ? url : (url as string);

  const token = getAccessToken();
  const headers = new Headers(init?.headers || {});
  if (token && !headers.has("Authorization")) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  const doFetch = () =>
    fetch(target, {
      ...init,
      headers,
    });

  let res = await doFetch();
  if (res.status !== 401) return res;

  const newAccess = await refreshAccessToken();
  if (!newAccess) return res;

  const retryHeaders = new Headers(init?.headers || {});
  retryHeaders.set("Authorization", `Bearer ${newAccess}`);
  res = await fetch(target, { ...init, headers: retryHeaders });
  return res;
}
