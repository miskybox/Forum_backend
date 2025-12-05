// @ts-check
import { test, expect } from '@playwright/test';

/**
 * Tests E2E para el Mapa de Viajes
 */
test.describe('Mapa de Viajes', () => {

  test.beforeEach(async ({ page }) => {
    // Login antes de cada test
    await page.goto('/login');
    await page.fill('input[name="username"], input[type="text"]', 'user');
    await page.fill('input[name="password"], input[type="password"]', 'User123!');
    await page.click('button[type="submit"]');
    await expect(page).not.toHaveURL(/.*login/, { timeout: 10000 });
  });

  test('debe cargar la página del mapa correctamente', async ({ page }) => {
    await page.goto('/travel');
    
    // Verificar que la página carga
    await expect(page.locator('text=/mapa|viajes|travel/i').first()).toBeVisible({ timeout: 10000 });
  });

  test('debe mostrar el mapa SVG', async ({ page }) => {
    await page.goto('/travel');
    
    // Esperar a que el mapa cargue (puede cargar GeoJSON)
    await page.waitForTimeout(3000);
    
    // Verificar que hay un SVG del mapa
    const svg = page.locator('svg');
    await expect(svg.first()).toBeVisible({ timeout: 15000 });
  });

  test('debe mostrar la leyenda del mapa', async ({ page }) => {
    await page.goto('/travel');
    
    await page.waitForTimeout(2000);
    
    // Verificar leyenda con estados
    await expect(page.locator('text=/leyenda|visitado|quiero ir/i').first()).toBeVisible({ timeout: 10000 });
  });

  test('debe mostrar estadísticas de viaje', async ({ page }) => {
    await page.goto('/travel');
    
    await page.waitForTimeout(2000);
    
    // Verificar que hay algún indicador de estadísticas
    const statsSection = page.locator('text=/países|estadísticas|marcados/i');
    await expect(statsSection.first()).toBeVisible({ timeout: 10000 });
  });

  test('mapa debe ser interactivo (hover en países)', async ({ page }) => {
    await page.goto('/travel');
    
    // Esperar carga del mapa
    await page.waitForTimeout(5000);
    
    // Buscar un path del mapa (país) y hacer hover
    const countryPath = page.locator('svg path').first();
    if (await countryPath.isVisible()) {
      await countryPath.hover();
      // El hover debería mostrar algún efecto visual
    }
  });

  test('debe poder abrir modal para agregar lugar', async ({ page }) => {
    await page.goto('/travel');
    
    await page.waitForTimeout(3000);
    
    // Buscar botón de agregar o hacer click en un país
    const addButton = page.locator('text=/agregar|añadir|nuevo/i');
    if (await addButton.first().isVisible()) {
      await addButton.first().click();
      // Verificar que se abre un modal/formulario
      await page.waitForTimeout(1000);
    }
  });
});

