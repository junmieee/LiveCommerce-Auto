"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";

type Step = 1 | 2 | 3;

type Step1State = {
  companyName: string; // 상호명*
  businessNumber: string;
  representativeName: string; // 대표자명*
  email: string; // 로그인/연락 이메일*
  password: string;
  passwordConfirm: string;
  phone: string; // 연락처
};

type Step2State = {
  storeName: string; // 판매자 스토어명
  profileImage?: File | null;
  bannerImage?: File | null;
  csStart?: string; // 고객센터 문의시간 시작
  csEnd?: string; // 고객센터 문의시간 종료
};

type Step3State = {
  instagramUrl?: string;
  youtubeUrl?: string;
  blogUrl?: string;
  kakaoChannelUrl?: string;
  shippingAddress?: string;
  returnAddress?: string; // 교환반품 주소*
  courierPickupTime?: string; // 택배 가능 시간
  returnPolicy?: string; // 반품 정책 상세
};

export default function SellerRegisterWizard() {
  const router = useRouter();
  const [step, setStep] = useState<Step>(1);
  const [loading, setLoading] = useState(false);
  const [serverError, setServerError] = useState<string | null>(null);

  const [s1, setS1] = useState<Step1State>({
    companyName: "",
    businessNumber: "",
    representativeName: "",
    email: "",
    password: "",
    passwordConfirm: "",
    phone: "",
  });
  const [s2, setS2] = useState<Step2State>({
    storeName: "",
    profileImage: null,
    bannerImage: null,
  });
  const [s3, setS3] = useState<Step3State>({});

  const validateStep1 = () => {
    const emailRe = /\S+@\S+\.\S+/;
    if (!s1.companyName.trim()) return "상호명을 입력해주세요";
    if (!s1.representativeName.trim()) return "대표자명을 입력해주세요";
    if (!s1.email.trim() || !emailRe.test(s1.email))
      return "올바른 이메일을 입력해주세요";
    if (!s1.password || s1.password.length < 8)
      return "비밀번호는 8자 이상이어야 합니다";
    if (s1.password !== s1.passwordConfirm)
      return "비밀번호가 일치하지 않습니다";
    return null;
  };

  const onSubmit = async () => {
    const err = validateStep1();
    if (err) {
      setServerError(err);
      setStep(1);
      return;
    }
    setServerError(null);
    try {
      setLoading(true);
      // 1차 가입은 사용자/판매자 최소 정보만 백엔드에 제출
      const res = await fetch("/api/users/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          email: s1.email,
          name: s1.representativeName, // backend의 name = 대표자명
          password: s1.password,
          provider: "local",
          isSeller: true,
          companyName: s1.companyName,
          businessNumber: s1.businessNumber,
          contactEmail: s1.email,
          // 추가 정보(s2/s3)는 추후 별도 API로 연동 예정
        }),
      });

      const data = await res.json().catch(() => ({}));
      if (!(res.status === 201 || res.status === 200)) {
        setServerError(data?.message || "회원가입에 실패했습니다.");
        return;
      }

      // TODO: s2/s3 전송용 API 확정 시 추가 호출 (배너/프로필 업로드 등)
      router.replace("/admin/auth/login");
    } catch {
      setServerError("네트워크 오류가 발생했습니다.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <Header step={step} />
      {step === 1 && (
        <Step1
          value={s1}
          onChange={setS1}
          onNext={() => {
            const e = validateStep1();
            if (e) {
              setServerError(e);
              return;
            }
            setServerError(null);
            setStep(2);
          }}
        />
      )}
      {step === 2 && (
        <Step2
          value={s2}
          onChange={setS2}
          onPrev={() => setStep(1)}
          onNext={() => setStep(3)}
        />
      )}
      {step === 3 && (
        <Step3
          value={s3}
          onChange={setS3}
          onPrev={() => setStep(2)}
          onSubmit={onSubmit}
          submitting={loading}
        />
      )}
      {serverError && (
        <p className="mt-4 text-sm text-red-600">{serverError}</p>
      )}
    </div>
  );
}

function Header({ step }: { step: Step }) {
  return (
    <div className="sm:mx-auto sm:w-full sm:max-w-md">
      <h2 className="mt-2 text-center text-3xl font-extrabold text-gray-900">
        판매자 회원가입
      </h2>
      <div className="mt-4 flex justify-center">
        <div className="flex items-center">
          <Circle n={1} active={step >= 1} />
          <Bar active={step >= 2} />
          <Circle n={2} active={step >= 2} />
          <Bar active={step >= 3} />
          <Circle n={3} active={step >= 3} />
        </div>
      </div>
    </div>
  );
}

function Circle({ n, active }: { n: number; active: boolean }) {
  return (
    <div
      className={`w-10 h-10 text-lg rounded-full flex items-center justify-center ${active ? "bg-blue-600 text-white" : "bg-gray-300 text-gray-600"}`}
    >
      {n}
    </div>
  );
}
function Bar({ active }: { active: boolean }) {
  return (
    <div className={`w-16 h-2 ${active ? "bg-blue-600" : "bg-gray-300"}`} />
  );
}

function Label({
  children,
  required,
}: {
  children: React.ReactNode;
  required?: boolean;
}) {
  return (
    <label className="block text-sm font-medium text-gray-700">
      {children} {required && <span className="text-red-500">*</span>}
    </label>
  );
}

function Input(props: React.InputHTMLAttributes<HTMLInputElement>) {
  return (
    <input
      {...props}
      className={`mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 ${props.className ?? ""}`}
    />
  );
}

function Card({
  title,
  children,
}: {
  title: string;
  children: React.ReactNode;
}) {
  return (
    <div className="bg-gray-50 border border-gray-200 rounded-lg p-4">
      <div className="text-gray-800 font-medium mb-3">{title}</div>
      {children}
    </div>
  );
}

function Step1({
  value,
  onChange,
  onNext,
}: {
  value: Step1State;
  onChange: (v: Step1State) => void;
  onNext: () => void;
}) {
  const bind = (key: keyof Step1State) => ({
    value: value[key] as string,
    onChange: (e: React.ChangeEvent<HTMLInputElement>) =>
      onChange({ ...value, [key]: e.target.value }),
  });
  return (
    <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
      <div className="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10 space-y-6">
        <Card title="판매자 정보">
          <div className="space-y-4">
            <div>
              <Label required>상호명</Label>
              <Input placeholder="등등이 하우스" {...bind("companyName")} />
            </div>
            <div>
              <Label>사업자 등록번호</Label>
              <Input placeholder="104308903345" {...bind("businessNumber")} />
            </div>
            <div>
              <Label required>대표자명</Label>
              <Input placeholder="홍길동" {...bind("representativeName")} />
            </div>
            <div>
              <Label required>이메일</Label>
              <Input
                type="email"
                placeholder="dongdong@gmail.com"
                {...bind("email")}
              />
            </div>
            <div>
              <Label>연락처</Label>
              <Input placeholder="010-1111-2222" {...bind("phone")} />
            </div>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <Label required>비밀번호</Label>
                <Input
                  type="password"
                  placeholder="8자 이상"
                  {...bind("password")}
                />
              </div>
              <div>
                <Label required>비밀번호 재확인</Label>
                <Input
                  type="password"
                  placeholder="비밀번호 다시 입력"
                  {...bind("passwordConfirm")}
                />
              </div>
            </div>
          </div>
        </Card>

        <div className="flex justify-end">
          <button
            onClick={onNext}
            className="px-4 py-2 bg-blue-600 text-white rounded-md"
          >
            다음
          </button>
        </div>
      </div>
    </div>
  );
}

function Step2({
  value,
  onChange,
  onPrev,
  onNext,
}: {
  value: Step2State;
  onChange: (v: Step2State) => void;
  onPrev: () => void;
  onNext: () => void;
}) {
  const bind = (key: keyof Step2State) => ({
    value: (value[key] as string) ?? "",
    onChange: (e: React.ChangeEvent<HTMLInputElement>) =>
      onChange({ ...value, [key]: e.target.value }),
  });
  return (
    <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
      <div className="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10 space-y-6">
        <Card title="판매몰 기본/배너 정보">
          <div className="space-y-4">
            <div>
              <Label>스토어명</Label>
              <Input placeholder="등등이 하우스" {...bind("storeName")} />
            </div>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <Label>프로필 이미지</Label>
                <input
                  type="file"
                  accept="image/*"
                  onChange={(e) =>
                    onChange({ ...value, profileImage: e.target.files?.[0] })
                  }
                />
              </div>
              <div>
                <Label>배너 이미지</Label>
                <input
                  type="file"
                  accept="image/*"
                  onChange={(e) =>
                    onChange({ ...value, bannerImage: e.target.files?.[0] })
                  }
                />
              </div>
            </div>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <Label>고객센터 문의시간(시작)</Label>
                <Input type="time" {...bind("csStart")} />
              </div>
              <div>
                <Label>고객센터 문의시간(종료)</Label>
                <Input type="time" {...bind("csEnd")} />
              </div>
            </div>
          </div>
        </Card>
        <div className="flex justify-between">
          <button
            onClick={onPrev}
            className="px-4 py-2 bg-gray-100 text-gray-800 rounded-md"
          >
            이전
          </button>
          <button
            onClick={onNext}
            className="px-4 py-2 bg-blue-600 text-white rounded-md"
          >
            다음
          </button>
        </div>
      </div>
    </div>
  );
}

function Step3({
  value,
  onChange,
  onPrev,
  onSubmit,
  submitting,
}: {
  value: Step3State;
  onChange: (v: Step3State) => void;
  onPrev: () => void;
  onSubmit: () => void;
  submitting?: boolean;
}) {
  const bind = (key: keyof Step3State) => ({
    value: (value[key] as string) ?? "",
    onChange: (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) =>
      onChange({ ...value, [key]: e.target.value }),
  });
  return (
    <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
      <div className="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10 space-y-6">
        <Card title="SNS 및 온라인 채널">
          <div className="space-y-4">
            <div>
              <Label>인스타그램 링크</Label>
              <Input
                placeholder="https://instagram.com/..."
                {...bind("instagramUrl")}
              />
            </div>
            <div>
              <Label>
                유튜브 링크 <span className="text-red-500">*</span>
              </Label>
              <Input
                placeholder="https://youtube.com/@..."
                {...bind("youtubeUrl")}
              />
            </div>
            <div>
              <Label>블로그 링크</Label>
              <Input
                placeholder="https://blog.example.com/..."
                {...bind("blogUrl")}
              />
            </div>
            <div>
              <Label>카카오 채널 링크</Label>
              <Input
                placeholder="https://pf.kakao.com/..."
                {...bind("kakaoChannelUrl")}
              />
            </div>
          </div>
        </Card>

        <Card title="배송 및 반품 정보">
          <div className="space-y-4">
            <div>
              <Label>배송출발 주소</Label>
              <Input placeholder="서울시 ..." {...bind("shippingAddress")} />
            </div>
            <div>
              <Label>
                교환반품 주소 <span className="text-red-500">*</span>
              </Label>
              <Input placeholder="경기도 ..." {...bind("returnAddress")} />
            </div>
            <div>
              <Label>택배 가능 시간</Label>
              <Input
                placeholder="평일 9시-20시"
                {...bind("courierPickupTime")}
              />
            </div>
            <div>
              <Label>반품 정책 상세 설명</Label>
              <textarea
                {...bind("returnPolicy")}
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 min-h-[96px]"
              />
            </div>
          </div>
        </Card>

        <div className="flex justify-between">
          <button
            onClick={onPrev}
            className="px-4 py-2 bg-gray-100 text-gray-800 rounded-md"
          >
            이전
          </button>
          <button
            onClick={onSubmit}
            disabled={submitting}
            className="px-4 py-2 bg-blue-600 text-white rounded-md disabled:opacity-60"
          >
            {submitting ? "가입 중..." : "가입 완료"}
          </button>
        </div>
      </div>
    </div>
  );
}
