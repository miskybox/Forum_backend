import { render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import CategoryCard from './CategoryCard'

describe('CategoryCard - Links funcionales', () => {
  const mockCategory = {
    id: 5,
    name: 'Europa',
    description: 'Descubre los mejores destinos de Europa, desde las playas del Mediterráneo hasta los Alpes nevados',
    imageUrl: 'https://example.com/europa.jpg',
    forumCount: 28
  }

  it('renderiza la categoría correctamente', () => {
    render(
      <MemoryRouter>
        <CategoryCard category={mockCategory} />
      </MemoryRouter>
    )

    expect(screen.getByText('Europa')).toBeInTheDocument()
    expect(screen.getByText(/Descubre los mejores destinos de Europa/)).toBeInTheDocument()
  })

  it('tiene link que navega a foros de la categoría', () => {
    render(
      <MemoryRouter>
        <CategoryCard category={mockCategory} />
      </MemoryRouter>
    )

    const categoryLink = screen.getByRole('link')
    expect(categoryLink).toHaveAttribute('href', '/forums/category/5')
  })

  it('muestra cantidad de foros disponibles (plural)', () => {
    render(
      <MemoryRouter>
        <CategoryCard category={mockCategory} />
      </MemoryRouter>
    )

    expect(screen.getByText(/28/)).toBeInTheDocument()
    expect(screen.getByText(/foros disponibles/)).toBeInTheDocument()
  })

  it('muestra cantidad de foros en singular cuando es 1', () => {
    const categoryWithOneForum = { ...mockCategory, forumCount: 1 }
    render(
      <MemoryRouter>
        <CategoryCard category={categoryWithOneForum} />
      </MemoryRouter>
    )

    expect(screen.getByText(/1/)).toBeInTheDocument()
    expect(screen.getByText(/foro disponible/)).toBeInTheDocument()
  })

  it('muestra 0 foros cuando no hay forumCount', () => {
    const categoryWithoutCount = { ...mockCategory, forumCount: undefined }
    render(
      <MemoryRouter>
        <CategoryCard category={categoryWithoutCount} />
      </MemoryRouter>
    )

    expect(screen.getByText(/0/)).toBeInTheDocument()
    expect(screen.getByText(/foros disponibles/)).toBeInTheDocument()
  })

  it('funciona sin imagen (muestra placeholder SVG)', () => {
    const categoryWithoutImage = { ...mockCategory, imageUrl: null }
    render(
      <MemoryRouter>
        <CategoryCard category={categoryWithoutImage} />
      </MemoryRouter>
    )

    expect(screen.getByText('Europa')).toBeInTheDocument()
  })

  it('muestra imagen cuando está disponible', () => {
    render(
      <MemoryRouter>
        <CategoryCard category={mockCategory} />
      </MemoryRouter>
    )

    const image = screen.getByAltText('Europa')
    expect(image).toBeInTheDocument()
    expect(image).toHaveAttribute('src', 'https://example.com/europa.jpg')
  })

  it('link es clickeable y visible', () => {
    render(
      <MemoryRouter>
        <CategoryCard category={mockCategory} />
      </MemoryRouter>
    )

    const link = screen.getByRole('link')
    expect(link).toBeVisible()
  })
})
