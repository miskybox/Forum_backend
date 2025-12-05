import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import useAuth from '../../hooks/useAuth'
import { toast } from 'react-hot-toast'

const LoginForm = () => {
  const [formData, setFormData] = useState({ username: '', password: '' })
  const [errors, setErrors] = useState({})
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [showPassword, setShowPassword] = useState(false)

  const { login } = useAuth()
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
    } else if (/[^a-zA-Z0-9._-]/.test(formData.username)) {
      newErrors.username = 'El nombre de usuario contiene caracteres inválidos'
    }

    if (!formData.password) {
      newErrors.password = 'La contraseña es obligatoria'
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!validateForm()) return

    setIsSubmitting(true)

    try {
      await login(formData)
      toast.success('¡Has iniciado sesión con éxito!')
      navigate('/')
    } catch (error) {
      const status = error.response?.status
      const message = error.response?.data?.message || 'Error al iniciar sesión'

      if (status === 401) {
        setErrors({ auth: 'Usuario o contraseña incorrectos' })
      } else {
        toast.error(message)
      }
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <div className="bg-white p-6 rounded-lg shadow-md max-w-md w-full mx-auto">
      <h2 className="text-2xl font-bold text-center text-primary-700 mb-6">Iniciar Sesión</h2>

      <form onSubmit={handleSubmit} className="space-y-4">
        {errors.auth && (
          <div className="p-3 rounded bg-red-50 text-red-700 text-sm">
            {errors.auth}
          </div>
        )}

        <div>
          <label htmlFor="username" className="block text-sm font-medium text-neutral-700 mb-1">
            Nombre de usuario
          </label>
          <input
            id="username"
            name="username"
            type="text"
            autoComplete="username"
            required
            className={`input w-full ${errors.username ? 'border-red-500 focus:ring-red-500' : ''}`}
            value={formData.username}
            onChange={handleChange}
            disabled={isSubmitting}
          />
          {errors.username && (
            <p className="mt-1 text-sm text-red-600">{errors.username}</p>
          )}
        </div>

        <div className="relative">
          <label htmlFor="password" className="block text-sm font-medium text-neutral-700 mb-1">
            Contraseña
          </label>
          <input
            id="password"
            name="password"
            type={showPassword ? 'text' : 'password'}
            autoComplete="current-password"
            required
            className={`input w-full pr-10 ${errors.password ? 'border-red-500 focus:ring-red-500' : ''}`}
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
          {errors.password && (
            <p className="mt-1 text-sm text-red-600">{errors.password}</p>
          )}
        </div>

        <button
          type="submit"
          className="btn btn-primary w-full"
          disabled={isSubmitting}
        >
          {isSubmitting ? 'Iniciando...' : 'Iniciar Sesión'}
        </button>
      </form>

      <div className="mt-6 text-center">
        <p className="text-sm text-neutral-600">
          ¿No tienes cuenta?{' '}
          <Link to="/register" className="font-medium text-primary-600 hover:text-primary-500">
            Regístrate
          </Link>
        </p>
      </div>
    </div>
  )
}

export default LoginForm
