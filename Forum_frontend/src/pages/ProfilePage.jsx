// Archivo: src/pages/ProfilePage.jsx
import { useState, useEffect, useRef } from 'react'
import { toast } from 'react-hot-toast'
import useAuth from '../hooks/useAuth'
import userService from '../services/userService'
import ForumList from '../components/forums/ForumList'
import PostList from '../components/post/PostList';

const ProfilePage = () => {
  const { currentUser, isAuthenticated } = useAuth()
  
  const [profileData, setProfileData] = useState({
    username: '',
    firstName: '',
    lastName: '',
    email: '',
    bio: ''
  })
  
  const [passwordData, setPasswordData] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  })
  
  const [profileImage, setProfileImage] = useState(null)
  const [imagePreview, setImagePreview] = useState('')
  const [activeTab, setActiveTab] = useState('profile')
  const [loading, setLoading] = useState(true)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [errors, setErrors] = useState({})
  
  const imageInputRef = useRef(null)
  
  useEffect(() => {
    if (currentUser) {
      setProfileData({
        username: currentUser.username || '',
        firstName: currentUser.firstName || '',
        lastName: currentUser.lastName || '',
        email: currentUser.email || '',
        bio: currentUser.bio || ''
      })
      
      if (currentUser.profileImage) {
        setImagePreview(currentUser.profileImage)
      }
      
      setLoading(false)
    }
  }, [currentUser])
  
  const handleProfileChange = (e) => {
    const { name, value } = e.target
    setProfileData({
      ...profileData,
      [name]: value
    })
    
    // Limpiar error del campo que se está editando
    if (errors[name]) {
      setErrors({
        ...errors,
        [name]: ''
      })
    }
  }
  
  const handlePasswordChange = (e) => {
    const { name, value } = e.target
    setPasswordData({
      ...passwordData,
      [name]: value
    })
    
    // Limpiar error del campo que se está editando
    if (errors[name]) {
      setErrors({
        ...errors,
        [name]: ''
      })
    }
  }
  
  const handleImageChange = (e) => {
    const file = e.target.files[0]
    if (!file) return
    
    // Verificar tamaño y tipo de archivo
    if (file.size > 2 * 1024 * 1024) { // 2 MB
      setErrors({
        ...errors,
        profileImage: 'La imagen debe ser menor a 2 MB'
      })
      return
    }
    
    const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
    if (!validTypes.includes(file.type)) {
      setErrors({
        ...errors,
        profileImage: 'El formato de imagen debe ser JPEG, PNG, GIF o WEBP'
      })
      return
    }
    
    setProfileImage(file)
    
    // Crear preview de la imagen
    const reader = new FileReader()
    reader.onload = () => {
      setImagePreview(reader.result)
    }
    reader.readAsDataURL(file)
    
    // Limpiar error de imagen
    if (errors.profileImage) {
      setErrors({
        ...errors,
        profileImage: ''
      })
    }
  }
  
  const validateProfileForm = () => {
    const newErrors = {}
    
    if (!profileData.username.trim()) {
      newErrors.username = 'El nombre de usuario es obligatorio'
    } else if (profileData.username.length < 3) {
      newErrors.username = 'El nombre de usuario debe tener al menos 3 caracteres'
    }
    
    if (!profileData.firstName.trim()) {
      newErrors.firstName = 'El nombre es obligatorio'
    }
    
    if (!profileData.lastName.trim()) {
      newErrors.lastName = 'El apellido es obligatorio'
    }
    
    if (!profileData.email.trim()) {
      newErrors.email = 'El correo electrónico es obligatorio'
    } else if (!/\S+@\S+\.\S+/.test(profileData.email)) {
      newErrors.email = 'Introduce un correo electrónico válido'
    }
    
    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }
  
  const validatePasswordForm = () => {
    const newErrors = {}
    
    if (!passwordData.currentPassword) {
      newErrors.currentPassword = 'La contraseña actual es obligatoria'
    }
    
    if (!passwordData.newPassword) {
      newErrors.newPassword = 'La nueva contraseña es obligatoria'
    } else if (passwordData.newPassword.length < 6) {
      newErrors.newPassword = 'La contraseña debe tener al menos 6 caracteres'
    }
    
    if (passwordData.newPassword !== passwordData.confirmPassword) {
      newErrors.confirmPassword = 'Las contraseñas no coinciden'
    }
    
    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }
  
  const handleUpdateProfile = async (e) => {
    e.preventDefault()
    
    if (!validateProfileForm()) {
      return
    }
    
    setIsSubmitting(true)
    
    try {
      // Actualizar información del perfil
      await userService.updateUser(currentUser.id, profileData)
      
      // Si hay una nueva imagen, actualizarla
      if (profileImage) {
        // Aquí agregaríamos la lógica para actualizar la imagen de perfil
        // Esto dependerá de cómo esté implementado en el backend
      }
      
      toast.success('Perfil actualizado con éxito')
    } catch (error) {
      console.error('Error al actualizar el perfil:', error)
      
      if (error.response?.status === 400 && error.response?.data?.errors) {
        const backendErrors = error.response.data.errors
        const formattedErrors = {}
        
        Object.keys(backendErrors).forEach(key => {
          formattedErrors[key] = backendErrors[key]
        })
        
        setErrors({
          ...errors,
          ...formattedErrors
        })
      } else {
        toast.error('Error al actualizar el perfil')
      }
    } finally {
      setIsSubmitting(false)
    }
  }
  
  const handleUpdatePassword = async (e) => {
    e.preventDefault()
    
    if (!validatePasswordForm()) {
      return
    }
    
    setIsSubmitting(true)
    
    try {
      await userService.changePassword(
        currentUser.id,
        passwordData.currentPassword,
        passwordData.newPassword
      )
      
      // Limpiar el formulario
      setPasswordData({
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      })
      
      toast.success('Contraseña actualizada con éxito')
    } catch (error) {
      console.error('Error al actualizar la contraseña:', error)
      
      if (error.response?.status === 400) {
        if (error.response?.data?.message?.includes('current')) {
          setErrors({
            ...errors,
            currentPassword: 'La contraseña actual es incorrecta'
          })
        } else {
          toast.error('Error al actualizar la contraseña')
        }
      } else {
        toast.error('Error al actualizar la contraseña')
      }
    } finally {
      setIsSubmitting(false)
    }
  }
  
  if (loading) {
    return (
      <div className="flex justify-center items-center py-20">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary-600"></div>
      </div>
    )
  }
  
  if (!isAuthenticated) {
    return (
      <div className="bg-neutral-50 py-10">
        <div className="container px-4 sm:px-6 lg:px-8 mx-auto">
          <div className="text-center py-10 bg-white rounded-lg shadow-sm p-6">
            <div className="text-red-600 mb-4">Debes iniciar sesión para acceder a tu perfil</div>
            <a href="/login" className="btn btn-primary">
              Iniciar sesión
            </a>
          </div>
        </div>
      </div>
    )
  }
  
  return (
    <div className="bg-neutral-50 py-8 sm:py-12">
      <div className="container px-4 sm:px-6 lg:px-8 mx-auto">
        <div className="max-w-5xl mx-auto">
          {/* Cabecera del perfil */}
          <div className="bg-white rounded-lg shadow-sm p-6 mb-8">
            <div className="flex flex-col md:flex-row md:items-center">
              <div className="flex-shrink-0 mb-4 md:mb-0 md:mr-6">
                <div className="relative w-24 h-24 rounded-full overflow-hidden bg-primary-500 flex items-center justify-center text-white text-4xl">
                  {imagePreview ? (
                    <img 
                      src={imagePreview} 
                      alt={profileData.username}
                      className="w-full h-full object-cover"
                    />
                  ) : (
                    profileData.username?.charAt(0)?.toUpperCase() || 'U'
                  )}
                </div>
              </div>
              
              <div className="flex-grow">
                <h1 className="text-2xl font-bold text-neutral-800 mb-1">
                  {profileData.firstName} {profileData.lastName}
                </h1>
                <p className="text-neutral-600 mb-2">@{profileData.username}</p>
                {profileData.bio && (
                  <p className="text-neutral-700">{profileData.bio}</p>
                )}
              </div>
            </div>
          </div>
          
          {/* Pestañas */}
          <div className="mb-6">
            <div className="border-b border-neutral-200">
              <nav className="flex -mb-px">
                <button
                  className={`py-4 px-6 text-sm font-medium ${
                    activeTab === 'profile'
                      ? 'border-b-2 border-primary-500 text-primary-600'
                      : 'text-neutral-500 hover:text-neutral-700 hover:border-neutral-300'
                  }`}
                  onClick={() => setActiveTab('profile')}
                >
                  Perfil
                </button>
                <button
                  className={`py-4 px-6 text-sm font-medium ${
                    activeTab === 'password'
                      ? 'border-b-2 border-primary-500 text-primary-600'
                      : 'text-neutral-500 hover:text-neutral-700 hover:border-neutral-300'
                  }`}
                  onClick={() => setActiveTab('password')}
                >
                  Contraseña
                </button>
                <button
                  className={`py-4 px-6 text-sm font-medium ${
                    activeTab === 'forums'
                      ? 'border-b-2 border-primary-500 text-primary-600'
                      : 'text-neutral-500 hover:text-neutral-700 hover:border-neutral-300'
                  }`}
                  onClick={() => setActiveTab('forums')}
                >
                  Mis Foros
                </button>
                <button
                  className={`py-4 px-6 text-sm font-medium ${
                    activeTab === 'posts'
                      ? 'border-b-2 border-primary-500 text-primary-600'
                      : 'text-neutral-500 hover:text-neutral-700 hover:border-neutral-300'
                  }`}
                  onClick={() => setActiveTab('posts')}
                >
                  Mis Publicaciones
                </button>
              </nav>
            </div>
          </div>
          
          {/* Contenido de las pestañas */}
          <div className="bg-white rounded-lg shadow-sm p-6">
            {activeTab === 'profile' && (
              <form onSubmit={handleUpdateProfile} className="space-y-6">
                <div className="space-y-4">
                  <h2 className="text-xl font-semibold text-neutral-800 mb-4">
                    Información personal
                  </h2>
                  
                  {/* Imagen de perfil */}
                  <div>
                    <label htmlFor="profileImage" className="block text-sm font-medium text-neutral-700 mb-1">
                      Imagen de perfil
                    </label>
                    
                    <div className="mt-1 flex items-center">
                      <div className="relative w-16 h-16 rounded-full overflow-hidden bg-primary-500 flex items-center justify-center text-white text-xl mr-4">
                        {imagePreview ? (
                          <img 
                            src={imagePreview} 
                            alt={profileData.username}
                            className="w-full h-full object-cover"
                          />
                        ) : (
                          profileData.username?.charAt(0)?.toUpperCase() || 'U'
                        )}
                      </div>
                      
                      <input
                        id="profileImage"
                        name="profileImage"
                        type="file"
                        accept="image/*"
                        ref={imageInputRef}
                        className="sr-only"
                        onChange={handleImageChange}
                        disabled={isSubmitting}
                      />
                      
                      <label
                        htmlFor="profileImage"
                        className="cursor-pointer bg-white py-2 px-3 border border-neutral-300 rounded-md shadow-sm text-sm leading-4 font-medium text-neutral-700 hover:bg-neutral-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500"
                      >
                        Cambiar
                      </label>
                    </div>
                    
                    {errors.profileImage && (
                      <p className="mt-1 text-sm text-red-600">{errors.profileImage}</p>
                    )}
                  </div>
                  
                  {/* Grid de campos */}
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <label htmlFor="firstName" className="block text-sm font-medium text-neutral-700 mb-1">
                        Nombre <span className="text-red-500">*</span>
                      </label>
                      <input
                        id="firstName"
                        name="firstName"
                        type="text"
                        autoComplete="given-name"
                        required
                        className={`input w-full ${errors.firstName ? 'border-red-500 focus:ring-red-500' : ''}`}
                        value={profileData.firstName}
                        onChange={handleProfileChange}
                        disabled={isSubmitting}
                      />
                      {errors.firstName && (
                        <p className="mt-1 text-sm text-red-600">{errors.firstName}</p>
                      )}
                    </div>
                    
                    <div>
                      <label htmlFor="lastName" className="block text-sm font-medium text-neutral-700 mb-1">
                        Apellido <span className="text-red-500">*</span>
                      </label>
                      <input
                        id="lastName"
                        name="lastName"
                        type="text"
                        autoComplete="family-name"
                        required
                        className={`input w-full ${errors.lastName ? 'border-red-500 focus:ring-red-500' : ''}`}
                        value={profileData.lastName}
                        onChange={handleProfileChange}
                        disabled={isSubmitting}
                      />
                      {errors.lastName && (
                        <p className="mt-1 text-sm text-red-600">{errors.lastName}</p>
                      )}
                    </div>
                    
                    <div>
                      <label htmlFor="username" className="block text-sm font-medium text-neutral-700 mb-1">
                        Nombre de usuario <span className="text-red-500">*</span>
                      </label>
                      <input
                        id="username"
                        name="username"
                        type="text"
                        autoComplete="username"
                        required
                        className={`input w-full ${errors.username ? 'border-red-500 focus:ring-red-500' : ''}`}
                        value={profileData.username}
                        onChange={handleProfileChange}
                        disabled={isSubmitting}
                      />
                      {errors.username && (
                        <p className="mt-1 text-sm text-red-600">{errors.username}</p>
                      )}
                    </div>
                    
                    <div>
                      <label htmlFor="email" className="block text-sm font-medium text-neutral-700 mb-1">
                        Correo electrónico <span className="text-red-500">*</span>
                      </label>
                      <input
                        id="email"
                        name="email"
                        type="email"
                        autoComplete="email"
                        required
                        className={`input w-full ${errors.email ? 'border-red-500 focus:ring-red-500' : ''}`}
                        value={profileData.email}
                        onChange={handleProfileChange}
                        disabled={isSubmitting}
                      />
                      {errors.email && (
                        <p className="mt-1 text-sm text-red-600">{errors.email}</p>
                      )}
                    </div>
                  </div>
                  
                  <div>
                    <label htmlFor="bio" className="block text-sm font-medium text-neutral-700 mb-1">
                      Biografía
                    </label>
                    <textarea
                      id="bio"
                      name="bio"
                      rows={4}
                      className="input w-full"
                      value={profileData.bio}
                      onChange={handleProfileChange}
                      placeholder="Cuéntanos sobre ti..."
                      disabled={isSubmitting}
                    />
                  </div>
                </div>
                
                <div className="flex justify-end pt-2">
                  <button
                    type="submit"
                    className="btn btn-primary"
                    disabled={isSubmitting}
                  >
                    {isSubmitting ? (
                      <span className="flex items-center">
                        <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                        </svg>
                        Guardando...
                      </span>
                    ) : (
                      'Guardar cambios'
                    )}
                  </button>
                </div>
              </form>
            )}
            
            {activeTab === 'password' && (
              <form onSubmit={handleUpdatePassword} className="space-y-6">
                <div className="space-y-4">
                  <h2 className="text-xl font-semibold text-neutral-800 mb-4">
                    Cambiar contraseña
                  </h2>
                  
                  <div>
                    <label htmlFor="currentPassword" className="block text-sm font-medium text-neutral-700 mb-1">
                      Contraseña actual <span className="text-red-500">*</span>
                    </label>
                    <input
                      id="currentPassword"
                      name="currentPassword"
                      type="password"
                      autoComplete="current-password"
                      required
                      className={`input w-full ${errors.currentPassword ? 'border-red-500 focus:ring-red-500' : ''}`}
                      value={passwordData.currentPassword}
                      onChange={handlePasswordChange}
                      disabled={isSubmitting}
                    />
                    {errors.currentPassword && (
                      <p className="mt-1 text-sm text-red-600">{errors.currentPassword}</p>
                    )}
                  </div>
                  
                  <div>
                    <label htmlFor="newPassword" className="block text-sm font-medium text-neutral-700 mb-1">
                      Nueva contraseña <span className="text-red-500">*</span>
                    </label>
                    <input
                      id="newPassword"
                      name="newPassword"
                      type="password"
                      autoComplete="new-password"
                      required
                      className={`input w-full ${errors.newPassword ? 'border-red-500 focus:ring-red-500' : ''}`}
                      value={passwordData.newPassword}
                      onChange={handlePasswordChange}
                      disabled={isSubmitting}
                    />
                    {errors.newPassword && (
                      <p className="mt-1 text-sm text-red-600">{errors.newPassword}</p>
                    )}
                  </div>
                  
                  <div>
                    <label htmlFor="confirmPassword" className="block text-sm font-medium text-neutral-700 mb-1">
                      Confirmar contraseña <span className="text-red-500">*</span>
                    </label>
                    <input
                      id="confirmPassword"
                      name="confirmPassword"
                      type="password"
                      autoComplete="new-password"
                      required
                      className={`input w-full ${errors.confirmPassword ? 'border-red-500 focus:ring-red-500' : ''}`}
                      value={passwordData.confirmPassword}
                      onChange={handlePasswordChange}
                      disabled={isSubmitting}
                    />
                    {errors.confirmPassword && (
                      <p className="mt-1 text-sm text-red-600">{errors.confirmPassword}</p>
                    )}
                  </div>
                </div>
                
                <div className="flex justify-end pt-2">
                  <button
                    type="submit"
                    className="btn btn-primary"
                    disabled={isSubmitting}
                  >
                    {isSubmitting ? (
                      <span className="flex items-center">
                        <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                        </svg>
                        Guardando...
                      </span>
                    ) : (
                      'Cambiar contraseña'
                    )}
                  </button>
                </div>
              </form>
            )}
            
            {activeTab === 'forums' && (
              <div>
                <h2 className="text-xl font-semibold text-neutral-800 mb-4">
                  Mis foros
                </h2>
                
                {/* Aquí iría el componente para mostrar los foros del usuario */}
                <ForumList />
              </div>
            )}
            
            {activeTab === 'posts' && (
              <div>
                <h2 className="text-xl font-semibold text-neutral-800 mb-4">
                  Mis publicaciones
                </h2>
                
                {/* Aquí iría el componente para mostrar las publicaciones del usuario */}
                <PostList />
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}

export default ProfilePage