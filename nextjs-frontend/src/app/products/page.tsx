'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useProducts, useCategories } from '@/services/queries';
import { ProductFilter } from '@/services/api';
import { useAuthContext } from '@/components/auth/AuthProvider';
import ProductCard from '@/components/products/ProductCard';
import FilterPanel from '@/components/products/FilterPanel';

export default function ProductsPage() {
  const [filters, setFilters] = useState<ProductFilter>({
    page: 0,
    size: 10,
    sort: 'createdAt,desc'
  });
  
  const { data, isLoading, error } = useProducts(filters);
  const { data: categories } = useCategories();
  const { hasRole, isAuthenticated } = useAuthContext();
  
  const handleFilterChange = (newFilters: Partial<ProductFilter>) => {
    setFilters(prev => ({ ...prev, ...newFilters, page: 0 })); // Reset to first page on filter change
  };
  
  const handlePageChange = (newPage: number) => {
    setFilters(prev => ({ ...prev, page: newPage }));
  };
  
  if (error) {
    return (
      <div className="text-center py-10">
        <h2 className="text-red-600">Error loading products</h2>
        <p className="text-gray-600 mt-2">{(error as Error).message}</p>
      </div>
    );
  }
  
  return (
    <div className="max-w-7xl mx-auto">
      <div className="flex flex-col md:flex-row md:items-center md:justify-between mb-6">
        <h1>Products</h1>
        
        {isAuthenticated && hasRole('PRODUCT_ADMIN') && (
          <Link href="/products/new" className="btn btn-primary mt-4 md:mt-0">
            Add New Product
          </Link>
        )}
      </div>
      
      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
        {/* Filters sidebar */}
        <div className="lg:col-span-1">
          <FilterPanel 
            filters={filters} 
            onFilterChange={handleFilterChange}
            categories={categories || []}
          />
        </div>
        
        {/* Products grid */}
        <div className="lg:col-span-3">
          {isLoading ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {Array.from({ length: 6 }).map((_, i) => (
                <div key={i} className="card animate-pulse">
                  <div className="h-40 bg-gray-200 rounded mb-4"></div>
                  <div className="h-6 bg-gray-200 rounded w-3/4 mb-2"></div>
                  <div className="h-4 bg-gray-200 rounded w-1/2"></div>
                </div>
              ))}
            </div>
          ) : data?.content.length === 0 ? (
            <div className="text-center py-10">
              <h2 className="text-xl text-gray-600">No products found</h2>
              <p className="text-gray-500 mt-2">Try adjusting your filters</p>
            </div>
          ) : (
            <>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {data?.content.map(product => (
                  <ProductCard 
                    key={product.id} 
                    product={product} 
                    canEdit={isAuthenticated && hasRole('PRODUCT_ADMIN')}
                  />
                ))}
              </div>
              
              {/* Pagination */}
              {data && data.totalPages > 1 && (
                <div className="flex justify-center mt-8">
                  <nav className="flex items-center">
                    <button
                      onClick={() => handlePageChange(data.number - 1)}
                      disabled={data.number === 0}
                      className="px-3 py-1 rounded-md mr-2 disabled:opacity-50 disabled:cursor-not-allowed bg-gray-200"
                    >
                      Previous
                    </button>
                    
                    <div className="flex space-x-1">
                      {Array.from({ length: data.totalPages }, (_, i) => (
                        <button
                          key={i}
                          onClick={() => handlePageChange(i)}
                          className={`px-3 py-1 rounded-md ${
                            i === data.number
                              ? 'bg-primary-600 text-white'
                              : 'bg-gray-200'
                          }`}
                        >
                          {i + 1}
                        </button>
                      ))}
                    </div>
                    
                    <button
                      onClick={() => handlePageChange(data.number + 1)}
                      disabled={data.number === data.totalPages - 1}
                      className="px-3 py-1 rounded-md ml-2 disabled:opacity-50 disabled:cursor-not-allowed bg-gray-200"
                    >
                      Next
                    </button>
                  </nav>
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
}