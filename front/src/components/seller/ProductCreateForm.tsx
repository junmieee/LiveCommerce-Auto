"use client";

import { useEffect, useMemo, useState } from "react";
import type {
  ChangeEvent,
  FormEvent,
  InputHTMLAttributes,
  ReactNode,
  TextareaHTMLAttributes,
} from "react";
import { useRouter } from "next/navigation";
import { apiFetch } from "@/lib/apiClient";
import { getUserId } from "@/lib/auth";

type FormState = {
  name: string;
  description: string;
  price: string;
  stockQuantity: string;
  imageUrl: string;
  isActive: boolean;
};

type FormErrors = Partial<FormState> & { base?: string };

export default function ProductCreateForm() {
  const router = useRouter();
  const sellerId = useMemo(() => getUserId(), []);

  const [form, setForm] = useState<FormState>({
    name: "",
    description: "",
    price: "0",
    stockQuantity: "0",
    imageUrl: "",
    isActive: true,
  });
  const [errors, setErrors] = useState<FormErrors>({});
  const [submitting, setSubmitting] = useState(false);
  const [submittedId, setSubmittedId] = useState<number | null>(null);
  const previewImage = form.imageUrl.trim();
  const previewBackground = previewImage
    ? { backgroundImage: `url("${previewImage.replace(/"/g, '\\"')}")` }
    : undefined;

  useEffect(() => {
    if (!sellerId) {
      router.replace(
        "/admin/auth/login?next=" + encodeURIComponent("/admin/products/new"),
      );
    }
  }, [router, sellerId]);

  const onChange = (e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    const field = name as Exclude<keyof FormState, "isActive">;
    setForm((prev) => ({ ...prev, [field]: value }));
    if (errors[field]) {
      setErrors((prev) => ({ ...prev, [field]: undefined }));
    }
  };

  const validate = () => {
    const next: FormErrors = {};
    if (!form.name.trim()) next.name = "상품명을 입력해주세요";
    const priceNum = Number(form.price);
    if (!Number.isFinite(priceNum) || priceNum < 0)
      next.price = "가격은 0 이상의 숫자";
    const stockNum = Number(form.stockQuantity);
    if (!Number.isInteger(stockNum) || stockNum < 0)
      next.stockQuantity = "재고는 0 이상의 정수";
    setErrors(next);
    return Object.keys(next).length === 0;
  };

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (!sellerId) return;
    if (!validate()) return;
    try {
      setSubmitting(true);
      setErrors({});
      const payload: Record<string, unknown> = {
        name: form.name.trim(),
        description: form.description.trim(),
        price: Number(form.price),
        stockQuantity: Number(form.stockQuantity),
        isActive: form.isActive,
      };
      if (form.imageUrl.trim()) payload.imageUrl = form.imageUrl.trim();

      const res = await apiFetch(
        `/api/seller/products?sellerId=${encodeURIComponent(sellerId)}`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload),
        },
      );
      const data = await res.json().catch(() => ({}));
      if (!res.ok) {
        setErrors({ base: data?.message || "상품 등록에 실패했습니다." });
        return;
      }
      const pid = (data?.product_id as number) ?? null;
      setSubmittedId(pid);
      // 잠깐 성공 상태 보여준 후 대시보드로 이동
      setTimeout(() => router.replace("/admin/dashboard"), 800);
    } catch {
      setErrors({ base: "네트워크 오류가 발생했습니다." });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form onSubmit={onSubmit} className="space-y-8">
      <div className="grid gap-6 xl:grid-cols-[2fr,1fr]">
        <div className="space-y-6">
          <SectionCard
            title="상품 기본 정보"
            description="스토어에 노출될 기본 내용을 입력하세요."
          >
            <div className="space-y-5">
              <Field label="상품명" required>
                <Input
                  name="name"
                  value={form.name}
                  onChange={onChange}
                  placeholder="예) 프리미엄 텀블러"
                />
                {errors.name && <FieldError>{errors.name}</FieldError>}
              </Field>
              <Field label="상품 설명">
                <TextArea
                  name="description"
                  value={form.description}
                  onChange={onChange}
                  placeholder="상품 특징, 구성, 배송 안내 등을 작성해주세요."
                />
              </Field>
              <Field label="대표 이미지 URL">
                <Input
                  name="imageUrl"
                  value={form.imageUrl}
                  onChange={onChange}
                  placeholder="https://example.com/product-cover.jpg"
                />
              </Field>
            </div>
          </SectionCard>

          <SectionCard
            title="판매 정보"
            description="가격과 재고, 노출 상태를 설정합니다."
          >
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
              <Field label="판매가 (원)" required>
                <Input
                  name="price"
                  value={form.price}
                  onChange={onChange}
                  inputMode="decimal"
                  pattern="[0-9]*"
                />
                {errors.price && <FieldError>{errors.price}</FieldError>}
              </Field>
              <Field label="재고 수량" required>
                <Input
                  name="stockQuantity"
                  value={form.stockQuantity}
                  onChange={onChange}
                  inputMode="numeric"
                  pattern="[0-9]*"
                />
                {errors.stockQuantity && (
                  <FieldError>{errors.stockQuantity}</FieldError>
                )}
              </Field>
            </div>
            <div className="mt-6 flex items-center justify-between rounded-xl border border-gray-200 bg-gray-50 px-4 py-3">
              <div>
                <div className="text-sm font-medium text-gray-800">
                  상품 노출 상태
                </div>
                <p className="text-xs text-gray-500">
                  비활성화하면 스토어에 노출되지 않습니다.
                </p>
              </div>
              <Toggle
                checked={form.isActive}
                onChange={(value) =>
                  setForm((prev) => ({ ...prev, isActive: value }))
                }
              />
            </div>
          </SectionCard>
        </div>

        <div className="space-y-6">
          <SectionCard
            title="미리보기"
            description="입력한 정보가 이렇게 보여집니다."
          >
            <div className="overflow-hidden rounded-2xl border border-dashed border-gray-200 bg-white">
              <div className="h-44 bg-gray-100">
                {previewBackground ? (
                  <div
                    className="h-full w-full bg-cover bg-center"
                    style={previewBackground}
                  />
                ) : (
                  <div className="flex h-full w-full flex-col items-center justify-center text-xs text-gray-400">
                    <span className="text-sm">대표 이미지 미등록</span>
                    <span>이미지 URL을 입력하면 미리보기 됩니다.</span>
                  </div>
                )}
              </div>
              <div className="space-y-3 px-4 py-5">
                <div className="text-lg font-semibold text-gray-900">
                  {form.name || "상품명이 여기에 표시됩니다"}
                </div>
                <div className="text-sm text-gray-500 max-h-24 overflow-hidden whitespace-pre-line break-words">
                  {form.description ||
                    "상품 설명을 입력하면 요약 정보가 노출됩니다."}
                </div>
                <div className="flex items-center justify-between text-sm text-gray-600">
                  <span>가격</span>
                  <span className="text-base font-semibold text-gray-900">
                    {Number(form.price || "0").toLocaleString()}원
                  </span>
                </div>
                <div className="flex items-center justify-between text-sm text-gray-600">
                  <span>재고</span>
                  <span>
                    {Number(form.stockQuantity || "0").toLocaleString()}개
                  </span>
                </div>
                <div className="rounded-full bg-gray-100 px-3 py-1 text-center text-xs font-medium text-gray-600">
                  {form.isActive ? "스토어에 노출" : "임시 비공개"}
                </div>
              </div>
            </div>
          </SectionCard>

          <SectionCard title="등록 가이드">
            <ul className="space-y-2 text-sm text-gray-600">
              <li>
                • 판매가와 재고 정보는 등록 후 언제든지 수정할 수 있습니다.
              </li>
              <li>• 이미지 URL은 CDN 또는 S3 등 공개 주소를 입력해주세요.</li>
              <li>• 노출 상태를 비활성화하면 스토어에서 바로 숨겨집니다.</li>
            </ul>
          </SectionCard>

          {submittedId && (
            <SectionCard
              title="등록 완료"
              description="잠시 후 상품 관리 페이지로 이동합니다."
            >
              <div className="text-sm text-emerald-700">
                상품이 등록되었습니다. 상품 ID: {submittedId}
              </div>
            </SectionCard>
          )}
        </div>
      </div>

      {errors.base && (
        <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-600">
          {errors.base}
        </div>
      )}

      <div className="flex justify-end gap-3">
        <button
          type="button"
          onClick={() => router.back()}
          className="rounded-full border border-gray-300 bg-white px-6 py-3 text-sm font-medium text-gray-600 transition hover:bg-gray-50"
        >
          취소
        </button>
        <button
          type="submit"
          disabled={submitting}
          className="rounded-full bg-[#F25C54] px-6 py-3 text-sm font-semibold text-white transition hover:bg-[#e24b44] disabled:opacity-60"
        >
          {submitting ? "등록 중..." : "상품 등록"}
        </button>
      </div>
    </form>
  );
}

function SectionCard({
  title,
  description,
  children,
}: {
  title: string;
  description?: string;
  children: ReactNode;
}) {
  return (
    <section className="rounded-2xl border border-gray-200 bg-white p-6 shadow-sm">
      <div className="mb-5">
        <h2 className="text-base font-semibold text-gray-900">{title}</h2>
        {description && (
          <p className="mt-1 text-sm text-gray-500">{description}</p>
        )}
      </div>
      {children}
    </section>
  );
}

function Field({
  label,
  required,
  children,
}: {
  label: string;
  required?: boolean;
  children: ReactNode;
}) {
  return (
    <label className="block text-sm text-gray-700">
      <span className="font-medium text-gray-800">
        {label} {required && <span className="text-red-500">*</span>}
      </span>
      <div className="mt-2">{children}</div>
    </label>
  );
}

function Input(props: InputHTMLAttributes<HTMLInputElement>) {
  return (
    <input
      {...props}
      className={`w-full rounded-xl border border-gray-300 px-4 py-3 text-sm text-gray-900 shadow-sm transition focus:border-[#F25C54] focus:outline-none focus:ring-2 focus:ring-offset-1 focus:ring-[#F25C54]/30 ${props.className ?? ""}`}
    />
  );
}

function TextArea(props: TextareaHTMLAttributes<HTMLTextAreaElement>) {
  return (
    <textarea
      {...props}
      className={`min-h-[140px] w-full rounded-xl border border-gray-300 px-4 py-3 text-sm text-gray-900 shadow-sm transition focus:border-[#F25C54] focus:outline-none focus:ring-2 focus:ring-offset-1 focus:ring-[#F25C54]/30 ${props.className ?? ""}`}
    />
  );
}

function FieldError({ children }: { children: ReactNode }) {
  return <p className="mt-2 text-xs text-red-600">{children}</p>;
}

function Toggle({
  checked,
  onChange,
}: {
  checked: boolean;
  onChange: (value: boolean) => void;
}) {
  return (
    <button
      type="button"
      onClick={() => onChange(!checked)}
      className={`relative inline-flex h-7 w-14 items-center rounded-full transition ${checked ? "bg-[#F25C54]" : "bg-gray-300"}`}
      aria-pressed={checked}
    >
      <span
        className={`inline-block h-6 w-6 transform rounded-full bg-white shadow transition ${checked ? "translate-x-7" : "translate-x-1"}`}
      />
      <span className="sr-only">상품 노출 상태 전환</span>
    </button>
  );
}
