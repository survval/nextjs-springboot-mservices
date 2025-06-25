import { Metadata } from 'next';
import '../styles/globals.css';
import { QueryProvider, queryClient } from '@/services/queries';
import { AuthProvider } from '@/components/auth/AuthProvider';
import Header from '@/components/layout/Header';

export const metadata: Metadata = {
  title: 'Product Management App',
  description: 'Multi-tenant product management application',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <body>
        <QueryProvider client={queryClient}>
          <AuthProvider>
            <div className="flex flex-col min-h-screen">
              <Header />
              <main className="flex-grow p-4 md:p-6 bg-gray-50">
                {children}
              </main>
              <footer className="py-4 text-center text-sm text-gray-500 border-t">
                &copy; {new Date().getFullYear()} Product Management App
              </footer>
            </div>
          </AuthProvider>
        </QueryProvider>
      </body>
    </html>
  );
}
