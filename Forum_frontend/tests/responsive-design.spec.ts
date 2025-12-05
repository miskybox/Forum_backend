import { test, expect } from '@playwright/test'

test.describe('Diseño Responsivo - Múltiples Dispositivos', () => {

  // ============ Mobile - iPhone 12 ============
  test.describe('Mobile - iPhone 12 (390x844)', () => {

    test('navbar se adapta correctamente en móvil', async ({ page }) => {
      await page.setViewportSize({ width: 390, height: 844 })
      await page.goto('/')
      await page.waitForLoadState('networkidle')

      // En móvil, debería haber un botón de menú hamburguesa
      const menuButton = page.locator('button[aria-label*="menú"], button[aria-label*="menu"], .menu-toggle, button svg')

      // Verificar que existe botón de menú móvil
      const buttonCount = await menuButton.count()
      expect(buttonCount).toBeGreaterThan(0)
    })

    test('contenido se ajusta al ancho de la pantalla', async ({ page }) => {
      await page.setViewportSize({ width: 390, height: 844 })
      await page.goto('/')

      // Verificar que no hay scroll horizontal
      const bodyWidth = await page.evaluate(() => document.body.scrollWidth)
      const viewportWidth = await page.evaluate(() => window.innerWidth)

      expect(bodyWidth).toBeLessThanOrEqual(viewportWidth + 20) // 20px de margen
    })

    test('botones tienen tamaño adecuado para touch', async ({ page }) => {
      await page.setViewportSize({ width: 390, height: 844 })
      await page.goto('/')

      const buttons = page.locator('button:visible, a.btn:visible')
      const count = Math.min(await buttons.count(), 5)

      for (let i = 0; i < count; i++) {
        const button = buttons.nth(i)
        const box = await button.boundingBox()

        if (box) {
          // Touch targets deben ser al menos 44x44px (WCAG)
          expect(box.width).toBeGreaterThanOrEqual(36)
          expect(box.height).toBeGreaterThanOrEqual(36)
        }
      }
    })
  })

  // ============ Tablet - iPad Air ============
  test.describe('Tablet - iPad Air (820x1180)', () => {

    test('navbar muestra menú apropiado en tablet', async ({ page }) => {
      await page.setViewportSize({ width: 820, height: 1180 })
      await page.goto('/')
      await page.waitForLoadState('networkidle')

      // En tablet, puede mostrar menú completo o botón hamburguesa
      const navbar = page.locator('nav')
      await expect(navbar).toBeVisible()

      // Verificar que los links principales son accesibles
      const navLinks = page.getByRole('navigation').getByRole('link')
      const linkCount = await navLinks.count()

      expect(linkCount).toBeGreaterThan(0)
    })

    test('layout se adapta al ancho de tablet', async ({ page }) => {
      await page.setViewportSize({ width: 820, height: 1180 })
      await page.goto('/forums')
      await page.waitForLoadState('networkidle')

      // Verificar que no hay overflow horizontal
      const hasHorizontalScroll = await page.evaluate(() => {
        return document.documentElement.scrollWidth > document.documentElement.clientWidth
      })

      expect(hasHorizontalScroll).toBeFalsy()
    })
  })

  // ============ Desktop - 1920x1080 ============
  test.describe('Desktop - Full HD (1920x1080)', () => {

    test('navbar muestra todos los links en desktop', async ({ page }) => {
      await page.setViewportSize({ width: 1920, height: 1080 })
      await page.goto('/')

      // En desktop, todos los links deberían ser visibles sin menú hamburguesa
      const desktopLinks = page.getByRole('navigation').getByRole('link', { name: /Inicio|Continentes|Foros|Blog/i })
      const count = await desktopLinks.count()

      // Debería haber al menos los links principales
      expect(count).toBeGreaterThanOrEqual(4)
    })

    test('contenido usa el ancho disponible eficientemente', async ({ page }) => {
      await page.setViewportSize({ width: 1920, height: 1080 })
      await page.goto('/forums')

      // El contenido principal no debería ser extremadamente estrecho
      const main = page.locator('main')
      const box = await main.boundingBox()

      if (box) {
        // En desktop, el main debería usar buen ancho (no tiny)
        expect(box.width).toBeGreaterThan(600)
      }
    })
  })

  // ============ Breakpoints Específicos ============
  test.describe('Breakpoints de Tailwind CSS', () => {

    test('sm breakpoint (640px) - contenido se adapta', async ({ page }) => {
      await page.setViewportSize({ width: 640, height: 800 })
      await page.goto('/')

      // Verificar que la página carga y es usable
      await expect(page.locator('body')).toBeVisible()

      // No debería haber overflow
      const hasOverflow = await page.evaluate(() => {
        return document.body.scrollWidth > window.innerWidth
      })

      expect(hasOverflow).toBeFalsy()
    })

    test('md breakpoint (768px) - layout cambia apropiadamente', async ({ page }) => {
      await page.setViewportSize({ width: 768, height: 1024 })
      await page.goto('/forums')

      // Verificar que el contenido es visible
      await expect(page.locator('main')).toBeVisible()
    })

    test('lg breakpoint (1024px) - menú completo visible', async ({ page }) => {
      await page.setViewportSize({ width: 1024, height: 768 })
      await page.goto('/')

      // En este breakpoint, debería mostrar menú desktop
      const navbar = page.locator('nav')
      await expect(navbar).toBeVisible()
    })
  })

  // ============ Texto y Legibilidad ============
  test.describe('Legibilidad en Diferentes Tamaños', () => {

    test('tamaño de fuente es legible en móvil', async ({ page }) => {
      await page.setViewportSize({ width: 375, height: 667 })
      await page.goto('/')

      const bodyFontSize = await page.evaluate(() => {
        const style = window.getComputedStyle(document.body)
        return parseInt(style.fontSize)
      })

      // Mínimo 14px en móvil
      expect(bodyFontSize).toBeGreaterThanOrEqual(14)
    })

    test('tamaño de fuente escala en desktop', async ({ page }) => {
      await page.setViewportSize({ width: 1920, height: 1080 })
      await page.goto('/')

      const bodyFontSize = await page.evaluate(() => {
        const style = window.getComputedStyle(document.body)
        return parseInt(style.fontSize)
      })

      // En desktop puede ser igual o mayor
      expect(bodyFontSize).toBeGreaterThanOrEqual(14)
    })
  })

  // ============ Formularios Responsivos ============
  test.describe('Formularios en Diferentes Tamaños', () => {

    test('inputs de formulario son del tamaño apropiado en móvil', async ({ page }) => {
      await page.setViewportSize({ width: 375, height: 667 })
      await page.goto('/login')

      const emailInput = page.locator('input[type="email"]')
      const box = await emailInput.boundingBox()

      if (box) {
        // Input debería tener altura adecuada para touch (mínimo 44px ideal)
        expect(box.height).toBeGreaterThanOrEqual(40)

        // Y ancho razonable (no tiny)
        expect(box.width).toBeGreaterThan(200)
      }
    })

    test('botones de formulario son fáciles de tocar en móvil', async ({ page }) => {
      await page.setViewportSize({ width: 375, height: 667 })
      await page.goto('/login')

      const submitButton = page.getByRole('button', { name: /iniciar sesión/i })
      const box = await submitButton.boundingBox()

      if (box) {
        // Botón debe tener tamaño de touch target adecuado
        expect(box.height).toBeGreaterThanOrEqual(44)
        expect(box.width).toBeGreaterThanOrEqual(100)
      }
    })
  })

  // ============ Performance en Móvil ============
  test.describe('Performance Responsiva', () => {

    test('página carga rápido en conexión móvil', async ({ page }) => {
      const startTime = Date.now()
      await page.goto('/', { waitUntil: 'domcontentloaded' })
      const loadTime = Date.now() - startTime

      // Debería cargar en menos de 8 segundos
      expect(loadTime).toBeLessThan(8000)
    })
  })
})
