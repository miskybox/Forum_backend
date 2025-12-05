import { test, expect } from '@playwright/test'

test.describe('Auth navegación', () => {
  test('CTA Unirse ahora navega a /register', async ({ page }) => {
    await page.goto('/')
    await page.waitForLoadState('networkidle')
    // Buscar el link específico del hero
    const cta = page.locator('a[href="/register"]').filter({ hasText: 'Unirse ahora' }).first()
    await expect(cta).toBeVisible({ timeout: 10000 })
    await cta.click()
    await expect(page).toHaveURL(/\/register$/)
  })

  test('CTA Iniciar sesión navega a /login', async ({ page }) => {
    await page.goto('/')
    // Use the navbar link specifically by targeting within navigation
    await page.getByRole('navigation').getByRole('link', { name: 'Iniciar Sesión' }).click()
    await expect(page).toHaveURL(/\/login$/)
  })
})