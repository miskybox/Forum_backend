import { test, expect } from '@playwright/test'

test.describe('Navegación Completa - Todos los Links y Botones', () => {
  
  test.beforeEach(async ({ page }) => {
    await page.goto('/')
  })

  // ============ NAVBAR - Desktop ============
  test.describe('Navbar Desktop', () => {
    test('Logo navega a /', async ({ page }) => {
      // El logo es un link que contiene la imagen, buscar por el alt text o href
      const logoLink = page.locator('a[href="/"]').first()
      await logoLink.click()
      await expect(page).toHaveURL(/\/$/)
    })

    test('Link Inicio navega a /', async ({ page }) => {
      await page.getByRole('navigation').getByRole('link', { name: 'Inicio' }).click()
      await expect(page).toHaveURL(/\/$/)
    })

    test('Link Continentes navega a /categories', async ({ page }) => {
      await page.getByRole('navigation').getByRole('link', { name: 'Continentes' }).click()
      await expect(page).toHaveURL(/\/categories$/)
    })

    test('Link Foros navega a /forums', async ({ page }) => {
      await page.getByRole('navigation').getByRole('link', { name: 'Foros' }).click()
      await expect(page).toHaveURL(/\/forums$/)
    })

    test('Link Blog navega a /blog', async ({ page }) => {
      await page.getByRole('navigation').getByRole('link', { name: 'Blog' }).click()
      await expect(page).toHaveURL(/\/blog$/)
    })
  })

  // ============ NAVBAR - Usuario No Autenticado ============
  test.describe('Navbar - Usuario No Autenticado', () => {
    test('Botón Iniciar Sesión navega a /login', async ({ page }) => {
      const loginButton = page.getByRole('link', { name: 'Iniciar Sesión' }).first()
      await loginButton.click()
      await expect(page).toHaveURL(/\/login$/)
    })

    test('Botón Registrarse navega a /register', async ({ page }) => {
      const registerButton = page.getByRole('link', { name: 'Registrarse' }).first()
      await registerButton.click()
      await expect(page).toHaveURL(/\/register$/)
    })
  })

  // ============ HOME PAGE ============
  test.describe('Home Page - CTAs y Links', () => {
    test('CTA Unirse ahora navega a /register', async ({ page }) => {
      const cta = page.getByRole('link', { name: 'Unirse ahora' })
      await expect(cta).toBeVisible()
      await cta.click()
      await expect(page).toHaveURL(/\/register$/)
    })

    test('CTA Iniciar sesión navega a /login', async ({ page }) => {
      // Hay dos links con "Iniciar sesión", usar el del hero (el que tiene la clase específica)
      const cta = page.locator('a[href="/login"]').filter({ hasText: 'Iniciar sesión' }).first()
      await expect(cta).toBeVisible()
      await cta.click()
      await expect(page).toHaveURL(/\/login$/)
    })

    test('CTA Explorar destinos navega a /categories', async ({ page }) => {
      const cta = page.getByRole('link', { name: 'Explorar destinos' })
      await expect(cta).toBeVisible()
      await cta.click()
      await expect(page).toHaveURL(/\/categories$/)
    })

    test('CTA Ver todos los continentes navega a /categories', async ({ page }) => {
      const cta = page.getByRole('link', { name: 'Ver todos los continentes' })
      if (await cta.isVisible()) {
        await cta.click()
        await expect(page).toHaveURL(/\/categories$/)
      }
    })
  })

  // ============ FOOTER ============
  test.describe('Footer - Links', () => {
    test('Link Foros en footer navega a /forums', async ({ page }) => {
      const footerLink = page.getByRole('contentinfo').getByRole('link', { name: 'Foros' })
      await footerLink.click()
      await expect(page).toHaveURL(/\/forums$/)
    })

    test('Link Continentes en footer navega a /categories', async ({ page }) => {
      const footerLink = page.getByRole('contentinfo').getByRole('link', { name: 'Continentes' })
      await footerLink.click()
      await expect(page).toHaveURL(/\/categories$/)
    })
  })

  // ============ NAVBAR MÓVIL ============
  test.describe('Navbar Móvil', () => {
    test.use({ viewport: { width: 375, height: 667 } }) // iPhone SE

    test('Botón menú móvil abre el menú', async ({ page }) => {
      const menuButton = page.getByRole('button', { name: /abrir menú principal/i })
      await menuButton.click()
      
      // Verificar que el menú está visible
      const mobileMenu = page.locator('#mobile-menu')
      await expect(mobileMenu).toBeVisible()
    })

    test('Menú móvil - Link Inicio navega correctamente', async ({ page }) => {
      await page.getByRole('button', { name: /abrir menú principal/i }).click()
      await page.getByRole('link', { name: 'Inicio' }).click()
      await expect(page).toHaveURL(/\/$/)
    })

    test('Menú móvil - Link Continentes navega correctamente', async ({ page }) => {
      await page.getByRole('button', { name: /abrir menú principal/i }).click()
      // Especificar que es el link dentro del menú móvil
      const mobileMenu = page.locator('#mobile-menu')
      await mobileMenu.getByRole('link', { name: 'Continentes' }).click()
      await expect(page).toHaveURL(/\/categories$/)
    })

    test('Menú móvil - Link Foros navega correctamente', async ({ page }) => {
      await page.getByRole('button', { name: /abrir menú principal/i }).click()
      // Especificar que es el link dentro del menú móvil
      const mobileMenu = page.locator('#mobile-menu')
      await mobileMenu.getByRole('link', { name: 'Foros' }).click()
      await expect(page).toHaveURL(/\/forums$/)
    })

    test('Menú móvil - Link Blog navega correctamente', async ({ page }) => {
      await page.getByRole('button', { name: /abrir menú principal/i }).click()
      await page.getByRole('link', { name: 'Blog' }).click()
      await expect(page).toHaveURL(/\/blog$/)
    })
  })

  // ============ PÁGINAS ESPECÍFICAS ============
  test.describe('Navegación entre páginas', () => {
    test('Desde Home a Categories y volver', async ({ page }) => {
      await page.goto('/')
      await page.getByRole('link', { name: 'Continentes' }).first().click()
      await expect(page).toHaveURL(/\/categories$/)
      
      // Volver usando el logo
      await page.getByRole('link', { name: /ForumViajeros/i }).first().click()
      await expect(page).toHaveURL(/\/$/)
    })

    test('Desde Home a Foros y volver', async ({ page }) => {
      await page.goto('/')
      await page.getByRole('link', { name: 'Foros' }).first().click()
      await expect(page).toHaveURL(/\/forums$/)
      
      await page.getByRole('link', { name: /ForumViajeros/i }).first().click()
      await expect(page).toHaveURL(/\/$/)
    })

    test('Desde Home a Blog y volver', async ({ page }) => {
      await page.goto('/')
      await page.getByRole('link', { name: 'Blog' }).first().click()
      await expect(page).toHaveURL(/\/blog$/)
      
      await page.getByRole('link', { name: /ForumViajeros/i }).first().click()
      await expect(page).toHaveURL(/\/$/)
    })
  })

  // ============ ACCESIBILIDAD ============
  test.describe('Accesibilidad - ARIA Labels y Roles', () => {
    test('Navbar tiene role="navigation"', async ({ page }) => {
      const navbar = page.getByRole('navigation')
      await expect(navbar).toBeVisible()
    })

    test('Footer tiene role="contentinfo"', async ({ page }) => {
      const footer = page.getByRole('contentinfo')
      await expect(footer).toBeVisible()
    })

    test('Botones tienen aria-labels apropiados', async ({ page }) => {
      // Solo verificar en vista móvil - usar viewport en el test directamente
      await page.setViewportSize({ width: 375, height: 667 })
      await page.goto('/')
      await page.waitForLoadState('networkidle')
      const menuButton = page.getByRole('button', { name: /abrir menú principal/i })
      await expect(menuButton).toBeVisible()
    })

    test('Links tienen texto descriptivo', async ({ page }) => {
      const links = [
        { name: 'Inicio', url: '/' },
        { name: 'Continentes', url: '/categories' },
        { name: 'Foros', url: '/forums' },
        { name: 'Blog', url: '/blog' }
      ]

      for (const link of links) {
        const linkElement = page.getByRole('link', { name: link.name }).first()
        await expect(linkElement).toBeVisible()
        expect(await linkElement.getAttribute('href')).toContain(link.url)
      }
    })
  })

  // ============ FOCUS Y KEYBOARD NAVIGATION ============
  test.describe('Navegación por Teclado', () => {
    test('Tab navega entre links del navbar', async ({ page }) => {
      await page.keyboard.press('Tab')
      // El primer elemento enfocado debería ser un link
      const focused = page.locator(':focus')
      await expect(focused).toBeVisible()
    })

    test('Enter activa el link enfocado', async ({ page }) => {
      // Enfocar un link específico primero
      const forumsLink = page.getByRole('navigation').getByRole('link', { name: 'Foros' })
      await forumsLink.focus()
      await page.keyboard.press('Enter')
      // Debería haber navegado a /forums
      await expect(page).toHaveURL(/\/forums$/)
    })
  })
})

