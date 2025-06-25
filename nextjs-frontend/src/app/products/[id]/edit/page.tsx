'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useProduct, useUpdateProduct, useCategories } from '@/services/queries';
import { useAuthContext } from '@/components/auth/AuthProvider';
import Link from 'next/link';

export default function EditProductPage({ params }: { params: { id: string } }) {
  const productId = parseInt(params.id, 10);
  const router = useRouter();
  const { isAuthenticated, hasRole } = useAuthContext();
  const { data: product, isLoading, error } = useProduct(productId);
  const updateProduct = useUpdateProduct();
  const { data: categories } = useCategories();
  
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    price: '',
    category: '',
    status: 'active' as 'active' | 'discontinued'
  });
  
  const [formErrors, setFormErrors] = useState<Record<string, string>>({});
  
  // Redirect if not authenticated or doesn't have required role
  if (typeof window !== 'undefined' && (!isAuthenticated || !hasRole('PRODUCT_ADMIN'))) {
    router.push('/products');
    return null;
  }
  
  // Populate form when product data is loaded
  useEffect(() => {
    if (product) {
      setFormData({
        name: product.name,
        description: product.description || '',
        price: product.price.toString(),
        category: product.category,
        status: product.status || 'active'
      });
    }
  }, [product]);
  
  const validateForm = () => {
    const errors: Record<string, string> = {};
    
    if (!formData.name.trim()) {
      errors.name = 'Name is required';
    }
    
    if (!formData.price) {
      errors.price = 'Price is required';
    } else if (isNaN(parseFloat(formData.price)) || parseFloat(formData.price) < 0) {
      errors.price = 'Price must be a positive number';
    }
    
    if (!formData.category) {
      errors.category = 'Category is required';
    }
    
    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };
  
  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    
    // Clear error when field is edited
    if (formErrors[name]) {
      setFormErrors(prev => {
        const newErrors = { ...prev };
        delete newErrors[name];
        return newErrors;
      });
    }
  };
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }
    
    try {
      await updateProduct.mutateAsync({
        id: productId,
        product: {
          name: formData.name,
          description: formData.description,
          price: parseFloat(formData.price),
          category: formData.category,
          status: formData.status
        }
      });
      
      router.push('/products');
    } catch (error) {
      console.error('Failed to update product:', error);
      setFormErrors(prev => ({
        ...prev,
        form: 'Failed to update product. Please try again.'
      }));
    }
  };
  
  if (isLoading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary-600"></div>
      </div>
    );
  }
  
  if (error || !product) {
    return (
      <div className="text-center py-10">
        <h2 className="text-red-600">Error loading product</h2>
        <p className="text-gray-600 mt-2">{(error as Error)?.message || 'Product not found'}</p>
        <Link href="/products" className="btn btn-primary mt-4">
          Back to Products
        </Link>
      </div>
    );
  }
  
  return (
    <div className="max-w-3xl mx-auto">
      <div className="flex items-center justify-between mb-6">
        <h1>Edit Product</h1>
        <Link href="/products" className="btn btn-secondary">
          Cancel
        </Link>
      </div>
      
      <div className="card">
        {formErrors.form && (
          <div className="mb-4 p-3 bg-red-100 text-red-700 rounded-md">
            {formErrors.form}
          </div>
        )}
        
        <form onSubmit={handleSubmit}>
          <div className="mb-4">
            <label htmlFor="name" className="form-label">
              Name <span className="text-red-500">*</span>
            </label>
            <input
              id="name"
              name="name"
              type="text"
              className={`form-input ${formErrors.name ? 'border-red-500' : ''}`}
              value={formData.name}
              onChange={handleChange}
            />
            {formErrors.name && (
              <p className="mt-1 text-sm text-red-500">{formErrors.name}</p>
            )}
          </div>
          
          <div className="mb-4">
            <label htmlFor="description" className="form-label">
              Description
            </label>
            <textarea
              id="description"
              name="description"
              rows={3}
              className="form-input"
              value={formData.description}
              onChange={handleChange}
            />
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
            <div>
              <label htmlFor="price" className="form-label">
                Price <span className="text-red-500">*</span>
              </label>
              <input
                id="price"
                name="price"
                type="number"
                step="0.01"
                min="0"
                className={`form-input ${formErrors.price ? 'border-red-500' : ''}`}
                value={formData.price}
                onChange={handleChange}
              />
              {formErrors.price && (
                <p className="mt-1 text-sm text-red-500">{formErrors.price}</p>
              )}
            </div>
            
            <div>
              <label htmlFor="category" className="form-label">
                Category <span className="text-red-500">*</span>
              </label>
              <select
                id="category"
                name="category"
                className={`form-input ${formErrors.category ? 'border-red-500' : ''}`}
                value={formData.category}
                onChange={handleChange}
              >
                <option value="">Select a category</option>
                {categories?.map(category => (
                  <option key={category} value={category}>
                    {category}
                  </option>
                ))}
              </select>
              {formErrors.category && (
                <p className="mt-1 text-sm text-red-500">{formErrors.category}</p>
              )}
            </div>
          </div>
          
          <div className="mb-6">
            <label className="form-label">Status</label>
            <div className="flex space-x-4 mt-1">
              <label className="inline-flex items-center">
                <input
                  type="radio"
                  name="status"
                  value="active"
                  checked={formData.status === 'active'}
                  onChange={handleChange}
                  className="form-radio h-4 w-4 text-primary-600"
                />
                <span className="ml-2">Active</span>
              </label>
              <label className="inline-flex items-center">
                <input
                  type="radio"
                  name="status"
                  value="discontinued"
                  checked={formData.status === 'discontinued'}
                  onChange={handleChange}
                  className="form-radio h-4 w-4 text-primary-600"
                />
                <span className="ml-2">Discontinued</span>
              </label>
            </div>
          </div>
          
          <div className="flex justify-end">
            <button
              type="submit"
              className="btn btn-primary"
              disabled={updateProduct.isPending}
            >
              {updateProduct.isPending ? 'Saving...' : 'Save Changes'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}