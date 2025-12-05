import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import useAuth from '../../hooks/useAuth'
import { toast } from 'react-hot-toast'

const RegisterForm = () => {
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
    firstName: '',
    lastName: ''
  })
  const [errors, setErrors] = useState({})
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [showPassword, setShowPassword] = useState(false)
  const [showConfirmPassword, setShowConfirmPassword] = useState(false)

  const { register, login } = useAuth()
  const navigate = useNavigate()

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData({ ...formData, [name]: value })

    if (errors[name]) {
      setErrors({ ...errors, [name]: '' })
    }
  }

  const validateForm = () => {
    const newErrors = {}

    if (!formData.username.trim()) {
      newErrors.username = 'El nombre de usuario es obligatorio'
    } else if (formData.username.length < 3) {
      newErrors.username = 'Debe tener al menos 3 caracteres'
    } else if (/[^a-zA-Z0-9._-]/.test(formData.username)) {
      newErrors.username = 'Contiene caracteres no válidos'
    }

    if (!formData.email.trim()) {
      newErrors.email = 'El correo es obligatorio'
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = 'Correo no válido'
    }

    if (!formData.password) {
      newErrors.password = 'La contraseña es obligatoria'
    } else if (formData.password.length < 6) {
      newErrors.password = 'Debe tener mínimo 6 caracteres'
    }

    if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = 'Las contraseñas no coinciden'
    }

    if (!formData.firstName.trim()) {
      newErrors.firstName = 'El nombre es obligatorio'
    }

    if (!formData.lastName.trim()) {
      newErrors.lastName = 'El apellido es obligatorio'
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!validateForm()) return

    setIsSubmitting(true)

    const userData = {
      username: formData.username,
      email: formData.email,
      password: formData.password,
      firstName: formData.firstName,
      lastName: formData.lastName
    }

    try {

      const registerResponse = await register(userData)
      console.log('Registro exitoso:', registerResponse)

      try {
        await login({ username: formData.username, password: formData.password })
        toast.success('¡Registro exitoso! Bienvenido/a ' + formData.username)
        navigate('/')
      } catch (loginError) {
   
        console.error('Error en inicio de sesión automático:', loginError)
        toast.success('¡Registro exitoso! Por favor, inicia sesión')
        navigate('/login')
      }
    } catch (error) {
      const status = error.response?.status
      const backendErrors = error.response?.data?.errors || {}
      const formattedErrors = {}

      if (status === 409 || backendErrors.username) {
        formattedErrors.username = backendErrors.username || 'Usuario ya registrado'
      }
      if (backendErrors.email) {
        formattedErrors.email = 'Este email ya está en uso'
      }

      setErrors({ ...errors, ...formattedErrors })
      toast.error(error.response?.data?.message || 'Error al registrar')
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <div className="bg-white p-6 rounded-lg shadow-md max-w-md w-full mx-auto">
      <h2 className="text-2xl font-bold text-center text-primary-700 mb-6">Crear una cuenta</h2>

      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label htmlFor="firstName">Nombre</label>
            <input
              id="firstName"
              name="firstName"
              type="text"
              required
              className={`input w-full ${errors.firstName && 'border-red-500'}`}
              value={formData.firstName}
              onChange={handleChange}
              disabled={isSubmitting}
            />
            {errors.firstName && <p className="text-red-600 text-sm">{errors.firstName}</p>}
          </div>

          <div>
            <label htmlFor="lastName">Apellido</label>
            <input
              id="lastName"
              name="lastName"
              type="text"
              required
              className={`input w-full ${errors.lastName && 'border-red-500'}`}
              value={formData.lastName}
              onChange={handleChange}
              disabled={isSubmitting}
            />
            {errors.lastName && <p className="text-red-600 text-sm">{errors.lastName}</p>}
          </div>
        </div>

        <div>
          <label htmlFor="username">Usuario</label>
          <input
            id="username"
            name="username"
            type="text"
            autoComplete="username"
            required
            className={`input w-full ${errors.username && 'border-red-500'}`}
            value={formData.username}
            onChange={handleChange}
            disabled={isSubmitting}
          />
          {errors.username && <p className="text-red-600 text-sm">{errors.username}</p>}
        </div>

        <div>
          <label htmlFor="email">Correo electrónico</label>
          <input
            id="email"
            name="email"
            type="email"
            required
            className={`input w-full ${errors.email && 'border-red-500'}`}
            value={formData.email}
            onChange={handleChange}
            disabled={isSubmitting}
          />
          {errors.email && <p className="text-red-600 text-sm">{errors.email}</p>}
        </div>

        <div className="relative">
          <label htmlFor="password">Contraseña</label>
          <input
            id="password"
            name="password"
            type={showPassword ? 'text' : 'password'}
            required
            className={`input w-full pr-10 ${errors.password && 'border-red-500'}`}
            value={formData.password}
            onChange={handleChange}
            disabled={isSubmitting}
          />
          <button
            type="button"
            onClick={() => setShowPassword(!showPassword)}
            className="absolute top-9 right-3 text-sm text-neutral-600"
          >
            {showPassword ? '🙈' : '👁️'}
          </button>
          {errors.password && <p className="text-red-600 text-sm">{errors.password}</p>}
        </div>

        <div className="relative">
          <label htmlFor="confirmPassword">Confirmar contraseña</label>
          <input
            id="confirmPassword"
            name="confirmPassword"
            type={showConfirmPassword ? 'text' : 'password'}
            required
            className={`input w-full pr-10 ${errors.confirmPassword && 'border-red-500'}`}
            value={formData.confirmPassword}
            onChange={handleChange}
            disabled={isSubmitting}
          />
          <button
            type="button"
            onClick={() => setShowConfirmPassword(!showConfirmPassword)}
            className="absolute top-9 right-3 text-sm text-neutral-600"
          >
            {showConfirmPassword ? '🙈' : '👁️'}
          </button>
          {errors.confirmPassword && <p className="text-red-600 text-sm">{errors.confirmPassword}</p>}
        </div>

        <button
          type="submit"
          className="btn btn-primary w-full"
          disabled={isSubmitting}
        >
          {isSubmitting ? 'Registrando...' : 'Registrarse'}
        </button>
      </form>
    </div>
  )
}

export default RegisterForm