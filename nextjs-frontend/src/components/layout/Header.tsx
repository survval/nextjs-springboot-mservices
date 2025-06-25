'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useState } from 'react';
import { useAuthContext } from '@/components/auth/AuthProvider';
import { getTenant } from '@/services/auth';

export default function Header() {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const { user, login, logout, isAuthenticated, loading } = useAuthContext();
  const pathname = usePathname();
  const tenant = getTenant();
  
  const toggleMenu = () => setIsMenuOpen(!isMenuOpen);
  const closeMenu = () => setIsMenuOpen(false);
  
  const isActive = (path: string) => {
    return pathname === path || pathname?.startsWith(`${path}/`);
  };
  
  return (
    <header className="bg-white shadow-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          {/* Logo and main nav */}
          <div className="flex">
            <div className="flex-shrink-0 flex items-center">
              <Link href="/" className="text-xl font-bold text-primary-600">
                Product Management
                {tenant !== 'default' && (
                  <span className="ml-2 text-sm bg-primary-100 text-primary-800 py-1 px-2 rounded-md">
                    {tenant}
                  </span>
                )}
              </Link>
            </div>
            
            {/* Desktop navigation */}
            <nav className="hidden md:ml-6 md:flex md:space-x-4">
              <Link 
                href="/products" 
                className={`px-3 py-2 rounded-md text-sm font-medium ${
                  isActive('/products') 
                    ? 'bg-primary-100 text-primary-800' 
                    : 'text-gray-600 hover:bg-gray-100'
                }`}
              >
                Products
              </Link>
            </nav>
          </div>
          
          {/* User menu */}
          <div className="hidden md:flex items-center">
            {loading ? (
              <div className="animate-pulse h-8 w-20 bg-gray-200 rounded"></div>
            ) : isAuthenticated ? (
              <div className="flex items-center space-x-4">
                <span className="text-sm text-gray-700">{user?.username}</span>
                <button 
                  onClick={logout}
                  className="px-3 py-2 rounded-md text-sm font-medium text-gray-600 hover:bg-gray-100"
                >
                  Logout
                </button>
              </div>
            ) : (
              <button 
                onClick={login}
                className="btn btn-primary"
              >
                Login
              </button>
            )}
          </div>
          
          {/* Mobile menu button */}
          <div className="flex items-center md:hidden">
            <button
              onClick={toggleMenu}
              className="inline-flex items-center justify-center p-2 rounded-md text-gray-600 hover:text-gray-900 hover:bg-gray-100 focus:outline-none"
              aria-expanded={isMenuOpen}
            >
              <span className="sr-only">Open main menu</span>
              {/* Icon for menu */}
              <svg
                className={`${isMenuOpen ? 'hidden' : 'block'} h-6 w-6`}
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
                aria-hidden="true"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M4 6h16M4 12h16M4 18h16"
                />
              </svg>
              {/* Icon for close */}
              <svg
                className={`${isMenuOpen ? 'block' : 'hidden'} h-6 w-6`}
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
                aria-hidden="true"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M6 18L18 6M6 6l12 12"
                />
              </svg>
            </button>
          </div>
        </div>
      </div>
      
      {/* Mobile menu */}
      <div className={`${isMenuOpen ? 'block' : 'hidden'} md:hidden`}>
        <div className="px-2 pt-2 pb-3 space-y-1 sm:px-3">
          <Link
            href="/products"
            className={`block px-3 py-2 rounded-md text-base font-medium ${
              isActive('/products')
                ? 'bg-primary-100 text-primary-800'
                : 'text-gray-600 hover:bg-gray-100'
            }`}
            onClick={closeMenu}
          >
            Products
          </Link>
        </div>
        <div className="pt-4 pb-3 border-t border-gray-200">
          <div className="px-2 space-y-1">
            {loading ? (
              <div className="animate-pulse h-8 w-20 bg-gray-200 rounded"></div>
            ) : isAuthenticated ? (
              <>
                <div className="px-3 py-2 text-base font-medium text-gray-700">
                  {user?.username}
                </div>
                <button
                  onClick={() => {
                    logout();
                    closeMenu();
                  }}
                  className="block w-full text-left px-3 py-2 rounded-md text-base font-medium text-gray-600 hover:bg-gray-100"
                >
                  Logout
                </button>
              </>
            ) : (
              <button
                onClick={() => {
                  login();
                  closeMenu();
                }}
                className="block w-full text-left px-3 py-2 rounded-md text-base font-medium bg-primary-600 text-white hover:bg-primary-700"
              >
                Login
              </button>
            )}
          </div>
        </div>
      </div>
    </header>
  );
}