import { defineConfig } from '@playwright/test'


export default defineConfig({
  testDir: 'tests',
  timeout: 60000,
  use: {
    baseURL: 'http://localhost:5173',
    headless: true,
    viewport: { width: 1280, height: 720 },
    actionTimeout: 10000,
  },
  webServer: {
    command: 'npm run dev',
    url: 'http://localhost:5173/',
    reuseExistingServer: true,
    timeout: 120000
  }
})