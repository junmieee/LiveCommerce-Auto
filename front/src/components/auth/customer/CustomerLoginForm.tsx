"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";

export default function CustomerLoginForm() {
  const router = useRouter();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (!email || !password) {
      setError("이메일과 비밀번호를 입력해주세요.");
      return;
    }

    try {
      setLoading(true);
      // Use Next.js rewrite to avoid cross-origin and keep a single base URL
      const res = await fetch("/api/users/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      });

      if (!res.ok) {
        const data = await res.json().catch(() => ({}));
        setError(data?.message || "로그인에 실패했습니다.");
        return;
      }

      const data = await res.json();
      const token = data?.token as string | undefined;
      const refreshToken = data?.refreshToken as string | undefined;
      if (token) {
        try {
          // Store access token and refresh token; userId is derived from refresh token
          localStorage.setItem("auth_token", token);
          if (refreshToken) {
            localStorage.setItem("refresh_token", refreshToken);
            // Derive and store user id from refresh token subject for refresh calls
            const parts = refreshToken.split(".");
            if (parts.length === 3) {
              const payload = parts[1].replace(/-/g, "+").replace(/_/g, "/");
              const decoded = atob(payload);
              const parsed = JSON.parse(decoded);
              if (parsed?.sub) {
                localStorage.setItem("user_id", String(parsed.sub));
              }
            }
          }
        } catch {}
      }
      router.push("/");
    } catch {
      setError("네트워크 오류가 발생했습니다.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      <div>
        <label
          htmlFor="email"
          className="block text-sm font-medium text-gray-700"
        >
          이메일
        </label>
        <input
          id="email"
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
          placeholder="example@email.com"
        />
      </div>

      <div>
        <label
          htmlFor="password"
          className="block text-sm font-medium text-gray-700"
        >
          비밀번호
        </label>
        <input
          id="password"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
          placeholder="비밀번호를 입력해주세요"
        />
      </div>

      {error && <p className="text-sm text-red-600">{error}</p>}

      <button
        type="submit"
        disabled={loading}
        className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 disabled:opacity-60"
      >
        {loading ? "로그인 중..." : "로그인"}
      </button>
    </form>
  );
}
