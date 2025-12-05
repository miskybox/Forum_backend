import { render, screen, fireEvent } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { vi } from 'vitest'
import Navbar from './Navbar'
import { AuthContext } from '../../contexts/AuthContext'

const mockNavigate = vi.fn()
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom')
  return {
    ...actual,
    useNavigate: () => mockNavigate
  }
})

describe('Navbar - Links y Botones', () => {
  const mockAuthContextGuest = {
    currentUser: null,
    isAuthenticated: false,
    logout: vi.fn()
  }

  const mockAuthContextUser = {
    currentUser: { username: 'testuser', email: 'test@example.com' },
    isAuthenticated: true,
    logout: vi.fn()
  }

  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('Links de navegación principal', () => {
    it('tiene link a Inicio con href correcto', () => {
      render(
        <MemoryRouter>
          <AuthContext.Provider value={mockAuthContextGuest}>
            <Navbar />
          </AuthContext.Provider>
        </MemoryRouter>
      )

      const homeLinks = screen.getAllByRole('link', { name: /inicio/i })
      homeLinks.forEach(link => {
        expect(link).toHaveAttribute('href', '/')
      })
    })

    it('tiene link a Continentes funcional', () => {
      render(
        <MemoryRouter>
          <AuthContext.Provider value={mockAuthContextGuest}>
            <Navbar />
          </AuthContext.Provider>
        </MemoryRouter>
      )

      const continentesLink = screen.getAllByRole('link', { name: /continentes/i })[0]
      expect(continentesLink).toBeInTheDocument()
      expect(continentesLink).toHaveAttribute('href', '/categories')
    })

    it('tiene link a Foros funcional', () => {
      render(
        <MemoryRouter>
          <AuthContext.Provider value={mockAuthContextGuest}>
            <Navbar />
          </AuthContext.Provider>
        </MemoryRouter>
      )

      const forosLink = screen.getAllByRole('link', { name: /^foros$/i })[0]
      expect(forosLink).toBeInTheDocument()
      expect(forosLink).toHaveAttribute('href', '/forums')
    })

    it('tiene link a Blog funcional', () => {
      render(
        <MemoryRouter>
          <AuthContext.Provider value={mockAuthContextGuest}>
            <Navbar />
          </AuthContext.Provider>
        </MemoryRouter>
      )

      const blogLink = screen.getAllByRole('link', { name: /blog/i })[0]
      expect(blogLink).toBeInTheDocument()
      expect(blogLink).toHaveAttribute('href', '/blog')
    })
  })

  describe('Links de autenticación (usuario no logueado)', () => {
    it('muestra botón Iniciar Sesión con aria-label', () => {
      render(
        <MemoryRouter>
          <AuthContext.Provider value={mockAuthContextGuest}>
            <Navbar />
          </AuthContext.Provider>
        </MemoryRouter>
      )

      const loginLink = screen.getByRole('link', { name: /iniciar sesi\u00f3n/i })
      expect(loginLink).toBeInTheDocument()
      expect(loginLink).toHaveAttribute('href', '/login')
      expect(loginLink).toHaveAttribute('aria-label', 'Iniciar sesión')
    })

    it('muestra botón Registrarse con aria-label', () => {
      render(
        <MemoryRouter>
          <AuthContext.Provider value={mockAuthContextGuest}>
            <Navbar />
          </AuthContext.Provider>
        </MemoryRouter>
      )

      const registerLinks = screen.getAllByRole('link', { name: /registrarse/i })
      registerLinks.forEach(link => {
        expect(link).toHaveAttribute('href', '/register')
        expect(link).toHaveAttribute('aria-label', 'Registrarse')
      })
    })
  })

  describe('Menú de usuario (usuario logueado)', () => {
    it('muestra botón de perfil cuando usuario está logueado', () => {
      render(
        <MemoryRouter>
          <AuthContext.Provider value={mockAuthContextUser}>
            <Navbar />
          </AuthContext.Provider>
        </MemoryRouter>
      )

      const userMenuButton = screen.getByRole('button', { name: /abrir men\u00fa de usuario/i })
      expect(userMenuButton).toBeInTheDocument()
      expect(userMenuButton).toHaveAttribute('aria-haspopup', 'true')
    })

    it('abre menú desplegable al hacer click en avatar', () => {
      render(
        <MemoryRouter>
          <AuthContext.Provider value={mockAuthContextUser}>
            <Navbar />
          </AuthContext.Provider>
        </MemoryRouter>
      )

      const userMenuButton = screen.getByRole('button', { name: /abrir men\u00fa de usuario/i })
      fireEvent.click(userMenuButton)

      expect(screen.getByRole('menuitem', { name: /tu perfil/i })).toBeInTheDocument()
      expect(screen.getByRole('menuitem', { name: /crear foro/i })).toBeInTheDocument()
      expect(screen.getByRole('menuitem', { name: /cerrar sesi\u00f3n/i })).toBeInTheDocument()
    })

    it('tiene link a Tu Perfil funcional', () => {
      render(
        <MemoryRouter>
          <AuthContext.Provider value={mockAuthContextUser}>
            <Navbar />
          </AuthContext.Provider>
        </MemoryRouter>
      )

      const userMenuButton = screen.getByRole('button', { name: /abrir men\u00fa de usuario/i })
      fireEvent.click(userMenuButton)

      const profileLink = screen.getByRole('menuitem', { name: /tu perfil/i })
      expect(profileLink).toHaveAttribute('href', '/profile')
    })

    it('tiene link a Crear Foro funcional', () => {
      render(
        <MemoryRouter>
          <AuthContext.Provider value={mockAuthContextUser}>
            <Navbar />
          </AuthContext.Provider>
        </MemoryRouter>
      )

      const userMenuButton = screen.getByRole('button', { name: /abrir men\u00fa de usuario/i })
      fireEvent.click(userMenuButton)

      const createForumLink = screen.getByRole('menuitem', { name: /crear foro/i })
      expect(createForumLink).toHaveAttribute('href', '/forums/create')
    })

    it('botón Cerrar Sesión llama a la función logout', async () => {
      const mockLogout = vi.fn()
      const contextWithLogout = {
        ...mockAuthContextUser,
        logout: mockLogout
      }

      render(
        <MemoryRouter>
          <AuthContext.Provider value={contextWithLogout}>
            <Navbar />
          </AuthContext.Provider>
        </MemoryRouter>
      )

      const userMenuButton = screen.getByRole('button', { name: /abrir men\u00fa de usuario/i })
      fireEvent.click(userMenuButton)

      const logoutButton = screen.getByRole('menuitem', { name: /cerrar sesi\u00f3n/i })
      fireEvent.click(logoutButton)

      expect(mockLogout).toHaveBeenCalled()
    })
  })

  describe('Menú móvil', () => {
    it('abre menú móvil al hacer click en botón hamburguesa', () => {
      render(
        <MemoryRouter>
          <AuthContext.Provider value={mockAuthContextGuest}>
            <Navbar />
          </AuthContext.Provider>
        </MemoryRouter>
      )

      const mobileMenuButton = screen.getByRole('button', { name: /abrir men\u00fa principal/i })
      fireEvent.click(mobileMenuButton)

      expect(screen.getByRole('navigation')).toBeInTheDocument()
    })
  })

  describe('Accesibilidad', () => {
    it('navegación tiene role="navigation"', () => {
      render(
        <MemoryRouter>
          <AuthContext.Provider value={mockAuthContextGuest}>
            <Navbar />
          </AuthContext.Provider>
        </MemoryRouter>
      )

      const nav = screen.getByRole('navigation')
      expect(nav).toBeInTheDocument()
    })

    it('todos los links principales tienen atributos de accesibilidad', () => {
      render(
        <MemoryRouter>
          <AuthContext.Provider value={mockAuthContextGuest}>
            <Navbar />
          </AuthContext.Provider>
        </MemoryRouter>
      )

      const links = [
        screen.getAllByRole('link', { name: /inicio/i })[0],
        screen.getAllByRole('link', { name: /continentes/i })[0],
        screen.getAllByRole('link', { name: /^foros$/i })[0],
        screen.getAllByRole('link', { name: /blog/i })[0]
      ]

      links.forEach(link => {
        expect(link).toBeVisible()
        expect(link).toHaveAttribute('href')
      })
    })
  })
})
