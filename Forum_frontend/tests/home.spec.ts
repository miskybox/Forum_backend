import { test, expect } from '@playwright/test'

test.describe('Home CTAs', () => {
  test('CTA Unirse ahora navega a registro y es focus-visible', async ({ page }) => {
    await page.goto('/')
    await page.waitForLoadState('networkidle')
    // Buscar el link específico del hero
    const cta = page.locator('a[href="/register"]').filter({ hasText: 'Unirse ahora' }).first()
    await expect(cta).toBeVisible({ timeout: 10000 })
    await cta.focus()
    const boxShadow = await cta.evaluate(el => getComputedStyle(el).boxShadow)
    expect(boxShadow).toContain('rgb(255, 255, 255)')
  })

  test('Explorar destinos navega a /categories', async ({ page }) => {
    await page.goto('/')
    await page.getByRole('link', { name: 'Explorar destinos' }).click()
    await expect(page).toHaveURL(/\/categories$/)
  })
})