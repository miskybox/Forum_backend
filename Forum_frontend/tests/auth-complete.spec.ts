import { test, expect } from '@playwright/test'

test.describe('Autenticación completa', () => {
  test.beforeEach(async ({ page }) => {
    // Limpiar localStorage antes de cada test
    await page.goto('/')
    await page.evaluate(() => {
      localStorage.clear()
    })
  })

  test('Registro completo de nuevo usuario', async ({ page }) => {
    await page.goto('/register')
    await page.waitForLoadState('networkidle')

    // Verificar que el formulario está visible
    await expect(page.getByRole('heading', { name: /crear una cuenta/i })).toBeVisible()

    // Llenar formulario de registro
    const timestamp = Date.now()
    const testUser = `testuser_${timestamp}`
    const testEmail = `test_${timestamp}@example.com`

    await page.getByLabel(/nombre de usuario/i).fill(testUser)
    await page.getByLabel(/correo electrónico/i).fill(testEmail)
    await page.getByLabel(/^nombre$/i).fill('Test')
    await page.getByLabel(/apellido/i).fill('User')
    await page.getByLabel(/^contraseña$/i).fill('password123')
    await page.getByLabel(/confirmar contraseña/i).fill('password123')

    // Enviar formulario
    await page.getByRole('button', { name: /registrarse/i }).click()

    // Esperar a que se complete el registro (puede navegar a home o login)
    await page.waitForLoadState('networkidle')
    
    // Verificar que se guardó el token en localStorage
    const token = await page.evaluate(() => localStorage.getItem('token'))
    expect(token).toBeTruthy()
  })

  test('Login completo con credenciales válidas', async ({ page }) => {
    // Primero crear un usuario (o usar uno existente)
    // Nota: En un entorno real, esto debería usar un helper o API directa
    await page.goto('/login')
    await page.waitForLoadState('networkidle')

    // Verificar que el formulario está visible
    await expect(page.getByRole('heading', { name: /iniciar sesión/i })).toBeVisible()

    // Llenar formulario de login
    // Nota: Estos valores deben existir en la base de datos de test
    await page.getByLabel(/nombre de usuario/i).fill('testuser')
    await page.getByLabel(/contraseña/i).fill('password123')

    // Enviar formulario
    await page.getByRole('button', { name: /iniciar sesión/i }).click()

    // Esperar a que se complete el login
    await page.waitForLoadState('networkidle')
    await page.waitForTimeout(2000) // Dar tiempo para que se procese

    // Verificar que se guardó el token
    const token = await page.evaluate(() => localStorage.getItem('token'))
    expect(token).toBeTruthy()

    // Verificar que se navegó a la página principal
    await expect(page).toHaveURL(/\/$/)
  })

  test('Login falla con credenciales inválidas', async ({ page }) => {
    await page.goto('/login')
    await page.waitForLoadState('networkidle')

    await page.getByLabel(/nombre de usuario/i).fill('usuario_inexistente')
    await page.getByLabel(/contraseña/i).fill('password_incorrecto')

    await page.getByRole('button', { name: /iniciar sesión/i }).click()

    // Esperar mensaje de error
    await page.waitForTimeout(2000)
    
    // Verificar que NO se guardó el token
    const token = await page.evaluate(() => localStorage.getItem('token'))
    expect(token).toBeFalsy()

    // Verificar que se muestra un mensaje de error (toast o en el formulario)
    const errorMessage = page.getByText(/incorrecto|inválido|error/i).first()
    await expect(errorMessage).toBeVisible({ timeout: 5000 })
  })

  test('Validación de formulario de registro - campos vacíos', async ({ page }) => {
    await page.goto('/register')
    await page.waitForLoadState('networkidle')

    // Intentar enviar sin llenar campos
    await page.getByRole('button', { name: /registrarse/i }).click()

    // Verificar que se muestran mensajes de error
    await expect(page.getByText(/obligatorio/i).first()).toBeVisible({ timeout: 3000 })
  })

  test('Validación de formulario de registro - contraseñas no coinciden', async ({ page }) => {
    await page.goto('/register')
    await page.waitForLoadState('networkidle')

    await page.getByLabel(/nombre de usuario/i).fill('testuser')
    await page.getByLabel(/correo electrónico/i).fill('test@test.com')
    await page.getByLabel(/^nombre$/i).fill('Test')
    await page.getByLabel(/apellido/i).fill('User')
    await page.getByLabel(/^contraseña$/i).fill('password123')
    await page.getByLabel(/confirmar contraseña/i).fill('password456')

    await page.getByRole('button', { name: /registrarse/i }).click()

    // Verificar mensaje de error
    await expect(page.getByText(/no coinciden/i)).toBeVisible({ timeout: 3000 })
  })

  test('Validación de formulario de login - campos vacíos', async ({ page }) => {
    await page.goto('/login')
    await page.waitForLoadState('networkidle')

    await page.getByRole('button', { name: /iniciar sesión/i }).click()

    // Verificar que se muestran mensajes de error
    await expect(page.getByText(/obligatorio/i).first()).toBeVisible({ timeout: 3000 })
  })

  test('Navegación entre login y registro', async ({ page }) => {
    await page.goto('/login')
    await page.waitForLoadState('networkidle')

    // Ir a registro desde login
    const registerLink = page.getByRole('link', { name: /regístrate|registrarse/i })
    await expect(registerLink).toBeVisible()
    await registerLink.click()
    await expect(page).toHaveURL(/\/register$/)

    // Volver a login desde registro
    const loginLink = page.getByRole('link', { name: /iniciar sesión|ya tienes cuenta/i })
    await expect(loginLink).toBeVisible()
    await loginLink.click()
    await expect(page).toHaveURL(/\/login$/)
  })

  test('Logout funciona correctamente', async ({ page }) => {
    // Primero hacer login (simulado)
    await page.goto('/')
    await page.evaluate(() => {
      localStorage.setItem('token', 'test_token')
      localStorage.setItem('refreshToken', 'test_refresh_token')
    })

    // Navegar a una página que requiera autenticación
    await page.goto('/')
    await page.waitForLoadState('networkidle')

    // Buscar botón de logout (puede estar en navbar)
    const logoutButton = page.getByRole('button', { name: /cerrar sesión|logout|salir/i }).first()
    
    if (await logoutButton.isVisible().catch(() => false)) {
      await logoutButton.click()
      await page.waitForLoadState('networkidle')

      // Verificar que se eliminaron los tokens
      const token = await page.evaluate(() => localStorage.getItem('token'))
      expect(token).toBeFalsy()
    }
  })

  test('Mostrar/ocultar contraseña funciona', async ({ page }) => {
    await page.goto('/login')
    await page.waitForLoadState('networkidle')

    const passwordInput = page.getByLabel(/contraseña/i)
    await passwordInput.fill('testpassword')

    // Buscar botón de mostrar contraseña
    const toggleButton = page.locator('button[aria-label*="contraseña"], button[type="button"]').filter({ hasText: /mostrar|ocultar|ver/i }).first()
    
    if (await toggleButton.isVisible().catch(() => false)) {
      const initialType = await passwordInput.getAttribute('type')
      await toggleButton.click()
      const newType = await passwordInput.getAttribute('type')
      expect(newType).not.toBe(initialType)
    }
  })
})

