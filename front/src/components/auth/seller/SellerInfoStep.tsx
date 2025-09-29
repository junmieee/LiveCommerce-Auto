"use client";

import { useState } from "react";
import { SellerInfo } from "@/app/(plain)/admin/auth/register/page";

interface SellerInfoStepProps {
  initialData: SellerInfo;
  onSubmit: (data: SellerInfo) => void;
  onSkip: () => void;
  onBack: () => void;
}

export default function SellerInfoStep({
  initialData,
  onSubmit,
  onSkip,
  onBack,
}: SellerInfoStepProps) {
  const [formData, setFormData] = useState<SellerInfo>(initialData);
  const [errors, setErrors] = useState<Partial<SellerInfo>>({});

  const handleInputChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));

    // 에러 클리어
    if (errors[name as keyof SellerInfo]) {
      setErrors((prev) => ({
        ...prev,
        [name]: "",
      }));
    }
  };

  const handleFileChange = (
    e: React.ChangeEvent<HTMLInputElement>,
    fieldName: "profileImage" | "bannerImage",
  ) => {
    const file = e.target.files?.[0] || null;
    setFormData((prev) => ({
      ...prev,
      [fieldName]: file,
    }));
  };

  const validateForm = (): boolean => {
    const newErrors: Partial<SellerInfo> = {};

    if (!formData.businessName) {
      newErrors.businessName = "상호명을 입력해주세요";
    }

    if (!formData.businessNumber) {
      newErrors.businessNumber = "사업자등록번호를 입력해주세요";
    } else if (!/^\d{3}-\d{2}-\d{5}$/.test(formData.businessNumber)) {
      newErrors.businessNumber =
        "올바른 사업자등록번호 형식이 아닙니다 (예: 123-45-67890)";
    }

    if (!formData.representativeName) {
      newErrors.representativeName = "대표자명을 입력해주세요";
    }

    if (!formData.customerServiceHours) {
      newErrors.customerServiceHours = "고객센터 운영시간을 입력해주세요";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (validateForm()) {
      onSubmit(formData);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      <h3 className="text-lg font-medium text-gray-900 mb-4">판매자 정보</h3>

      {/* 상호명 */}
      <div>
        <label
          htmlFor="businessName"
          className="block text-sm font-medium text-gray-700"
        >
          상호명 *
        </label>
        <input
          type="text"
          id="businessName"
          name="businessName"
          value={formData.businessName}
          onChange={handleInputChange}
          className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
          placeholder="(주)홍길동상사"
        />
        {errors.businessName && (
          <p className="mt-1 text-sm text-red-600">{errors.businessName}</p>
        )}
      </div>

      {/* 사업자등록번호 */}
      <div>
        <label
          htmlFor="businessNumber"
          className="block text-sm font-medium text-gray-700"
        >
          사업자등록번호 *
        </label>
        <input
          type="text"
          id="businessNumber"
          name="businessNumber"
          value={formData.businessNumber}
          onChange={handleInputChange}
          className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
          placeholder="123-45-67890"
        />
        {errors.businessNumber && (
          <p className="mt-1 text-sm text-red-600">{errors.businessNumber}</p>
        )}
      </div>

      {/* 대표자명 */}
      <div>
        <label
          htmlFor="representativeName"
          className="block text-sm font-medium text-gray-700"
        >
          대표자명 *
        </label>
        <input
          type="text"
          id="representativeName"
          name="representativeName"
          value={formData.representativeName}
          onChange={handleInputChange}
          className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
          placeholder="홍길동"
        />
        {errors.representativeName && (
          <p className="mt-1 text-sm text-red-600">
            {errors.representativeName}
          </p>
        )}
      </div>

      {/* 프로필 이미지 */}
      <div>
        <label
          htmlFor="sellerProfileImage"
          className="block text-sm font-medium text-gray-700"
        >
          판매자 프로필 이미지
        </label>
        <input
          type="file"
          id="sellerProfileImage"
          name="sellerProfileImage"
          accept="image/*"
          onChange={(e) => handleFileChange(e, "profileImage")}
          className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
        />
        <p className="mt-1 text-sm text-gray-500">선택사항입니다</p>
      </div>

      {/* 고객센터 운영시간 */}
      <div>
        <label
          htmlFor="customerServiceHours"
          className="block text-sm font-medium text-gray-700"
        >
          고객센터 운영시간 *
        </label>
        <input
          type="text"
          id="customerServiceHours"
          name="customerServiceHours"
          value={formData.customerServiceHours}
          onChange={handleInputChange}
          className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
          placeholder="평일 09:00 ~ 18:00"
        />
        {errors.customerServiceHours && (
          <p className="mt-1 text-sm text-red-600">
            {errors.customerServiceHours}
          </p>
        )}
      </div>

      {/* 판매자몰 배너 이미지 */}
      <div>
        <label
          htmlFor="bannerImage"
          className="block text-sm font-medium text-gray-700"
        >
          판매자몰 배너 이미지
        </label>
        <input
          type="file"
          id="bannerImage"
          name="bannerImage"
          accept="image/*"
          onChange={(e) => handleFileChange(e, "bannerImage")}
          className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
        />
        <p className="mt-1 text-sm text-gray-500">선택사항입니다</p>
      </div>

      {/* 판매자 소개 */}
      <div>
        <label
          htmlFor="description"
          className="block text-sm font-medium text-gray-700"
        >
          판매자 소개
        </label>
        <textarea
          id="description"
          name="description"
          rows={4}
          value={formData.description}
          onChange={handleInputChange}
          className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
          placeholder="판매자 및 상품에 대한 간단한 소개를 입력해주세요"
        />
        <p className="mt-1 text-sm text-gray-500">선택사항입니다</p>
      </div>

      {/* 버튼들 */}
      <div className="flex space-x-4">
        <button
          type="button"
          onClick={onBack}
          className="flex-1 py-2 px-4 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
        >
          이전
        </button>
        <button
          type="button"
          onClick={onSkip}
          className="flex-1 py-2 px-4 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
        >
          건너뛰기
        </button>
        <button
          type="submit"
          className="flex-1 py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
        >
          완료
        </button>
      </div>
    </form>
  );
}
