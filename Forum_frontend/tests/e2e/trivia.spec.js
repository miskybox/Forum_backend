// @ts-check
import { test, expect } from '@playwright/test';

/**
 * Tests E2E para la Trivia Geográfica
 */
test.describe('Trivia Geográfica', () => {

  test.beforeEach(async ({ page }) => {
    // Login antes de cada test
    await page.goto('/login');
    await page.fill('input[name="username"], input[type="text"]', 'user');
    await page.fill('input[name="password"], input[type="password"]', 'User123!');
    await page.click('button[type="submit"]');
    await expect(page).not.toHaveURL(/.*login/, { timeout: 10000 });
  });

  test('debe cargar la página de trivia correctamente', async ({ page }) => {
    await page.goto('/trivia');
    
    await expect(page.locator('text=/trivia geográfica/i')).toBeVisible({ timeout: 10000 });
  });

  test('debe mostrar los modos de juego', async ({ page }) => {
    await page.goto('/trivia');
    
    // Verificar que hay opciones de juego
    await expect(page.locator('text=/quiz rápido|desafío|modo infinito/i').first()).toBeVisible();
  });

  test('debe mostrar el modo infinito', async ({ page }) => {
    await page.goto('/trivia');
    
    await expect(page.locator('text=/infinito/i')).toBeVisible();
  });

  test('debe poder iniciar partida rápida', async ({ page }) => {
    await page.goto('/trivia');
    
    // Click en Quiz Rápido
    const quickButton = page.locator('text=/quiz rápido/i');
    await quickButton.click();
    
    // Esperar a que cargue la partida
    await page.waitForTimeout(3000);
    
    // Verificar que estamos en modo juego (hay pregunta o cargando)
    const gameContent = page.locator('text=/pregunta|cargando|puntuación/i');
    await expect(gameContent.first()).toBeVisible({ timeout: 15000 });
  });

  test('debe poder iniciar modo infinito', async ({ page }) => {
    await page.goto('/trivia');
    
    // Click en Modo Infinito
    const infiniteButton = page.locator('text=/modo infinito/i');
    await infiniteButton.click();
    
    // Verificar que navega a la página de trivia infinita
    await expect(page).toHaveURL(/.*trivia\/infinite/, { timeout: 10000 });
  });

  test('modo infinito debe cargar preguntas de API', async ({ page }) => {
    await page.goto('/trivia/infinite');
    
    // Esperar carga de preguntas desde API
    await page.waitForTimeout(5000);
    
    // Verificar que hay una pregunta visible
    const question = page.locator('text=/capital|bandera|moneda|continente|idioma/i');
    await expect(question.first()).toBeVisible({ timeout: 20000 });
  });

  test('debe poder responder una pregunta', async ({ page }) => {
    await page.goto('/trivia/infinite');
    
    // Esperar carga
    await page.waitForTimeout(5000);
    
    // Buscar opciones de respuesta (botones)
    const options = page.locator('button').filter({ hasText: /^[A-Z]/ });
    
    if (await options.first().isVisible()) {
      // Click en la primera opción
      await options.first().click();
      
      // Verificar feedback (correcto/incorrecto o siguiente)
      await page.waitForTimeout(1000);
      const feedback = page.locator('text=/siguiente|correcto|incorrecto|puntos/i');
      await expect(feedback.first()).toBeVisible({ timeout: 5000 });
    }
  });

  test('debe mostrar el leaderboard', async ({ page }) => {
    await page.goto('/trivia/leaderboard');
    
    // Verificar que carga la página de ranking
    await expect(page.locator('text=/ranking|leaderboard|puntuación/i').first()).toBeVisible({ timeout: 10000 });
  });

  test('debe poder navegar entre secciones de trivia', async ({ page }) => {
    await page.goto('/trivia');
    
    // Ir al leaderboard
    await page.click('text=/ranking|leaderboard/i');
    await expect(page).toHaveURL(/.*leaderboard/);
    
    // Volver a trivia home
    await page.goto('/trivia');
    await expect(page.locator('text=/trivia geográfica/i')).toBeVisible();
  });
});

