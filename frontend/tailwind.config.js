/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        brand: {
          50: '#f5f2ff',
          100: '#ebe5ff',
          200: '#d6caff',
          300: '#b8a3ff',
          400: '#9672fb',
          500: '#7a4cf0',
          600: '#6430dc',
          700: '#5224b8',
          800: '#442095',
          900: '#2e1566',
        },
      },
      fontFamily: {
        sans: ['Inter', 'ui-sans-serif', 'system-ui', 'Segoe UI', 'Roboto', 'Helvetica Neue', 'Arial', 'sans-serif'],
      },
    },
  },
  plugins: [],
};
