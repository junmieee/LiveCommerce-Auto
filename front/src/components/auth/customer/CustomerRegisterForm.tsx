"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";

interface FormState {
  email: string;
  name: string;
  password: string;
  passwordConfirm: string;
}

export default function CustomerRegisterForm() {
  const router = useRouter();
  const [form, setForm] = useState<FormState>({
    email: "",
    name: "",
    password: "",
    passwordConfirm: "",
  });
  const [errors, setErrors] = useState<Partial<FormState>>({});
  const [serverError, setServerError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const onChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
    if (errors[name as keyof FormState]) {
      setErrors((prev) => ({ ...prev, [name]: undefined }));
    }
    setServerError(null);
  };

  const validate = () => {
    const next: Partial<FormState> = {};
    if (!form.email) next.email = "이메일을 입력해주세요";
    else if (!/\S+@\S+\.\S+/.test(form.email))
      next.email = "올바른 이메일 형식이 아닙니다";
    if (!form.name) next.name = "이름을 입력해주세요";
    if (!form.password) next.password = "비밀번호를 입력해주세요";
    else if (form.password.length < 8)
      next.password = "비밀번호는 8자 이상이어야 합니다";
    if (!form.passwordConfirm)
      next.passwordConfirm = "비밀번호 확인을 입력해주세요";
    else if (form.password !== form.passwordConfirm)
      next.passwordConfirm = "비밀번호가 일치하지 않습니다";
    setErrors(next);
    return Object.keys(next).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;

    try {
      setLoading(true);
      setServerError(null);
      const res = await fetch("http://localhost:8081/api/users/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          email: form.email,
          name: form.name,
          password: form.password,
          provider: "local",
          is_seller: false,
        }),
      });

      if (res.status === 201) {
        const data = await res.json();
        console.log("회원가입 성공:", data);
        router.push("/auth/login");
        return;
      }

      const data = await res.json().catch(() => ({}));
      if (res.status === 409) {
        setServerError("이미 사용 중인 이메일입니다.");
      } else {
        console.log(data);
        setServerError(data?.message || "회원가입에 실패했습니다.");
      }
    } catch {
      setServerError("네트워크 오류가 발생했습니다.");
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
          이메일 *
        </label>
        <input
          id="email"
          name="email"
          type="email"
          value={form.email}
          onChange={onChange}
          className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
          placeholder="example@email.com"
        />
        {errors.email && (
          <p className="mt-1 text-sm text-red-600">{errors.email}</p>
        )}
      </div>

      <div>
        <label
          htmlFor="name"
          className="block text-sm font-medium text-gray-700"
        >
          이름 *
        </label>
        <input
          id="name"
          name="name"
          type="text"
          value={form.name}
          onChange={onChange}
          className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
          placeholder="홍길동"
        />
        {errors.name && (
          <p className="mt-1 text-sm text-red-600">{errors.name}</p>
        )}
      </div>

      <div>
        <label
          htmlFor="password"
          className="block text-sm font-medium text-gray-700"
        >
          비밀번호 *
        </label>
        <input
          id="password"
          name="password"
          type="password"
          value={form.password}
          onChange={onChange}
          className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
          placeholder="8자 이상 입력해주세요"
        />
        {errors.password && (
          <p className="mt-1 text-sm text-red-600">{errors.password}</p>
        )}
      </div>

      <div>
        <label
          htmlFor="passwordConfirm"
          className="block text-sm font-medium text-gray-700"
        >
          비밀번호 재확인 *
        </label>
        <input
          id="passwordConfirm"
          name="passwordConfirm"
          type="password"
          value={form.passwordConfirm}
          onChange={onChange}
          className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
          placeholder="비밀번호를 다시 입력해주세요"
        />
        {errors.passwordConfirm && (
          <p className="mt-1 text-sm text-red-600">{errors.passwordConfirm}</p>
        )}
      </div>

      {serverError && <p className="text-sm text-red-600">{serverError}</p>}

      <button
        type="submit"
        disabled={loading}
        className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 disabled:opacity-60"
      >
        {loading ? "가입 중..." : "회원가입"}
      </button>
    </form>
  );
}
