/* eslint-disable react-refresh/only-export-components */
import { createContext, useState, useEffect, useMemo, useCallback } from 'react'
import PropTypes from 'prop-types'
import authService from '../services/authService'

export const AuthContext = createContext()

export const AuthProvider = ({ children }) => {
  const [currentUser, setCurrentUser] = useState(null)
  const [token, setToken] = useState(localStorage.getItem('token') || null)
  const [refreshToken, setRefreshToken] = useState(localStorage.getItem('refreshToken') || null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const logout = useCallback(async () => {
    setLoading(true)
    try {
      if (token) {
        await authService.logout()
      }
    } catch (err) {
      console.error('Error durante el cierre de sesión:', err)
      setError(err.response?.data?.message || 'No se pudo cerrar la sesión correctamente')
    } finally {
      setToken(null)
      setRefreshToken(null)
      setCurrentUser(null)
      localStorage.removeItem('token')
      localStorage.removeItem('refreshToken')
      setLoading(false)
    }
  }, [token])

  useEffect(() => {
    const initAuth = async () => {
      if (token) {
        try {
          const userInfo = await authService.getCurrentUser()
          setCurrentUser(userInfo)
        } catch (err) {
          setError(err.response?.data?.message || 'Error al obtener usuario actual')

          if (refreshToken) {
            try {
              const response = await authService.refreshToken(refreshToken)
              setToken(response.accessToken)
              setRefreshToken(response.refreshToken)
              localStorage.setItem('token', response.accessToken)
              localStorage.setItem('refreshToken', response.refreshToken)

              const userInfo = await authService.getCurrentUser()
              setCurrentUser(userInfo)
            } catch (refreshErr) {
              setError(refreshErr.response?.data?.message || 'Error al refrescar el token')
              logout()
            }
          } else {
            logout()
          }
        } finally {
          setLoading(false)
        }
      } else {
        setLoading(false)
      }
    }

    initAuth()
  }, [token, refreshToken, logout])

  const login = useCallback(async (credentials) => {
    try {
      setError(null)
      setLoading(true)
      const response = await authService.login(credentials)
      setToken(response.accessToken)
      setRefreshToken(response.refreshToken)
      localStorage.setItem('token', response.accessToken)
      localStorage.setItem('refreshToken', response.refreshToken)

      const userInfo = await authService.getCurrentUser()
      setCurrentUser(userInfo)
      return userInfo
    } catch (err) {
      setError(err.response?.data?.message || 'Error al iniciar sesión')
      throw err
    } finally {
      setLoading(false)
    }
  }, [])

  const register = useCallback(async (userData) => {
    try {
      setError(null)
      setLoading(true)
      const response = await authService.register(userData)
      return response
    } catch (err) {
      setError(err.response?.data?.message || 'Error al registrar')
      throw err
    } finally {
      setLoading(false)
    }
  }, [])

  const value = useMemo(() => ({
    currentUser,
    token,
    loading,
    error,
    login,
    register,
    logout,
    isAuthenticated: !!currentUser,
    hasRole: (role) => currentUser?.roles?.includes(role) || false,
  }), [currentUser, token, loading, error, login, register, logout])

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  )
}

AuthProvider.propTypes = {
  children: PropTypes.node.isRequired,
}
