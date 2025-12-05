import { test, expect } from '@playwright/test'

test.describe('Navbar navegación', () => {
  test('Inicio navega a /', async ({ page }) => {
    await page.goto('/')
    await page.waitForLoadState('networkidle')
    // Usar el link del navbar específicamente
    const inicioLink = page.getByRole('navigation').getByRole('link', { name: 'Inicio' })
    await expect(inicioLink).toBeVisible({ timeout: 10000 })
    await inicioLink.click()
    await expect(page).toHaveURL(/\/$/)
  })

  test('Continentes navega a /categories', async ({ page }) => {
    await page.goto('/')
    // Use the navbar link specifically by targeting within navigation
    await page.getByRole('navigation').getByRole('link', { name: 'Continentes' }).click()
    await expect(page).toHaveURL(/\/categories$/)
  })

  test('Foros navega a /forums', async ({ page }) => {
    await page.goto('/')
    // Use the navbar link specifically by targeting within navigation
    await page.getByRole('navigation').getByRole('link', { name: 'Foros' }).click()
    await expect(page).toHaveURL(/\/forums$/)
  })
})