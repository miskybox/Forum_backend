// Archivo: src/components/common/LoadingSpinner.jsx
import React from 'react'
import PropTypes from 'prop-types'

const LoadingSpinner = ({ size = 'medium' }) => {
  // Determinar el tamaño del spinner
  let dimensions
  switch (size) {
    case 'small':
      dimensions = 'h-6 w-6'
      break
    case 'large':
      dimensions = 'h-16 w-16'
      break
    case 'medium':
    default:
      dimensions = 'h-10 w-10'
      break
  }

  return (
    <div className="flex justify-center items-center">
      <div className={`${dimensions} border-4 border-neutral-200 border-t-primary-600 rounded-full animate-spin`}></div>
    </div>
  )
}
LoadingSpinner.propTypes = {
  size: PropTypes.oneOf(['small', 'medium', 'large'])
}

export default LoadingSpinner
