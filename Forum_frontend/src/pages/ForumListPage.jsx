// Archivo: src/pages/ForumListPage.jsx
import { useParams } from 'react-router-dom'
import ForumList from '../components/forums/ForumList'

const ForumListPage = () => {
  const { categoryId } = useParams()
  
  return (
    <div className="bg-neutral-50 py-8 sm:py-12">
      <div className="container px-4 sm:px-6 lg:px-8 mx-auto">
        <ForumList categoryId={categoryId} />
      </div>
    </div>
  )
}

export default ForumListPage