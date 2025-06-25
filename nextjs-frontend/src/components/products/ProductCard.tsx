'use client';

import Link from 'next/link';
import { Product } from '@/services/api';
import { useState } from 'react';
import { useDeleteProduct } from '@/services/queries';
import { useRouter } from 'next/navigation';

interface ProductCardProps {
  product: Product;
  canEdit: boolean;
}

export default function ProductCard({ product, canEdit }: ProductCardProps) {
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const deleteProduct = useDeleteProduct();
  const router = useRouter();
  
  const handleDelete = async () => {
    try {
      await deleteProduct.mutateAsync(product.id);
      setIsDeleteModalOpen(false);
      // No need to navigate away, the list will update automatically
    } catch (error) {
      console.error('Failed to delete product:', error);
    }
  };
  
  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(price);
  };
  
  return (
    <>
      <div className="card hover:shadow-lg transition-shadow">
        <div className="flex justify-between items-start">
          <div>
            <h3 className="text-lg font-semibold text-gray-900 mb-1">
              {product.name}
            </h3>
            <p className="text-sm text-gray-500 mb-2">
              {product.category}
            </p>
          </div>
          <div className="text-lg font-bold text-primary-600">
            {formatPrice(product.price)}
          </div>
        </div>
        
        {product.description && (
          <p className="text-gray-700 mt-2 mb-4 line-clamp-2">
            {product.description}
          </p>
        )}
        
        {product.status && (
          <div className="mb-4">
            <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
              product.status === 'active' 
                ? 'bg-green-100 text-green-800' 
                : 'bg-red-100 text-red-800'
            }`}>
              {product.status === 'active' ? 'Active' : 'Discontinued'}
            </span>
          </div>
        )}
        
        {canEdit && (
          <div className="flex space-x-2 mt-4 pt-4 border-t border-gray-100">
            <Link 
              href={`/products/${product.id}/edit`}
              className="btn btn-secondary flex-1 text-center"
            >
              Edit
            </Link>
            <button 
              onClick={() => setIsDeleteModalOpen(true)}
              className="btn btn-danger flex-1"
            >
              Delete
            </button>
          </div>
        )}
      </div>
      
      {/* Delete confirmation modal */}
      {isDeleteModalOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg max-w-md w-full p-6">
            <h3 className="text-lg font-medium text-gray-900 mb-4">
              Delete Product
            </h3>
            <p className="text-gray-500 mb-6">
              Are you sure you want to delete "{product.name}"? This action cannot be undone.
            </p>
            <div className="flex justify-end space-x-3">
              <button 
                onClick={() => setIsDeleteModalOpen(false)}
                className="btn btn-secondary"
              >
                Cancel
              </button>
              <button 
                onClick={handleDelete}
                className="btn btn-danger"
                disabled={deleteProduct.isPending}
              >
                {deleteProduct.isPending ? 'Deleting...' : 'Delete'}
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}