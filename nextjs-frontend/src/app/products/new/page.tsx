'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useCreateProduct, useCategories } from '@/services/queries';
import { useAuthContext } from '@/components/auth/AuthProvider';
import Link from 'next/link';

export default function NewProductPage() {
  const router = useRouter();
  const { isAuthenticated, hasRole } = useAuthContext();
  const createProduct = useCreateProduct();
  const { data: categories } = useCategories();
  
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    price: '',
    category: '',
    status: 'active' as 'active' | 'discontinued'
  });
  
  const [errors, setErrors] = useState<Record<string, string>>({});
  
  // Redirect if not authenticated or doesn't have required role
  if (typeof window !== 'undefined' && (!isAuthenticated || !hasRole('PRODUCT_ADMIN'))) {
    router.push('/products');
    return null;
  }
  
  const validateForm = () => {
    const newErrors: Record<string, string> = {};
    
    if (!formData.name.trim()) {
      newErrors.name = 'Name is required';
    }
    
    if (!formData.price) {
      newErrors.price = 'Price is required';
    } else if (isNaN(parseFloat(formData.price)) || parseFloat(formData.price) < 0) {
      newErrors.price = 'Price must be a positive number';
    }
    
    if (!formData.category) {
      newErrors.category = 'Category is required';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };
  
  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    
    // Clear error when field is edited
    if (errors[name]) {
      setErrors(prev => {
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
      await createProduct.mutateAsync({
        name: formData.name,
        description: formData.description,
        price: parseFloat(formData.price),
        category: formData.category,
        status: formData.status
      });
      
      router.push('/products');
    } catch (error) {
      console.error('Failed to create product:', error);
      setErrors(prev => ({
        ...prev,
        form: 'Failed to create product. Please try again.'
      }));
    }
  };
  
  return (
    <div className="max-w-3xl mx-auto">
      <div className="flex items-center justify-between mb-6">
        <h1>Create New Product</h1>
        <Link href="/products" className="btn btn-secondary">
          Cancel
        </Link>
      </div>
      
      <div className="card">
        {errors.form && (
          <div className="mb-4 p-3 bg-red-100 text-red-700 rounded-md">
            {errors.form}
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
              className={`form-input ${errors.name ? 'border-red-500' : ''}`}
              value={formData.name}
              onChange={handleChange}
            />
            {errors.name && (
              <p className="mt-1 text-sm text-red-500">{errors.name}</p>
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
                className={`form-input ${errors.price ? 'border-red-500' : ''}`}
                value={formData.price}
                onChange={handleChange}
              />
              {errors.price && (
                <p className="mt-1 text-sm text-red-500">{errors.price}</p>
              )}
            </div>
            
            <div>
              <label htmlFor="category" className="form-label">
                Category <span className="text-red-500">*</span>
              </label>
              <select
                id="category"
                name="category"
                className={`form-input ${errors.category ? 'border-red-500' : ''}`}
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
              {errors.category && (
                <p className="mt-1 text-sm text-red-500">{errors.category}</p>
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
              disabled={createProduct.isPending}
            >
              {createProduct.isPending ? 'Creating...' : 'Create Product'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}