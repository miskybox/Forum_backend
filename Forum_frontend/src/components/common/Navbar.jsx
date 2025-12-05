// Archivo: src/components/common/Navbar.jsx (actualizado)
import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import useAuth from '../../hooks/useAuth'
import logo from '../../assets/logo2.png'; 

const Navbar = () => {
  const { currentUser, isAuthenticated, logout } = useAuth()
  const navigate = useNavigate()
  const [isMenuOpen, setIsMenuOpen] = useState(false)
  const [isProfileMenuOpen, setIsProfileMenuOpen] = useState(false)

  const handleLogout = async () => {
    await logout()
    navigate('/')
  }

  return (
    <nav className="bg-white shadow-sm border-b border-gray-200 sticky top-0 z-50">
      <div className="container mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          {/* Logo y navegación principal */}
          <div className="flex items-center">
            <Link to="/" className="flex-shrink-0 flex items-center">
              <img
                className="h-12 w-auto mr-2"
                src={logo} // Usamos la variable importada
                alt="ForumViajeros"
              />
              <div className="flex flex-col">
                <span className="text-xl font-bold text-green-700">ForumViajeros</span>
                <span className="text-xs text-amber-700">CONEXIÓN GLOBAL</span>
              </div>
            </Link>
            
            {/* Menú navegación en desktop */}
            <div className="hidden sm:ml-6 sm:flex sm:space-x-8">
              <Link
                to="/"
                className="border-transparent text-gray-700 hover:border-green-500 hover:text-green-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium"
              >
                Inicio
              </Link>
              <Link
                to="/categories"
                className="border-transparent text-gray-700 hover:border-green-500 hover:text-green-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium"
              >
                Continentes
              </Link>
              <Link
                to="/forums"
                className="border-transparent text-gray-700 hover:border-green-500 hover:text-green-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium"
              >
                Foros
              </Link>
              <Link
                to="/travel"
                className="border-transparent text-gray-700 hover:border-emerald-500 hover:text-emerald-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium"
              >
                🗺️ Mi Mapa
              </Link>
              <Link
                to="/trivia"
                className="border-transparent text-gray-700 hover:border-purple-500 hover:text-purple-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium"
              >
                🎮 Trivia
              </Link>
            </div>
          </div>
          
          {/* Botones de acción y perfil */}
          <div className="flex items-center">
            <div className="hidden sm:flex sm:items-center sm:ml-6">
              {isAuthenticated ? (
                <div className="ml-3 relative">
                  <div>
                    <button
                      type="button"
                      className="flex text-sm rounded-full focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500"
                      id="user-menu-button"
                      aria-expanded={isProfileMenuOpen ? 'true' : 'false'}
                      aria-haspopup="true"
                      onClick={() => setIsProfileMenuOpen(!isProfileMenuOpen)}
                    >
                      <span className="sr-only">Abrir menú de usuario</span>
                      <div className="h-8 w-8 rounded-full bg-green-600 flex items-center justify-center text-white">
                        {currentUser?.username?.charAt(0)?.toUpperCase() || 'U'}
                      </div>
                    </button>
                  </div>
                  
                  {/* Menú desplegable de perfil */}
                  {isProfileMenuOpen && (
                    <div
                      className="origin-top-right absolute right-0 mt-2 w-48 rounded-md shadow-lg py-1 bg-white ring-1 ring-black ring-opacity-5 focus:outline-none"
                      role="menu"
                      aria-orientation="vertical"
                      aria-labelledby="user-menu-button"
                      tabIndex="-1"
                    >
                      <Link
                        to="/profile"
                        className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                        role="menuitem"
                        tabIndex="-1"
                        id="user-menu-item-0"
                        onClick={() => setIsProfileMenuOpen(false)}
                      >
                        Tu Perfil
                      </Link>
                      <Link
                        to="/forums/create"
                        className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                        role="menuitem"
                        tabIndex="-1"
                        id="user-menu-item-1"
                        onClick={() => setIsProfileMenuOpen(false)}
                      >
                        Crear Foro
                      </Link>
                      <button
                        type="button"
                        className="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                        role="menuitem"
                        tabIndex="-1"
                        id="user-menu-item-2"
                        onClick={() => {
                          setIsProfileMenuOpen(false)
                          handleLogout()
                        }}
                      >
                        Cerrar Sesión
                      </button>
                    </div>
                  )}
                </div>
              ) : (
                <div className="flex space-x-4">
                  <Link
                    to="/login"
                    className="btn btn-outline"
                    aria-label="Iniciar sesión"
                  >
                    Iniciar Sesión
                  </Link>
                  <Link
                    to="/register"
                    className="btn btn-primary"
                    aria-label="Registrarse"
                  >
                    Registrarse
                  </Link>
                </div>
              )}
            </div>
            
            {/* Botón de menú móvil */}
            <div className="flex items-center sm:hidden">
              <button
                type="button"
                className="inline-flex items-center justify-center p-2 rounded-md text-gray-700 hover:text-green-600 hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-inset focus:ring-green-500"
                aria-controls="mobile-menu"
                aria-expanded={isMenuOpen ? 'true' : 'false'}
                onClick={() => setIsMenuOpen(!isMenuOpen)}
              >
                <span className="sr-only">Abrir menú principal</span>
                {!isMenuOpen ? (
                  <svg 
                    className="block h-6 w-6" 
                    xmlns="http://www.w3.org/2000/svg" 
                    fill="none" 
                    viewBox="0 0 24 24" 
                    stroke="currentColor" 
                    aria-hidden="true"
                  >
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 6h16M4 12h16M4 18h16" />
                  </svg>
                ) : (
                  <svg 
                    className="block h-6 w-6" 
                    xmlns="http://www.w3.org/2000/svg" 
                    fill="none" 
                    viewBox="0 0 24 24" 
                    stroke="currentColor" 
                    aria-hidden="true"
                  >
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                )}
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Menú móvil */}
      {isMenuOpen && (
        <div className="sm:hidden" id="mobile-menu">
          <div className="pt-2 pb-3 space-y-1">
            <Link
              to="/"
              className="block pl-3 pr-4 py-2 border-l-4 border-transparent text-base font-medium text-gray-700 hover:bg-gray-50 hover:border-green-500 hover:text-green-700"
              onClick={() => setIsMenuOpen(false)}
            >
              Inicio
            </Link>
            <Link
              to="/categories"
              className="block pl-3 pr-4 py-2 border-l-4 border-transparent text-base font-medium text-gray-700 hover:bg-gray-50 hover:border-green-500 hover:text-green-700"
              onClick={() => setIsMenuOpen(false)}
            >
              Continentes
            </Link>
            <Link
              to="/forums"
              className="block pl-3 pr-4 py-2 border-l-4 border-transparent text-base font-medium text-gray-700 hover:bg-gray-50 hover:border-green-500 hover:text-green-700"
              onClick={() => setIsMenuOpen(false)}
            >
              Foros
            </Link>
            <Link
              to="/travel"
              className="block pl-3 pr-4 py-2 border-l-4 border-transparent text-base font-medium text-gray-700 hover:bg-gray-50 hover:border-emerald-500 hover:text-emerald-700"
              onClick={() => setIsMenuOpen(false)}
            >
              🗺️ Mi Mapa
            </Link>
            <Link
              to="/trivia"
              className="block pl-3 pr-4 py-2 border-l-4 border-transparent text-base font-medium text-gray-700 hover:bg-gray-50 hover:border-purple-500 hover:text-purple-700"
              onClick={() => setIsMenuOpen(false)}
            >
              🎮 Trivia
            </Link>
          </div>
          
          <div className="pt-4 pb-3 border-t border-gray-200">
            {isAuthenticated ? (
              <div className="space-y-1">
                <div className="flex items-center px-4">
                  <div className="flex-shrink-0">
                    <div className="h-10 w-10 rounded-full bg-green-600 flex items-center justify-center text-white">
                      {currentUser?.username?.charAt(0)?.toUpperCase() || 'U'}
                    </div>
                  </div>
                  <div className="ml-3">
                    <div className="text-base font-medium text-gray-800">
                      {currentUser?.username || 'Usuario'}
                    </div>
                    <div className="text-sm font-medium text-gray-500">
                      {currentUser?.email || ''}
                    </div>
                  </div>
                </div>
                <Link
                  to="/profile"
                  className="block pl-3 pr-4 py-2 border-l-4 border-transparent text-base font-medium text-gray-700 hover:bg-gray-50 hover:border-green-500 hover:text-green-700"
                  onClick={() => setIsMenuOpen(false)}
                >
                  Tu Perfil
                </Link>
                <Link
                  to="/forums/create"
                  className="block pl-3 pr-4 py-2 border-l-4 border-transparent text-base font-medium text-gray-700 hover:bg-gray-50 hover:border-green-500 hover:text-green-700"
                  onClick={() => setIsMenuOpen(false)}
                >
                  Crear Foro
                </Link>
                <button
                  type="button"
                  className="block w-full text-left pl-3 pr-4 py-2 border-l-4 border-transparent text-base font-medium text-gray-700 hover:bg-gray-50 hover:border-green-500 hover:text-green-700"
                  onClick={() => {
                    setIsMenuOpen(false)
                    handleLogout()
                  }}
                >
                  Cerrar Sesión
                </button>
              </div>
            ) : (
              <div className="mt-3 space-y-1 px-2">
                <Link
                  to="/login"
                  className="block w-full text-center px-4 py-2 text-base font-medium text-gray-700 hover:bg-gray-100 rounded-md"
                  onClick={() => setIsMenuOpen(false)}
                >
                  Iniciar Sesión
                </Link>
                <Link
                  to="/register"
                  className="block w-full text-center px-4 py-2 text-base font-medium text-white bg-green-600 hover:bg-green-700 rounded-md"
                  onClick={() => setIsMenuOpen(false)}
                >
                  Registrarse
                </Link>
              </div>
            )}
          </div>
        </div>
      )}
    </nav>
  )
}

export default Navbar