import { render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import ForumCard from './ForumCard'

describe('ForumCard - Links funcionales', () => {
  const mockForum = {
    id: 1,
    title: 'Viaje a París',
    description: 'Consejos para viajar a la ciudad de la luz',
    imageUrl: 'https://example.com/paris.jpg',
    postCount: 15,
    viewCount: 230,
    creator: {
      username: 'viajero123',
      profileImage: null
    },
    createdAt: '2024-01-15T10:00:00Z',
    tags: [
      { id: 1, name: 'Europa' },
      { id: 2, name: 'Francia' }
    ]
  }

  it('renderiza el componente correctamente', () => {
    render(
      <MemoryRouter>
        <ForumCard forum={mockForum} />
      </MemoryRouter>
    )

    expect(screen.getByText('Viaje a París')).toBeInTheDocument()
    expect(screen.getByText('Consejos para viajar a la ciudad de la luz')).toBeInTheDocument()
  })

  it('tiene links que navegan a detalles del foro', () => {
    render(
      <MemoryRouter>
        <ForumCard forum={mockForum} />
      </MemoryRouter>
    )

    const links = screen.getAllByRole('link', { name: 'Viaje a París' })
    expect(links.length).toBeGreaterThan(0)
    links.forEach(link => {
      expect(link).toHaveAttribute('href', '/forums/1')
    })
  })

  it('muestra información del creador', () => {
    render(
      <MemoryRouter>
        <ForumCard forum={mockForum} />
      </MemoryRouter>
    )

    expect(screen.getByText('viajero123')).toBeInTheDocument()
  })

  it('muestra estadísticas del foro', () => {
    render(
      <MemoryRouter>
        <ForumCard forum={mockForum} />
      </MemoryRouter>
    )

    expect(screen.getByText(/15/)).toBeInTheDocument()
    expect(screen.getByText(/publicaciones/)).toBeInTheDocument()
    expect(screen.getByText(/230/)).toBeInTheDocument()
    expect(screen.getByText(/vistas/)).toBeInTheDocument()
  })

  it('muestra los tags del foro', () => {
    render(
      <MemoryRouter>
        <ForumCard forum={mockForum} />
      </MemoryRouter>
    )

    expect(screen.getByText('Europa')).toBeInTheDocument()
    expect(screen.getByText('Francia')).toBeInTheDocument()
  })

  it('funciona sin imagen (muestra placeholder)', () => {
    const forumWithoutImage = { ...mockForum, imageUrl: null }
    render(
      <MemoryRouter>
        <ForumCard forum={forumWithoutImage} />
      </MemoryRouter>
    )

    expect(screen.getByText('Viaje a París')).toBeInTheDocument()
  })
})
