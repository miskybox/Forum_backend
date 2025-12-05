import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter } from 'react-router-dom'
import { vi } from 'vitest'
import RegisterForm from './RegisterForm'
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

describe('RegisterForm', () => {
  const mockRegister = vi.fn()
  const mockLogin = vi.fn()
  const mockAuthContext = {
    register: mockRegister,
    login: mockLogin,
    user: null,
    loading: false,
    logout: vi.fn()
  }

  beforeEach(() => {
    vi.clearAllMocks()
  })

  const renderRegisterForm = () => {
    return render(
      <MemoryRouter>
        <AuthContext.Provider value={mockAuthContext}>
          <RegisterForm />
        </AuthContext.Provider>
      </MemoryRouter>
    )
  }

  it('renderiza el formulario correctamente', () => {
    renderRegisterForm()

    expect(screen.getByLabelText(/usuario/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/correo electrónico/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/^contraseña$/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/confirmar contraseña/i)).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /registrarse/i })).toBeInTheDocument()
  })

  it('muestra error cuando las contraseñas no coinciden', async () => {
    const user = userEvent.setup()
    renderRegisterForm()

    await user.type(screen.getByLabelText(/usuario/i), 'testuser')
    await user.type(screen.getByLabelText(/correo/i), 'test@test.com')
    await user.type(screen.getByLabelText(/^nombre$/i), 'Test')
    await user.type(screen.getByLabelText(/apellido/i), 'User')
    await user.type(screen.getByLabelText(/^contraseña$/i), 'password123')
    await user.type(screen.getByLabelText(/confirmar/i), 'different')
    await user.click(screen.getByRole('button', { name: /registrarse/i }))

    await waitFor(() => {
      expect(screen.getByText(/las contraseñas no coinciden/i)).toBeInTheDocument()
    })
  })

  it('registra usuario exitosamente', async () => {
    const user = userEvent.setup()
    mockRegister.mockResolvedValueOnce({ success: true })
    mockLogin.mockResolvedValueOnce({ success: true })
    renderRegisterForm()

    await user.type(screen.getByLabelText(/usuario/i), 'newuser')
    await user.type(screen.getByLabelText(/correo/i), 'new@test.com')
    await user.type(screen.getByLabelText(/^nombre$/i), 'New')
    await user.type(screen.getByLabelText(/apellido/i), 'User')
    await user.type(screen.getByLabelText(/^contraseña$/i), 'password123')
    await user.type(screen.getByLabelText(/confirmar/i), 'password123')
    await user.click(screen.getByRole('button', { name: /registrarse/i }))

    await waitFor(() => {
      expect(mockRegister).toHaveBeenCalled()
    })
  })

  it('valida que el nombre de usuario es obligatorio', async () => {
    const user = userEvent.setup()
    renderRegisterForm()

    await user.type(screen.getByLabelText(/correo/i), 'test@test.com')
    await user.type(screen.getByLabelText(/^nombre$/i), 'Test')
    await user.type(screen.getByLabelText(/apellido/i), 'User')
    await user.type(screen.getByLabelText(/^contraseña$/i), 'password123')
    await user.type(screen.getByLabelText(/confirmar/i), 'password123')
    await user.click(screen.getByRole('button', { name: /registrarse/i }))

    await waitFor(() => {
      expect(screen.getByText(/obligatorio/i)).toBeInTheDocument()
    })
    expect(mockRegister).not.toHaveBeenCalled()
  })

  it('valida formato de email', async () => {
    const user = userEvent.setup()
    renderRegisterForm()

    await user.type(screen.getByLabelText(/usuario/i), 'testuser')
    await user.type(screen.getByLabelText(/correo/i), 'email-invalido')
    await user.type(screen.getByLabelText(/^nombre$/i), 'Test')
    await user.type(screen.getByLabelText(/apellido/i), 'User')
    await user.type(screen.getByLabelText(/^contraseña$/i), 'password123')
    await user.type(screen.getByLabelText(/confirmar/i), 'password123')
    await user.click(screen.getByRole('button', { name: /registrarse/i }))

    await waitFor(() => {
      expect(screen.getByText(/no válido|inválido/i)).toBeInTheDocument()
    })
    expect(mockRegister).not.toHaveBeenCalled()
  })

  it('valida longitud mínima de contraseña', async () => {
    const user = userEvent.setup()
    renderRegisterForm()

    await user.type(screen.getByLabelText(/usuario/i), 'testuser')
    await user.type(screen.getByLabelText(/correo/i), 'test@test.com')
    await user.type(screen.getByLabelText(/^nombre$/i), 'Test')
    await user.type(screen.getByLabelText(/apellido/i), 'User')
    await user.type(screen.getByLabelText(/^contraseña$/i), '123')
    await user.type(screen.getByLabelText(/confirmar/i), '123')
    await user.click(screen.getByRole('button', { name: /registrarse/i }))

    await waitFor(() => {
      expect(screen.getByText(/mínimo 6 caracteres|Debe tener mínimo/i)).toBeInTheDocument()
    })
    expect(mockRegister).not.toHaveBeenCalled()
  })

  it('valida que nombre y apellido son obligatorios', async () => {
    const user = userEvent.setup()
    renderRegisterForm()

    await user.type(screen.getByLabelText(/usuario/i), 'testuser')
    await user.type(screen.getByLabelText(/correo/i), 'test@test.com')
    await user.type(screen.getByLabelText(/^contraseña$/i), 'password123')
    await user.type(screen.getByLabelText(/confirmar/i), 'password123')
    await user.click(screen.getByRole('button', { name: /registrarse/i }))

    await waitFor(() => {
      const errorMessages = screen.getAllByText(/obligatorio/i)
      expect(errorMessages.length).toBeGreaterThan(0)
    })
    expect(mockRegister).not.toHaveBeenCalled()
  })

  it('muestra error cuando el registro falla', async () => {
    const user = userEvent.setup()
    const errorMessage = 'El nombre de usuario ya está en uso'
    mockRegister.mockRejectedValueOnce({
      response: {
        status: 409,
        data: {
          message: errorMessage,
          errors: { username: errorMessage }
        }
      }
    })
    renderRegisterForm()

    await user.type(screen.getByLabelText(/usuario/i), 'existinguser')
    await user.type(screen.getByLabelText(/correo/i), 'test@test.com')
    await user.type(screen.getByLabelText(/^nombre$/i), 'Test')
    await user.type(screen.getByLabelText(/apellido/i), 'User')
    await user.type(screen.getByLabelText(/^contraseña$/i), 'password123')
    await user.type(screen.getByLabelText(/confirmar/i), 'password123')
    await user.click(screen.getByRole('button', { name: /registrarse/i }))

    await waitFor(() => {
      expect(screen.getByText(errorMessage)).toBeInTheDocument()
    })
  })

  it('deshabilita el botón durante el envío', async () => {
    const user = userEvent.setup()
    mockRegister.mockImplementation(() => new Promise(resolve => setTimeout(resolve, 100)))
    renderRegisterForm()

    await user.type(screen.getByLabelText(/usuario/i), 'testuser')
    await user.type(screen.getByLabelText(/correo/i), 'test@test.com')
    await user.type(screen.getByLabelText(/^nombre$/i), 'Test')
    await user.type(screen.getByLabelText(/apellido/i), 'User')
    await user.type(screen.getByLabelText(/^contraseña$/i), 'password123')
    await user.type(screen.getByLabelText(/confirmar/i), 'password123')

    const submitButton = screen.getByRole('button', { name: /registrarse/i })
    await user.click(submitButton)

    expect(submitButton).toBeDisabled()
  })

  it('tiene todos los campos requeridos', () => {
    renderRegisterForm()
    // Verificar que todos los campos están presentes
    expect(screen.getByLabelText(/usuario/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/correo/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/^nombre$/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/apellido/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/^contraseña$/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/confirmar/i)).toBeInTheDocument()
  })
})
