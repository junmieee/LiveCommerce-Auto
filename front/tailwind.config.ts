import type { Config } from "tailwindcss";

export default {
  content: [
    "./src/pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/components/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/app/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      colors: {
        // 메인 계열
        primary: {
          DEFAULT: "#2563EB",
          light: "#3B82F6",
          dark: "#1E40AF",
        },

        // 보조 색상
        secondary: {
          DEFAULT: "#7D9DD2",
          light: "#A4BCE5",
          dark: "#5A7BAD",
        },

        // 상태별 색상
        success: "#10B981",
        warning: "#F59E0B",
        error: "#EF4444",
        info: "#3B82F6",

        // 중립/기본
        neutral: {
          50: "#FAFAFA",
          100: "#F5F5F5",
          200: "#E5E5E5",
          300: "#D4D4D4",
          400: "#A3A3A3",
          500: "#737373",
          600: "#525252",
          700: "#404040",
          800: "#262626",
          900: "#171717",
        },
        // layout specific
        sidebarAdmin: "#111827", // gray-900
        // Warm, soft sand tone with a bit more contrast
        sidebarCustomer: "#E8E3DB",
      },
      keyframes: {
        wiggleLeft: {
          "0% 100%": { transform: "translateX(0)" },
          "50%": { transform: "translateX(-6px)" },
        },
        wiggleRight: {
          "0% 100%": { transform: "translateX(0)" },
          "50%": { transform: "translateX(6px)" },
        },
      },
      animation: {
        wiggleLeft: "wiggleLeft 0.5s ease-in-out infinite",
        wiggleRight: "wiggleRight 0.5s ease-in-out infinite",
      },
    },
  },
  plugins: [],
} satisfies Config;
