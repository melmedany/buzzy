import { defineConfig } from "vite";
import alias from "@rollup/plugin-alias";
import vue from "@vitejs/plugin-vue";
import { resolve } from "path";
// import dns from 'dns';

// dns.setDefaultResultOrder('verbatim')

const rootDir = resolve(__dirname);

// https://vitejs.dev/config/
export default defineConfig({
  server: {
    host: '127.0.0.1',
    port: 3000
  },
  plugins: [vue(), alias()],
  resolve: {
    alias: {
      "@src": resolve(rootDir, "src"),
      "@custom_types": resolve(rootDir, "src/@custom_types"),
    },
  },
});
