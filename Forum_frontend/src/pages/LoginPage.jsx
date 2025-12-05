// Archivo: src/pages/LoginPage.jsx
import { useEffect } from 'react'
import { Link, useNavigate, useLocation } from 'react-router-dom'
import LoginForm from '../components/auth/LoginForm'
import useAuth from '../hooks/useAuth'

const LoginPage = () => {
  const { isAuthenticated } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  
  // Redirigir al usuario a la página principal o a la página de donde vino si ya está autenticado
  useEffect(() => {
    if (isAuthenticated) {
      const from = location.state?.from?.pathname || '/'
      navigate(from, { replace: true })
    }
  }, [isAuthenticated, navigate, location])
  
  return (
    <div className="min-h-screen bg-neutral-50 py-12 sm:py-16 lg:py-24">
      <div className="container px-4 sm:px-6 lg:px-8 mx-auto">
        <div className="max-w-md mx-auto">
          <div className="text-center mb-8">
            <Link to="/" className="inline-block">
              <img
                className="h-12 w-auto"
                src="/src/assets/logoFV.png"
                alt="ForumViajeros"
              />
            </Link>
            <h1 className="mt-6 text-3xl font-bold text-neutral-900">
              Bienvenido de nuevo
            </h1>
            <p className="mt-2 text-sm text-neutral-600">
              Inicia sesión para acceder a tu cuenta y compartir tus experiencias de viaje.
            </p>
          </div>
          
          <LoginForm />
          
          <div className="mt-8 text-center">
            <Link 
              to="/"
              className="text-sm font-medium text-neutral-600 hover:text-primary-600"
            >
              ← Volver a la página principal
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}

export default LoginPage