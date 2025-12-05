import React from 'react'
import { render, screen, waitFor } from '@testing-library/react'
import HomePage from './HomePage'
import { MemoryRouter } from 'react-router-dom'
import { vi } from 'vitest'

vi.mock('../services/forumService', () => ({ default: { getAllForums: async () => ({ content: [] }) } }))
vi.mock('../services/categoryService', () => ({ default: { getAllCategories: async () => [] } }))

describe('HomePage accesibilidad', () => {
  it('CTA Unirse ahora tiene aria-label y es visible', async () => {
    render(
      <MemoryRouter>
        <HomePage />
      </MemoryRouter>
    )

    // Wait for async state updates to complete
    await waitFor(() => {
      const cta = screen.getByRole('link', { name: 'Unirse ahora' })
      expect(cta).toBeVisible()
      expect(cta).toHaveAttribute('aria-label', 'Unirse ahora')
    })
  })
})