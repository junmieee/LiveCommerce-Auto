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
        background: "var(--background)",
        foreground: "var(--foreground)",
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
