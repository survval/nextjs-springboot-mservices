import Link from 'next/link';

export default function HomePage() {
  return (
    <div className="max-w-7xl mx-auto">
      <div className="text-center py-12 px-4 sm:px-6 lg:py-16 lg:px-8">
        <h1 className="text-4xl font-extrabold tracking-tight text-gray-900 sm:text-5xl">
          <span className="block">Product Management</span>
          <span className="block text-primary-600">Multi-tenant Application</span>
        </h1>
        <p className="mt-4 max-w-2xl mx-auto text-xl text-gray-500">
          Manage your products across multiple tenants with our easy-to-use platform.
        </p>
        <div className="mt-8 flex justify-center">
          <Link
            href="/products"
            className="btn btn-primary text-lg px-6 py-3"
          >
            View Products
          </Link>
        </div>
      </div>
      
      <div className="py-12">
        <div className="grid grid-cols-1 gap-8 md:grid-cols-3">
          <div className="card">
            <h3 className="text-lg font-medium text-gray-900">Multi-tenant</h3>
            <p className="mt-2 text-gray-600">
              Automatically detects tenant from subdomain or route prefix and sets the context for all requests.
            </p>
          </div>
          
          <div className="card">
            <h3 className="text-lg font-medium text-gray-900">Responsive Design</h3>
            <p className="mt-2 text-gray-600">
              Works seamlessly across all devices - desktop, tablet, and mobile.
            </p>
          </div>
          
          <div className="card">
            <h3 className="text-lg font-medium text-gray-900">Secure Authentication</h3>
            <p className="mt-2 text-gray-600">
              Integrated with Keycloak for secure authentication and authorization.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}