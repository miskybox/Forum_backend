// @ts-check
import { test, expect } from '@playwright/test';

/**
 * Tests E2E para Autenticación
 */
test.describe('Autenticación', () => {
  
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  test('debe mostrar la página de inicio', async ({ page }) => {
    await expect(page).toHaveTitle(/Forum|Viajeros/i);
  });

  test('debe navegar a la página de login', async ({ page }) => {
    await page.click('text=Iniciar sesión');
    await expect(page).toHaveURL(/.*login/);
    await expect(page.locator('form')).toBeVisible();
  });

  test('debe navegar a la página de registro', async ({ page }) => {
    await page.click('text=Registrarse');
    await expect(page).toHaveURL(/.*register/);
  });

  test('debe mostrar error con credenciales inválidas', async ({ page }) => {
    await page.goto('/login');
    
    await page.fill('input[name="username"], input[type="text"]', 'usuarioInvalido');
    await page.fill('input[name="password"], input[type="password"]', 'passwordIncorrecto');
    await page.click('button[type="submit"]');
    
    // Esperar mensaje de error
    await expect(page.locator('text=/error|incorrecto|inválido/i')).toBeVisible({ timeout: 10000 });
  });

  test('debe hacer login correctamente con usuario válido', async ({ page }) => {
    await page.goto('/login');
    
    // Usar credenciales de prueba
    await page.fill('input[name="username"], input[type="text"]', 'user');
    await page.fill('input[name="password"], input[type="password"]', 'User123!');
    await page.click('button[type="submit"]');
    
    // Esperar redirección o cambio en UI
    await expect(page).not.toHaveURL(/.*login/, { timeout: 10000 });
  });

  test('debe hacer logout correctamente', async ({ page }) => {
    // Primero hacer login
    await page.goto('/login');
    await page.fill('input[name="username"], input[type="text"]', 'user');
    await page.fill('input[name="password"], input[type="password"]', 'User123!');
    await page.click('button[type="submit"]');
    
    // Esperar a estar logueado
    await expect(page).not.toHaveURL(/.*login/, { timeout: 10000 });
    
    // Buscar y hacer click en logout
    const logoutButton = page.locator('text=/cerrar sesión|logout|salir/i');
    if (await logoutButton.isVisible()) {
      await logoutButton.click();
      // Verificar que se redirige o muestra opción de login
      await expect(page.locator('text=/iniciar sesión|login/i')).toBeVisible({ timeout: 5000 });
    }
  });
});

