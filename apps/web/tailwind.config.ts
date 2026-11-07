import type { Config } from 'tailwindcss';

const config: Config = {
  content: [
    './src/pages/**/*.{js,ts,jsx,tsx,mdx}',
    './src/components/**/*.{js,ts,jsx,tsx,mdx}',
    './src/app/**/*.{js,ts,jsx,tsx,mdx}',
  ],
  theme: {
    extend: {
      colors: {
        filkom: {
          blue: '#1E3A8A',
          sky: '#0284C7',
          orange: '#F97316',
        },
      },
    },
  },
  plugins: [],
};
export default config;
