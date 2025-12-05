// @ts-check
import { defineConfig, devices } from '@playwright/test';

/**
 * Configuración de Playwright para Forum Viajeros
 * @see https://playwright.dev/docs/test-configuration
 */
export default defineConfig({
  testDir: './tests/e2e',
  
  /* Tiempo máximo por test */
  timeout: 30 * 1000,
  
  /* Tiempo máximo para expect() */
  expect: {
    timeout: 5000
  },
  
  /* Ejecutar tests en paralelo */
  fullyParallel: true,
  
  /* Fallar el build si hay test.only en CI */
  forbidOnly: !!process.env.CI,
  
  /* Reintentos en CI */
  retries: process.env.CI ? 2 : 0,
  
  /* Workers paralelos */
  workers: process.env.CI ? 1 : undefined,
  
  /* Reporter de resultados */
  reporter: [
    ['html', { open: 'never' }],
    ['list']
  ],
  
  /* Configuración compartida para todos los tests */
  use: {
    /* URL base de la aplicación */
    baseURL: 'http://localhost:5173',
    
    /* Capturar trace en caso de fallo */
    trace: 'on-first-retry',
    
    /* Screenshots en caso de fallo */
    screenshot: 'only-on-failure',
    
    /* Video en caso de fallo */
    video: 'on-first-retry',
  },

  /* Configurar proyectos para diferentes navegadores */
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
    // Descomentar para probar en más navegadores
    // {
    //   name: 'firefox',
    //   use: { ...devices['Desktop Firefox'] },
    // },
    // {
    //   name: 'webkit',
    //   use: { ...devices['Desktop Safari'] },
    // },
  ],

  /* Servidor de desarrollo */
  webServer: {
    command: 'npm run dev',
    url: 'http://localhost:5173',
    reuseExistingServer: !process.env.CI,
    timeout: 120 * 1000,
  },
});

