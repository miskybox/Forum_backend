// Archivo: src/pages/RegisterPage.jsx
import { useEffect } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import RegisterForm from '../components/auth/RegisterForm'
import useAuth from '../hooks/useAuth'

const RegisterPage = () => {
  const { isAuthenticated } = useAuth()
  const navigate = useNavigate()
  
  // Redirigir al usuario a la página principal si ya está autenticado
  useEffect(() => {
    if (isAuthenticated) {
      navigate('/')
    }
  }, [isAuthenticated, navigate])
  
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
              Únete a ForumViajeros
            </h1>
            <p className="mt-2 text-sm text-neutral-600">
              Crea tu cuenta para compartir experiencias y conectar con viajeros de todo el mundo.
            </p>
          </div>
          
          <RegisterForm />
          
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

export default RegisterPage