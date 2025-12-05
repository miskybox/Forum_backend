import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter } from 'react-router-dom'
import { vi } from 'vitest'
import LoginForm from './LoginForm'
import { AuthContext } from '../../contexts/AuthContext'

vi.mock('react-hot-toast', () => ({
  default: {
    success: vi.fn(),
    error: vi.fn()
  },
  toast: {
    success: vi.fn(),
    error: vi.fn()
  }
}))

const mockNavigate = vi.fn()
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom')
  return {
    ...actual,
    useNavigate: () => mockNavigate
  }
})

describe('LoginForm', () => {
  const mockLogin = vi.fn()
  const mockAuthContext = {
    login: mockLogin,
    user: null,
    loading: false,
    logout: vi.fn()
  }

  beforeEach(() => {
    vi.clearAllMocks()
  })

  const renderLoginForm = () => {
    return render(
      <MemoryRouter>
        <AuthContext.Provider value={mockAuthContext}>
          <LoginForm />
        </AuthContext.Provider>
      </MemoryRouter>
    )
  }

  it('renderiza el formulario correctamente', () => {
    renderLoginForm()

    expect(screen.getByLabelText(/nombre de usuario/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/contraseña/i)).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /iniciar sesión/i })).toBeInTheDocument()
  })

  it('permite introducir texto en los campos', async () => {
    const user = userEvent.setup()
    renderLoginForm()

    const usernameInput = screen.getByLabelText(/nombre de usuario/i)
    const passwordInput = screen.getByLabelText(/contraseña/i)

    await user.type(usernameInput, 'testuser')
    await user.type(passwordInput, 'password123')

    expect(usernameInput).toHaveValue('testuser')
    expect(passwordInput).toHaveValue('password123')
  })

  it('llama a login al enviar el formulario', async () => {
    const user = userEvent.setup()
    mockLogin.mockResolvedValueOnce({ success: true })
    renderLoginForm()

    await user.type(screen.getByLabelText(/nombre de usuario/i), 'testuser')
    await user.type(screen.getByLabelText(/contraseña/i), 'password123')
    await user.click(screen.getByRole('button', { name: /iniciar sesión/i }))

    await waitFor(() => {
      expect(mockLogin).toHaveBeenCalled()
    })
  })

  it('tiene un enlace para registrarse', () => {
    renderLoginForm()

    const registerLink = screen.getByRole('link', { name: /regístrate/i })
    expect(registerLink).toBeInTheDocument()
    expect(registerLink).toHaveAttribute('href', '/register')
  })

  it('valida que el nombre de usuario es obligatorio', async () => {
    const user = userEvent.setup()
    renderLoginForm()

    await user.type(screen.getByLabelText(/contraseña/i), 'password123')
    await user.click(screen.getByRole('button', { name: /iniciar sesión/i }))

    await waitFor(() => {
      expect(screen.getByText(/nombre de usuario es obligatorio/i)).toBeInTheDocument()
    })
    expect(mockLogin).not.toHaveBeenCalled()
  })

  it('valida que la contraseña es obligatoria', async () => {
    const user = userEvent.setup()
    renderLoginForm()

    await user.type(screen.getByLabelText(/nombre de usuario/i), 'testuser')
    await user.click(screen.getByRole('button', { name: /iniciar sesión/i }))

    await waitFor(() => {
      expect(screen.getByText(/contraseña es obligatoria/i)).toBeInTheDocument()
    })
    expect(mockLogin).not.toHaveBeenCalled()
  })

  it('valida caracteres inválidos en nombre de usuario', async () => {
    const user = userEvent.setup()
    renderLoginForm()

    await user.type(screen.getByLabelText(/nombre de usuario/i), 'user@invalid')
    await user.type(screen.getByLabelText(/contraseña/i), 'password123')
    await user.click(screen.getByRole('button', { name: /iniciar sesión/i }))

    await waitFor(() => {
      expect(screen.getByText(/caracteres inválidos/i)).toBeInTheDocument()
    })
    expect(mockLogin).not.toHaveBeenCalled()
  })

  it('muestra error cuando las credenciales son incorrectas', async () => {
    const user = userEvent.setup()
    mockLogin.mockRejectedValueOnce({
      response: {
        status: 401,
        data: { message: 'Credenciales inválidas' }
      }
    })
    renderLoginForm()

    await user.type(screen.getByLabelText(/nombre de usuario/i), 'wronguser')
    await user.type(screen.getByLabelText(/contraseña/i), 'wrongpass')
    await user.click(screen.getByRole('button', { name: /iniciar sesión/i }))

    await waitFor(() => {
      expect(screen.getByText(/incorrecto|inválido/i)).toBeInTheDocument()
    })
  })

  it('navega a home después de login exitoso', async () => {
    const user = userEvent.setup()
    mockLogin.mockResolvedValueOnce({ success: true })
    renderLoginForm()

    await user.type(screen.getByLabelText(/nombre de usuario/i), 'testuser')
    await user.type(screen.getByLabelText(/contraseña/i), 'password123')
    await user.click(screen.getByRole('button', { name: /iniciar sesión/i }))

    await waitFor(() => {
      expect(mockLogin).toHaveBeenCalledWith({
        username: 'testuser',
        password: 'password123'
      })
      expect(mockNavigate).toHaveBeenCalledWith('/')
    })
  })

  it('deshabilita el botón durante el envío', async () => {
    const user = userEvent.setup()
    mockLogin.mockImplementation(() => new Promise(resolve => setTimeout(resolve, 100)))
    renderLoginForm()

    await user.type(screen.getByLabelText(/nombre de usuario/i), 'testuser')
    await user.type(screen.getByLabelText(/contraseña/i), 'password123')

    const submitButton = screen.getByRole('button', { name: /iniciar sesión/i })
    await user.click(submitButton)

    expect(submitButton).toBeDisabled()
    expect(submitButton).toHaveTextContent(/iniciando/i)
  })

  it('limpia errores cuando el usuario empieza a escribir', async () => {
    const user = userEvent.setup()
    renderLoginForm()

    // Primero generar un error
    await user.click(screen.getByRole('button', { name: /iniciar sesión/i }))
    await waitFor(() => {
      expect(screen.getByText(/obligatorio/i)).toBeInTheDocument()
    })

    // Luego escribir en el campo
    await user.type(screen.getByLabelText(/nombre de usuario/i), 'test')
    
    await waitFor(() => {
      expect(screen.queryByText(/nombre de usuario es obligatorio/i)).not.toBeInTheDocument()
    })
  })

  it('permite mostrar/ocultar contraseña', async () => {
    const user = userEvent.setup()
    renderLoginForm()

    const passwordInput = screen.getByLabelText(/contraseña/i)
    await user.type(passwordInput, 'password123')

    // Buscar botón de mostrar contraseña
    const toggleButton = screen.getByRole('button', { name: /👁️|🙈/i })
    expect(toggleButton).toBeInTheDocument()

    // Verificar que inicialmente es password
    expect(passwordInput).toHaveAttribute('type', 'password')

    // Hacer clic para mostrar
    await user.click(toggleButton)
    expect(passwordInput).toHaveAttribute('type', 'text')

    // Hacer clic para ocultar
    await user.click(toggleButton)
    expect(passwordInput).toHaveAttribute('type', 'password')
  })
})
