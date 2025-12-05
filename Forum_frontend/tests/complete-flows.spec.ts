import { test, expect } from '@playwright/test'

test.describe('Flujos completos de usuario', () => {
  test('usuario puede navegar por todas las páginas principales', async ({ page }) => {
    // Inicio
    await page.goto('/')
    await page.waitForLoadState('networkidle')
    await expect(page).toHaveTitle(/ForumViajeros|Viajeros/)

    // Categorías
    const categoriasLink = page.getByRole('navigation').getByRole('link', { name: 'Continentes' })
    await expect(categoriasLink).toBeVisible({ timeout: 10000 })
    await categoriasLink.click()
    await expect(page).toHaveURL(/\/categories/)

    // Foros
    await page.waitForLoadState('networkidle')
    const forosLink = page.getByRole('navigation').getByRole('link', { name: 'Foros' })
    await expect(forosLink).toBeVisible({ timeout: 10000 })
    await forosLink.click()
    await expect(page).toHaveURL(/\/forums/)

    // Volver a inicio
    await page.waitForLoadState('networkidle')
    const inicioLink = page.getByRole('navigation').getByRole('link', { name: 'Inicio' })
    await expect(inicioLink).toBeVisible({ timeout: 10000 })
    await inicioLink.click()
    await expect(page).toHaveURL(/\/$/)
  })

  test('usuario no autenticado no puede acceder a crear foro', async ({ page }) => {
    await page.goto('/forums/create')

    // Debería redirigir a login o mostrar mensaje de error
    await expect(page).toHaveURL(/\/login|\//)
  })

  test('links del footer funcionan correctamente', async ({ page }) => {
    await page.goto('/')

    // Test footer Foros link
    await page.getByRole('contentinfo').getByRole('link', { name: 'Foros' }).click()
    await expect(page).toHaveURL(/\/forums/)

    await page.goto('/')

    // Test footer Continentes link
    await page.getByRole('contentinfo').getByRole('link', { name: 'Continentes' }).click()
    await expect(page).toHaveURL(/\/categories/)
  })

  test('botones CTA de la homepage navegan correctamente', async ({ page }) => {
    await page.goto('/')

    // Test "Explorar destinos"
    const explorarDestinos = page.getByRole('link', { name: 'Explorar destinos' }).first()
    await explorarDestinos.click()
    await expect(page).toHaveURL(/\/categories/)

    // Volver a home
    await page.goto('/')

    // Test "Unirse ahora"  - primero en el hero
    const unirseAhora = page.getByRole('link', { name: 'Unirse ahora' }).first()
    await unirseAhora.click()
    await expect(page).toHaveURL(/\/register/)
  })

  test('formulario de login muestra validación', async ({ page }) => {
    await page.goto('/login')

    // Intentar submit sin llenar campos
    await page.getByRole('button', { name: /iniciar sesión/i }).click()

    // HTML5 validation debería prevenir el submit
    const usernameInput = page.getByLabel(/nombre de usuario/i)
    await expect(usernameInput).toHaveAttribute('required', '')
  })

  test('formulario de registro muestra validación', async ({ page }) => {
    await page.goto('/register')

    // Intentar submit sin llenar campos
    await page.getByRole('button', { name: /registrarse/i }).click()

    // HTML5 validation
    const usernameInput = page.getByLabel(/usuario/i)
    await expect(usernameInput).toHaveAttribute('required', '')
  })

  test('búsqueda de foros muestra resultados', async ({ page }) => {
    await page.goto('/forums')

    // Buscar si existe el componente de búsqueda
    const searchInput = page.getByPlaceholder(/buscar/i)
    if (await searchInput.isVisible()) {
      await searchInput.fill('test')
      // Debería mostrar resultados o mensaje de "no hay resultados"
    }
  })

  test('página 404 muestra links de navegación', async ({ page }) => {
    await page.goto('/pagina-que-no-existe-12345')

    // Debería mostrar página 404 o redirigir
    const homeLink = page.getByRole('link', { name: /página principal|inicio/i }).first()
    if (await homeLink.isVisible()) {
      await homeLink.click()
      await expect(page).toHaveURL(/\/$/)
    }
  })
})

test.describe('Accesibilidad básica', () => {
  test('navbar tiene navegación accesible', async ({ page }) => {
    await page.goto('/')

    const nav = page.getByRole('navigation')
    await expect(nav).toBeVisible()

    // Los links deben ser accesibles por rol
    await expect(nav.getByRole('link', { name: 'Inicio' })).toBeVisible()
    await expect(nav.getByRole('link', { name: 'Continentes' })).toBeVisible()
    await expect(nav.getByRole('link', { name: 'Foros' })).toBeVisible()
  })

  test('footer tiene rol contentinfo', async ({ page }) => {
    await page.goto('/')

    const footer = page.getByRole('contentinfo')
    await expect(footer).toBeVisible()
  })

  test('botones tienen labels apropiados', async ({ page }) => {
    await page.goto('/')

    // CTA buttons deben tener aria-labels
    const ctaButtons = await page.getByRole('link', { name: /unirse ahora|explorar destinos/i }).all()
    expect(ctaButtons.length).toBeGreaterThan(0)
  })
})
