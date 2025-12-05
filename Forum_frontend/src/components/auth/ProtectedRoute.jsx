import { Navigate, useLocation } from 'react-router-dom'
import useAuth from '../../hooks/useAuth'
import LoadingSpinner from '../common/LoadingSpinner'
import PropTypes from 'prop-types'

const ProtectedRoute = ({ children, requiredRoles = [] }) => {
  const { currentUser, loading, isAuthenticated } = useAuth()
  const location = useLocation()


  if (loading) {
    return <LoadingSpinner />
  }


  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location.pathname }} replace />
  }


  if (requiredRoles.length > 0) {
    const hasRequiredRole = requiredRoles.some(role => 
      currentUser.roles?.includes(role)
    )

    if (!hasRequiredRole) {
      return <Navigate to="/" replace />
    }
  }


  return children
}
ProtectedRoute.propTypes = {
  children: PropTypes.node.isRequired,
  requiredRoles: PropTypes.arrayOf(PropTypes.string)
}

export default ProtectedRoute