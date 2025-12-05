// @ts-check
import { test, expect } from '@playwright/test';

/**
 * Tests E2E para Navegación General
 */
test.describe('Navegación', () => {

  test('debe cargar la página principal correctamente', async ({ page }) => {
    await page.goto('/');
    
    // Verificar que el navbar está presente
    await expect(page.locator('nav')).toBeVisible();
    
    // Verificar elementos principales
    await expect(page.locator('text=/inicio|home/i').first()).toBeVisible();
  });

  test('debe navegar a Categorías/Continentes', async ({ page }) => {
    await page.goto('/');
    
    await page.click('text=/continentes|categorías/i');
    await expect(page).toHaveURL(/.*categories/);
  });

  test('debe navegar a Foros', async ({ page }) => {
    await page.goto('/');
    
    await page.click('text=Foros');
    await expect(page).toHaveURL(/.*forums/);
  });

  test('debe navegar a Trivia', async ({ page }) => {
    await page.goto('/');
    
    // Hacer login primero para ver el enlace de Trivia
    await page.goto('/login');
    await page.fill('input[name="username"], input[type="text"]', 'user');
    await page.fill('input[name="password"], input[type="password"]', 'User123!');
    await page.click('button[type="submit"]');
    await expect(page).not.toHaveURL(/.*login/, { timeout: 10000 });
    
    // Navegar a Trivia
    await page.click('text=Trivia');
    await expect(page).toHaveURL(/.*trivia/);
    await expect(page.locator('text=/trivia geográfica/i')).toBeVisible();
  });

  test('debe navegar a Mi Mapa', async ({ page }) => {
    // Hacer login primero
    await page.goto('/login');
    await page.fill('input[name="username"], input[type="text"]', 'user');
    await page.fill('input[name="password"], input[type="password"]', 'User123!');
    await page.click('button[type="submit"]');
    await expect(page).not.toHaveURL(/.*login/, { timeout: 10000 });
    
    // Navegar a Mi Mapa
    await page.click('text=/mi mapa|mapa/i');
    await expect(page).toHaveURL(/.*travel/);
  });

  test('debe mostrar página 404 para rutas inexistentes', async ({ page }) => {
    await page.goto('/ruta-que-no-existe-12345');
    
    await expect(page.locator('text=/404|no encontrada|not found/i')).toBeVisible();
  });

  test('debe tener footer visible', async ({ page }) => {
    await page.goto('/');
    
    await expect(page.locator('footer')).toBeVisible();
  });

  test('navbar debe ser responsive', async ({ page }) => {
    await page.goto('/');
    
    // Desktop
    await page.setViewportSize({ width: 1280, height: 720 });
    await expect(page.locator('nav')).toBeVisible();
    
    // Mobile
    await page.setViewportSize({ width: 375, height: 667 });
    await expect(page.locator('nav')).toBeVisible();
  });
});

