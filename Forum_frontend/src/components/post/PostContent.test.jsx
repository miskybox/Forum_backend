import React from 'react'
import { render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import PostContent from './PostContent'

describe('PostContent', () => {
  it('renderiza título y contenido', () => {
    const post = { title: 'Mi viaje', content: 'Texto\n\nMás texto', forumId: 1 }
    render(
      <MemoryRouter>
        <PostContent post={post} />
      </MemoryRouter>
    )
    expect(screen.getByText('Mi viaje')).toBeInTheDocument()
    expect(screen.getByText('Texto')).toBeInTheDocument()
    expect(screen.getByText('Más texto')).toBeInTheDocument()
  })

  it('renderiza imágenes cuando existen', () => {
    const post = { title: 'Con fotos', content: 'Contenido', forumId: 1, imagePaths: ['/api/images/a.jpg', '/api/images/b.jpg'] }
    render(
      <MemoryRouter>
        <PostContent post={post} />
      </MemoryRouter>
    )
    const imgs = screen.getAllByRole('img')
    expect(imgs.length).toBe(2)
  })
})