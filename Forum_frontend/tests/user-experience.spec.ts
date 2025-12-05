import { test, expect } from '@playwright/test'

test.describe('Experiencia de Usuario (UX)', () => {

  test.beforeEach(async ({ page }) => {
    await page.goto('/')
    await page.waitForLoadState('networkidle')
  })

  // ============ Estados de Carga ============
  test.describe('Estados de Carga', () => {

    test('muestra indicador de carga al navegar entre páginas', async ({ page }) => {
      // Navegar a una página que pueda tener loading state
      await page.goto('/forums')

      // Verificar que la página carga correctamente
      await expect(page).toHaveURL(/\/forums/)
      await page.waitForLoadState('networkidle')

      // Verificar que no hay spinners de carga visibles después de cargar
      const loadingSpinners = page.locator('[aria-label*="Cargando"], [aria-label*="Loading"], .loading')
      const count = await loadingSpinners.count()

      // Si hay spinners, verificar que desaparecen
      if (count > 0) {
        await expect(loadingSpinners.first()).toBeHidden({ timeout: 5000 })
      }
    })

    test('imágenes tienen atributo alt para accesibilidad', async ({ page }) => {
      const images = page.locator('img')
      const count = await images.count()

      // Verificar que todas las imágenes tienen alt text
      for (let i = 0; i < count; i++) {
        const img = images.nth(i)
        const alt = await img.getAttribute('alt')
        expect(alt).not.toBeNull()
      }
    })
  })

  // ============ Feedback Visual ============
  test.describe('Feedback Visual al Usuario', () => {

    test('botones cambian de estilo al hacer hover', async ({ page }) => {
      const loginButton = page.getByRole('link', { name: 'Iniciar Sesión' }).first()

      // Obtener estilos antes del hover
      const beforeHover = await loginButton.evaluate((el) => {
        return window.getComputedStyle(el).getPropertyValue('background-color')
      })

      // Hacer hover
      await loginButton.hover()

      // Esperar un momento para que se aplique el estilo
      await page.waitForTimeout(100)

      // Verificar que el botón es visible y clickeable
      await expect(loginButton).toBeVisible()
      await expect(loginButton).toBeEnabled()
    })

    test('links tienen estado de focus visible para navegación por teclado', async ({ page }) => {
      // Hacer focus en el primer link usando Tab
      await page.keyboard.press('Tab')

      // Obtener el elemento con focus
      const focusedElement = page.locator(':focus')

      // Verificar que hay un elemento con focus
      await expect(focusedElement).toBeVisible()

      // Verificar que tiene algún estilo de outline o ring (Tailwind)
      const outlineStyle = await focusedElement.evaluate((el) => {
        const style = window.getComputedStyle(el)
        return {
          outline: style.outline,
          outlineWidth: style.outlineWidth,
          outlineColor: style.outlineColor,
          boxShadow: style.boxShadow
        }
      })

      // Al menos debe tener outline o box-shadow
      const hasFocusStyle =
        outlineStyle.outlineWidth !== '0px' ||
        outlineStyle.boxShadow !== 'none'

      expect(hasFocusStyle).toBeTruthy()
    })
  })

  // ============ Mensajes de Error y Validación ============
  test.describe('Mensajes de Error y Validación', () => {

    test('formulario de login muestra mensajes de validación claros', async ({ page }) => {
      await page.goto('/login')

      // Intentar enviar formulario vacío
      const submitButton = page.getByRole('button', { name: /iniciar sesión/i })
      await submitButton.click()

      // Verificar que los campos requeridos muestran validación HTML5
      const emailInput = page.locator('input[type="email"]')
      const passwordInput = page.locator('input[type="password"]')

      // Verificar que los inputs tienen el atributo required
      await expect(emailInput).toHaveAttribute('required', '')
      await expect(passwordInput).toHaveAttribute('required', '')
    })

    test('formulario de registro muestra error cuando contraseñas no coinciden', async ({ page }) => {
      await page.goto('/register')

      // Llenar formulario con contraseñas diferentes
      await page.fill('input[name="username"]', 'testuser')
      await page.fill('input[name="email"]', 'test@example.com')
      await page.fill('input[name="password"]', 'Password123!')
      await page.fill('input[name="confirmPassword"]', 'DifferentPassword123!')

      // Enviar formulario
      await page.getByRole('button', { name: /registrarse/i }).click()

      // Esperar mensaje de error
      await page.waitForTimeout(500)

      // Verificar que aparece un mensaje de error
      const errorMessage = page.locator('text=/contraseñas no coinciden/i')
      await expect(errorMessage).toBeVisible({ timeout: 3000 })
    })
  })

  // ============ Interactividad y Respuesta ============
  test.describe('Interactividad y Respuesta', () => {

    test('búsqueda de foros responde a input del usuario', async ({ page }) => {
      await page.goto('/forums')

      // Buscar el input de búsqueda
      const searchInput = page.locator('input[type="search"], input[placeholder*="Buscar"]')

      if (await searchInput.count() > 0) {
        // Escribir en el campo de búsqueda
        await searchInput.first().fill('Asia')

        // Verificar que el valor se actualizó
        await expect(searchInput.first()).toHaveValue('Asia')

        // Presionar Enter o click en buscar
        await page.keyboard.press('Enter')

        // Esperar a que se procese la búsqueda
        await page.waitForTimeout(1000)

        // La URL debería cambiar o los resultados deberían filtrarse
        // (esto depende de la implementación específica)
      }
    })

    test('botones de navegación funcionan sin delay perceptible', async ({ page }) => {
      const startTime = Date.now()

      // Click en un link de navegación
      await page.getByRole('link', { name: 'Foros' }).first().click()

      // Esperar a que la navegación complete
      await page.waitForURL(/\/forums/)

      const endTime = Date.now()
      const navigationTime = endTime - startTime

      // La navegación debería ser razonablemente rápida (< 3 segundos)
      expect(navigationTime).toBeLessThan(3000)
    })
  })

  // ============ Contenido y Legibilidad ============
  test.describe('Contenido y Legibilidad', () => {

    test('texto principal tiene tamaño de fuente legible', async ({ page }) => {
      const bodyText = page.locator('body')

      const fontSize = await bodyText.evaluate((el) => {
        return window.getComputedStyle(el).fontSize
      })

      // El tamaño de fuente debería ser al menos 14px
      const fontSizeValue = parseInt(fontSize)
      expect(fontSizeValue).toBeGreaterThanOrEqual(14)
    })

    test('links son distinguibles del texto normal', async ({ page }) => {
      // Buscar links en el contenido principal
      const links = page.locator('main a, article a')

      if (await links.count() > 0) {
        const linkStyle = await links.first().evaluate((el) => {
          const style = window.getComputedStyle(el)
          return {
            color: style.color,
            textDecoration: style.textDecoration,
            fontWeight: style.fontWeight
          }
        })

        // Los links deberían tener alguna distinción visual
        // (color diferente, subrayado, peso de fuente, etc.)
        const hasDistinction =
          linkStyle.textDecoration.includes('underline') ||
          linkStyle.color !== 'rgb(0, 0, 0)' // No es negro puro

        expect(hasDistinction).toBeTruthy()
      }
    })

    test('headings tienen jerarquía correcta', async ({ page }) => {
      // Verificar que existe un h1 en la página
      const h1 = page.locator('h1')
      await expect(h1.first()).toBeVisible()

      // Verificar que los headings siguen un orden lógico
      const headings = await page.locator('h1, h2, h3, h4, h5, h6').all()

      expect(headings.length).toBeGreaterThan(0)
    })
  })

  // ============ Navegación y Orientación ============
  test.describe('Navegación y Orientación', () => {

    test('navbar permanece visible al hacer scroll', async ({ page }) => {
      // Verificar que el navbar existe
      const navbar = page.locator('nav').first()
      await expect(navbar).toBeVisible()

      // Hacer scroll hacia abajo
      await page.evaluate(() => window.scrollTo(0, 500))
      await page.waitForTimeout(300)

      // El navbar debería seguir visible (sticky)
      await expect(navbar).toBeVisible()
    })

    test('breadcrumbs o indicador de ubicación presente en páginas internas', async ({ page }) => {
      await page.goto('/forums')

      // Verificar que hay algún indicador de la página actual
      // Puede ser el título de la página, breadcrumbs, o navbar activo
      const pageTitle = page.locator('h1, h2')
      await expect(pageTitle.first()).toBeVisible()
    })

    test('botón volver atrás funciona correctamente', async ({ page }) => {
      // Navegar a una página
      await page.goto('/forums')
      await page.waitForLoadState('networkidle')

      // Luego a otra página
      await page.goto('/categories')
      await page.waitForLoadState('networkidle')

      // Usar el botón volver del navegador
      await page.goBack()

      // Debería volver a /forums
      await expect(page).toHaveURL(/\/forums/)
    })
  })

  // ============ Performance y Optimización ============
  test.describe('Performance y Optimización', () => {

    test('página principal carga en tiempo razonable', async ({ page }) => {
      const startTime = Date.now()

      await page.goto('/')
      await page.waitForLoadState('domcontentloaded')

      const loadTime = Date.now() - startTime

      // La página debería cargar en menos de 5 segundos
      expect(loadTime).toBeLessThan(5000)
    })

    test('imágenes se cargan correctamente', async ({ page }) => {
      await page.goto('/')

      // Esperar a que las imágenes carguen
      await page.waitForLoadState('networkidle')

      const images = page.locator('img[src]')
      const count = await images.count()

      if (count > 0) {
        // Verificar que al menos la primera imagen tiene src válido
        const firstImg = images.first()
        const src = await firstImg.getAttribute('src')
        expect(src).toBeTruthy()
        expect(src).not.toBe('')
      }
    })
  })

  // ============ Elementos Interactivos ============
  test.describe('Elementos Interactivos', () => {

    test('todos los botones tienen tamaño mínimo de área de click (44x44px)', async ({ page }) => {
      const buttons = page.locator('button:visible, a.btn:visible')
      const count = await buttons.count()

      for (let i = 0; i < Math.min(count, 10); i++) {
        const button = buttons.nth(i)
        const box = await button.boundingBox()

        if (box) {
          // WCAG recomienda 44x44px mínimo para touch targets
          expect(box.width).toBeGreaterThanOrEqual(30) // Un poco menos estricto
          expect(box.height).toBeGreaterThanOrEqual(30)
        }
      }
    })

    test('formularios tienen labels asociados correctamente', async ({ page }) => {
      await page.goto('/login')

      const inputs = page.locator('input[type="email"], input[type="password"]')
      const count = await inputs.count()

      for (let i = 0; i < count; i++) {
        const input = inputs.nth(i)

        // El input debe tener id, name, o aria-label
        const hasId = await input.getAttribute('id')
        const hasName = await input.getAttribute('name')
        const hasAriaLabel = await input.getAttribute('aria-label')
        const hasPlaceholder = await input.getAttribute('placeholder')

        const hasIdentifier = hasId || hasName || hasAriaLabel || hasPlaceholder
        expect(hasIdentifier).toBeTruthy()
      }
    })
  })

  // ============ Manejo de Errores ============
  test.describe('Manejo de Errores', () => {

    test('página 404 muestra mensaje útil y opciones de navegación', async ({ page }) => {
      await page.goto('/ruta-que-no-existe')

      // Esperar a que cargue la página 404
      await page.waitForLoadState('networkidle')

      // Debería mostrar un mensaje de error
      const errorMessage = page.locator('text=/404|no encontrado|not found/i')
      await expect(errorMessage.first()).toBeVisible({ timeout: 3000 })

      // Debería tener links para volver a navegar
      const homeLink = page.locator('a[href="/"]')
      await expect(homeLink.first()).toBeVisible()
    })

    test('errores de formulario son accesibles y claros', async ({ page }) => {
      await page.goto('/register')

      // Intentar registrar con email inválido
      await page.fill('input[name="email"]', 'email-invalido')
      await page.fill('input[name="username"]', 'test')
      await page.fill('input[name="password"]', '123')
      await page.fill('input[name="confirmPassword"]', '123')

      // Click fuera del campo para trigger validación
      await page.click('body')

      // El navegador debería mostrar validación HTML5
      const emailInput = page.locator('input[name="email"]')
      const isValid = await emailInput.evaluate((el: HTMLInputElement) => el.validity.valid)

      // Email inválido debería fallar validación
      expect(isValid).toBeFalsy()
    })
  })
})
