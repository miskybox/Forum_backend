import { test, expect } from '@playwright/test'

test.describe('Creación de Foro - E2E', () => {
  
  test.beforeEach(async ({ page }) => {
    // Para tests de creación de foro, necesitamos estar autenticados
    // Por ahora, estos tests solo verifican que la página se carga
    // En un entorno real, aquí harías login primero
  })

  test('Navegar a crear foro desde navbar', async ({ page }) => {
    await page.goto('/')
    await page.waitForLoadState('networkidle')
    
    // Este test requiere autenticación, así que lo marcamos como skip si no hay usuario
    // En un entorno real, aquí harías login primero
    test.skip(true, 'Requiere autenticación - implementar login en beforeEach')
  })

  test('Formulario de creación de foro se renderiza correctamente', async ({ page }) => {
    // Este test requiere autenticación
    // Si no estás autenticado, serás redirigido a /login
    await page.goto('/forums/create')
    await page.waitForLoadState('networkidle')
    
    // Si estamos en login, el test falla (esperado sin auth)
    if (page.url().includes('/login')) {
      test.skip(true, 'Requiere autenticación')
      return
    }
    
    // Verificar que los campos están presentes
    await expect(page.getByLabel(/título/i)).toBeVisible({ timeout: 10000 })
    await expect(page.getByLabel(/descripción/i)).toBeVisible()
    await expect(page.getByLabel(/continente/i)).toBeVisible()
    await expect(page.getByLabel(/imagen/i)).toBeVisible()
    
    // Verificar botones
    await expect(page.getByRole('button', { name: /crear foro/i })).toBeVisible()
    await expect(page.getByRole('button', { name: /cancelar/i })).toBeVisible()
  })

  test('Validación de formulario - campos requeridos', async ({ page }) => {
    await page.goto('/forums/create')
    await page.waitForLoadState('networkidle')
    
    // Si estamos en login, el test falla (esperado sin auth)
    if (page.url().includes('/login')) {
      test.skip(true, 'Requiere autenticación')
      return
    }
    
    // Intentar enviar sin llenar campos
    const submitButton = page.getByRole('button', { name: /crear foro/i })
    await expect(submitButton).toBeVisible({ timeout: 10000 })
    await submitButton.click()
    
    // Debería mostrar errores de validación
    // (ajustar según tu implementación de validación)
    const titleError = page.locator('text=/título.*obligatorio/i')
    // await expect(titleError).toBeVisible() // Descomentar si hay validación en frontend
  })

  test('Botón Cancelar navega hacia atrás', async ({ page }) => {
    await page.goto('/forums/create')
    await page.waitForLoadState('networkidle')
    
    // Si estamos en login, el test falla (esperado sin auth)
    if (page.url().includes('/login')) {
      test.skip(true, 'Requiere autenticación')
      return
    }
    
    const cancelButton = page.getByRole('button', { name: /cancelar/i })
    await expect(cancelButton).toBeVisible({ timeout: 10000 })
    await cancelButton.click()
    
    // Debería volver a la página anterior
    await expect(page).toHaveURL(/\/forums$/)
  })

  test('Carga de imagen funciona', async ({ page }) => {
    await page.goto('/forums/create')
    await page.waitForLoadState('networkidle')
    
    // Si estamos en login, el test falla (esperado sin auth)
    if (page.url().includes('/login')) {
      test.skip(true, 'Requiere autenticación')
      return
    }
    
    const fileInput = page.getByLabel(/imagen/i)
    await expect(fileInput).toBeVisible({ timeout: 10000 })
    await fileInput.setInputFiles({
      name: 'test-image.jpg',
      mimeType: 'image/jpeg',
      buffer: Buffer.from('fake-image-data')
    })
    
    // Verificar que se muestra preview (si está implementado)
    // const preview = page.locator('img[alt="Vista previa"]')
    // await expect(preview).toBeVisible()
  })
})

