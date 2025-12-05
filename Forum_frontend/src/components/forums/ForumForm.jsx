import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { toast } from 'react-hot-toast'
import categoryService from '../../services/categoryService'
import forumService from '../../services/forumService'

const ForumForm = ({ initialData = null, isEdit = false }) => {
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    categoryId: '',
    ...initialData
  })
  
  const [categories, setCategories] = useState([])
  const [image, setImage] = useState(null)
  const [imagePreview, setImagePreview] = useState(initialData?.imageUrl || '')
  const [errors, setErrors] = useState({})
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [isLoading, setIsLoading] = useState(true)
  
  const navigate = useNavigate()
  
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const data = await categoryService.getAllCategories()
        setCategories(data)
  
        if (!formData.categoryId && data.length > 0) {
          setFormData(prevData => ({
            ...prevData,
            categoryId: data[0].id
          }))
        }
      } catch (error) {
        console.error('Error al cargar las categorías:', error)
        toast.error('No se pudieron cargar las categorías. Por favor, inténtalo de nuevo.')
      } finally {
        setIsLoading(false)
      }
    }
    
    fetchCategories()
  }, [formData.categoryId])
  
  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData({
      ...formData,
      [name]: value
    })
    
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
    
  
    if (file.size > 5 * 1024 * 1024) { 
      setErrors({
        ...errors,
        image: 'La imagen debe ser menor a 5 MB'
      })
      return
    }
    
    const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
    if (!validTypes.includes(file.type)) {
      setErrors({
        ...errors,
        image: 'El formato de imagen debe ser JPEG, PNG, GIF o WEBP'
      })
      return
    }
    
    setImage(file)
    
    const reader = new FileReader()
    reader.onload = () => {
      setImagePreview(reader.result)
    }
    reader.readAsDataURL(file)

    if (errors.image) {
      setErrors({
        ...errors,
        image: ''
      })
    }
  }
  
  const validateForm = () => {
    const newErrors = {}
    
    if (!formData.title.trim()) {
      newErrors.title = 'El título es obligatorio'
    } else if (formData.title.length < 5) {
      newErrors.title = 'El título debe tener al menos 5 caracteres'
    }
    
    if (!formData.description.trim()) {
      newErrors.description = 'La descripción es obligatoria'
    } else if (formData.description.length < 10) {
      newErrors.description = 'La descripción debe tener al menos 10 caracteres'
    }
    
    if (!formData.categoryId) {
      newErrors.categoryId = 'Selecciona una categoría'
    }
    
    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }
  
  const handleSubmit = async (e) => {
    e.preventDefault()
    
    if (!validateForm()) {
      return
    }
    
    setIsSubmitting(true)
    
    try {
      let response
      
      if (isEdit) {

        response = await forumService.updateForum(initialData.id, formData)
        
        if (image) {
          response = await forumService.uploadForumImage(initialData.id, image)
        }
        
        toast.success('¡Foro actualizado con éxito!')
      } else {

        response = await forumService.createForum(formData)
        
        if (image) {
          response = await forumService.uploadForumImage(response.id, image)
        }
        
        toast.success('¡Foro creado con éxito!')
      }
      
      navigate(`/forums/${response.id}`)
    } catch (error) {
      console.error('Error al guardar el foro:', error)
      toast.error(error.response?.data?.message || 'Error al guardar el foro. Por favor, inténtalo de nuevo.')
      
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
      }
    } finally {
      setIsSubmitting(false)
    }
  }
  
  if (isLoading) {
    return (
      <div className="flex justify-center items-center py-20">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary-600"></div>
      </div>
    )
  }
  
  const getImageLabel = () => {
    if (image) {
      return image.name;
    } else if (imagePreview) {
      return 'Imagen cargada';
    } else {
      return 'No se ha seleccionado ninguna imagen';
    }
  };

  const getButtonContent = () => {
    const buttonText = isEdit ? 'Actualizar Foro' : 'Crear Foro';
    const loadingText = isEdit ? 'Actualizando...' : 'Creando...';

    if (isSubmitting) {
      return (
        <span className="flex items-center justify-center">
          <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          {loadingText}
        </span>
      );
    }

    return (
      <span>{buttonText}</span>
    );
  };
  
  return (
    <div className="max-w-2xl mx-auto">
      <form onSubmit={handleSubmit} className="space-y-6">
        <div>
          <label htmlFor="title" className="block text-sm font-medium text-neutral-700 mb-1">
            Título <span className="text-red-500">*</span>
          </label>
          <input
            id="title"
            name="title"
            type="text"
            required
            className={`input w-full ${errors.title ? 'border-red-500 focus:ring-red-500' : ''}`}
            value={formData.title}
            onChange={handleChange}
            placeholder="Título del foro"
            disabled={isSubmitting}
          />
          {errors.title && (
            <p className="mt-1 text-sm text-red-600">{errors.title}</p>
          )}
        </div>

        <div>
          <label htmlFor="description" className="block text-sm font-medium text-neutral-700 mb-1">
            Descripción <span className="text-red-500">*</span>
          </label>
          <textarea
            id="description"
            name="description"
            rows={4}
            required
            className={`input w-full ${errors.description ? 'border-red-500 focus:ring-red-500' : ''}`}
            value={formData.description}
            onChange={handleChange}
            placeholder="Descripción del foro"
            disabled={isSubmitting}
          />
          {errors.description && (
            <p className="mt-1 text-sm text-red-600">{errors.description}</p>
          )}
        </div>
        
        <div>
          <label htmlFor="categoryId" className="block text-sm font-medium text-neutral-700 mb-1">
            Continente <span className="text-red-500">*</span>
          </label>
          <select
            id="categoryId"
            name="categoryId"
            required
            className={`input w-full ${errors.categoryId ? 'border-red-500 focus:ring-red-500' : ''}`}
            value={formData.categoryId}
            onChange={handleChange}
            disabled={isSubmitting}
          >
            <option value="" disabled>Selecciona un continente</option>
            {categories.map((category) => (
              <option key={category.id} value={category.id}>
                {category.name}
              </option>
            ))}
          </select>
          {errors.categoryId && (
            <p className="mt-1 text-sm text-red-600">{errors.categoryId}</p>
          )}
        </div>

        <div>
          <label htmlFor="image" className="block text-sm font-medium text-neutral-700 mb-1">
            Imagen (opcional)
          </label>
          
          <div className="mt-1 flex items-center">
            <input
              id="image"
              name="image"
              type="file"
              accept="image/*"
              className="sr-only"
              onChange={handleImageChange}
              disabled={isSubmitting}
            />
            <label
              htmlFor="image"
              className="cursor-pointer bg-white py-2 px-3 border border-neutral-300 rounded-md shadow-sm text-sm leading-4 font-medium text-neutral-700 hover:bg-neutral-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500"
            >
              Seleccionar imagen
            </label>
            
            <span className="ml-3 text-sm text-neutral-500">
              {getImageLabel()}
            </span>
          </div>
          
          {errors.image && (
            <p className="mt-1 text-sm text-red-600">{errors.image}</p>
          )}
          
          {imagePreview && (
            <div className="mt-3">
              <div className="relative w-full h-48 bg-neutral-200 rounded overflow-hidden">
                <img 
                  src={imagePreview} 
                  alt="Vista previa" 
                  className="w-full h-full object-cover"
                />
                <button
                  type="button"
                  className="absolute top-2 right-2 bg-red-500 text-white rounded-full p-1 hover:bg-red-600"
                  onClick={() => {
                    setImage(null)
                    setImagePreview('')
                  }}
                  aria-label="Eliminar imagen"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                    <path fillRule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clipRule="evenodd" />
                  </svg>
                </button>
              </div>
            </div>
          )}
        </div>
        
        <div className="flex gap-4 pt-2">
          <button
            type="button"
            className="btn btn-outline"
            onClick={() => navigate(-1)}
            disabled={isSubmitting}
          >
            Cancelar
          </button>
          
          <button
            type="submit"
            className="btn btn-primary flex-1"
            disabled={isSubmitting}
          >
            {getButtonContent()}
          </button>
        </div>
      </form>
    </div>
  )
}

import PropTypes from 'prop-types'

ForumForm.propTypes = {
  initialData: PropTypes.object,
  isEdit: PropTypes.bool
}

export default ForumForm