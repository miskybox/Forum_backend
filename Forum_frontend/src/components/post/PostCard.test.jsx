import { render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import PostCard from './PostCard'

describe('PostCard - Links funcionales', () => {
  const mockPost = {
    id: 42,
    title: 'Mi experiencia en Tokio',
    content: 'Tokio es una ciudad increíble con una mezcla perfecta de tradición y modernidad. Los templos antiguos conviven con rascacielos futuristas.',
    createdAt: '2024-01-20T14:30:00Z',
    author: {
      username: 'explorador99',
      profileImage: null
    },
    images: [
      { id: 1, url: 'https://example.com/tokio1.jpg' },
      { id: 2, url: 'https://example.com/tokio2.jpg' }
    ],
    commentCount: 12,
    viewCount: 145
  }

  it('renderiza el post correctamente', () => {
    render(
      <MemoryRouter>
        <PostCard post={mockPost} />
      </MemoryRouter>
    )

    expect(screen.getByText('Mi experiencia en Tokio')).toBeInTheDocument()
    expect(screen.getByText(/Tokio es una ciudad increíble/)).toBeInTheDocument()
  })

  it('tiene link que navega a detalles del post', () => {
    render(
      <MemoryRouter>
        <PostCard post={mockPost} />
      </MemoryRouter>
    )

    const postLink = screen.getByRole('link')
    expect(postLink).toHaveAttribute('href', '/posts/42')
  })

  it('muestra información del autor', () => {
    render(
      <MemoryRouter>
        <PostCard post={mockPost} />
      </MemoryRouter>
    )

    expect(screen.getByText('explorador99')).toBeInTheDocument()
  })

  it('muestra estadísticas del post', () => {
    render(
      <MemoryRouter>
        <PostCard post={mockPost} />
      </MemoryRouter>
    )

    expect(screen.getByText(/12/)).toBeInTheDocument()
    expect(screen.getByText(/comentarios/)).toBeInTheDocument()
    expect(screen.getByText(/145/)).toBeInTheDocument()
    expect(screen.getByText(/vistas/)).toBeInTheDocument()
  })

  it('muestra botón "Leer más"', () => {
    render(
      <MemoryRouter>
        <PostCard post={mockPost} />
      </MemoryRouter>
    )

    expect(screen.getByText('Leer más')).toBeInTheDocument()
  })

  it('muestra imágenes del post', () => {
    render(
      <MemoryRouter>
        <PostCard post={mockPost} />
      </MemoryRouter>
    )

    const images = screen.getAllByRole('img')
    expect(images.length).toBe(2)
  })

  it('funciona sin imágenes', () => {
    const postWithoutImages = { ...mockPost, images: [] }
    render(
      <MemoryRouter>
        <PostCard post={postWithoutImages} />
      </MemoryRouter>
    )

    expect(screen.getByText('Mi experiencia en Tokio')).toBeInTheDocument()
    expect(screen.getByText('Leer más')).toBeInTheDocument()
  })

  it('trunca contenido largo correctamente', () => {
    const longContent = 'A'.repeat(200)
    const postWithLongContent = { ...mockPost, content: longContent }

    render(
      <MemoryRouter>
        <PostCard post={postWithLongContent} />
      </MemoryRouter>
    )

    expect(screen.getByText(/\.\.\./)).toBeInTheDocument()
  })
})
