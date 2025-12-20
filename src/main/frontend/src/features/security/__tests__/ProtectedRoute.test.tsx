import { render, screen } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { ProtectedRoute } from '../ProtectedRoute.tsx';
import { AuthContext, type AuthContextType } from '../AuthContext.tsx';

// Mock the Spinner component
vi.mock('../../../components/ui/spinner.tsx', () => ({
    Spinner: ({ className }: { className?: string }) => (
        <div data-testid="spinner" className={className}>Loading...</div>
    ),
}));

describe('ProtectedRoute', () => {
    const mockLogin = vi.fn();

    beforeEach(() => {
        vi.clearAllMocks();
    });

    it('should render spinner while loading', () => {
        const contextValue: AuthContextType = {
            accountInfo: undefined,
            loading: true,
            isAuthenticated: false,
            login: mockLogin,
        };

        render(
            <AuthContext.Provider value={contextValue}>
                <ProtectedRoute>
                    <div data-testid="protected-content">Protected Content</div>
                </ProtectedRoute>
            </AuthContext.Provider>
        );

        expect(screen.getByTestId('spinner')).toBeInTheDocument();
        expect(screen.queryByTestId('protected-content')).not.toBeInTheDocument();
        expect(mockLogin).not.toHaveBeenCalled();
    });

    it('should call login and render nothing when not authenticated', () => {
        const contextValue: AuthContextType = {
            accountInfo: undefined,
            loading: false,
            isAuthenticated: false,
            login: mockLogin,
        };

        const { container } = render(
            <AuthContext.Provider value={contextValue}>
                <ProtectedRoute>
                    <div data-testid="protected-content">Protected Content</div>
                </ProtectedRoute>
            </AuthContext.Provider>
        );

        expect(mockLogin).toHaveBeenCalledTimes(1);
        expect(screen.queryByTestId('protected-content')).not.toBeInTheDocument();
        expect(container.innerHTML).toBe('');
    });

    it('should render children when authenticated', () => {
        const contextValue: AuthContextType = {
            accountInfo: { username: 'testuser', roles: ['USER'] },
            loading: false,
            isAuthenticated: true,
            login: mockLogin,
        };

        render(
            <AuthContext.Provider value={contextValue}>
                <ProtectedRoute>
                    <div data-testid="protected-content">Protected Content</div>
                </ProtectedRoute>
            </AuthContext.Provider>
        );

        expect(screen.getByTestId('protected-content')).toBeInTheDocument();
        expect(screen.getByText('Protected Content')).toBeInTheDocument();
        expect(mockLogin).not.toHaveBeenCalled();
    });

    it('should not call login while still loading even if not authenticated', () => {
        const contextValue: AuthContextType = {
            accountInfo: undefined,
            loading: true,
            isAuthenticated: false,
            login: mockLogin,
        };

        render(
            <AuthContext.Provider value={contextValue}>
                <ProtectedRoute>
                    <div data-testid="protected-content">Protected Content</div>
                </ProtectedRoute>
            </AuthContext.Provider>
        );

        expect(mockLogin).not.toHaveBeenCalled();
        expect(screen.getByTestId('spinner')).toBeInTheDocument();
    });

    it('should render complex children correctly when authenticated', () => {
        const contextValue: AuthContextType = {
            accountInfo: { username: 'admin', roles: ['ADMIN'] },
            loading: false,
            isAuthenticated: true,
            login: mockLogin,
        };

        render(
            <AuthContext.Provider value={contextValue}>
                <ProtectedRoute>
                    <div data-testid="parent">
                        <h1>Dashboard</h1>
                        <p>Welcome, admin!</p>
                        <nav data-testid="navigation">
                            <a href="/settings">Settings</a>
                        </nav>
                    </div>
                </ProtectedRoute>
            </AuthContext.Provider>
        );

        expect(screen.getByTestId('parent')).toBeInTheDocument();
        expect(screen.getByText('Dashboard')).toBeInTheDocument();
        expect(screen.getByText('Welcome, admin!')).toBeInTheDocument();
        expect(screen.getByTestId('navigation')).toBeInTheDocument();
    });

    it('should handle transition from loading to authenticated', () => {
        const contextValueLoading: AuthContextType = {
            accountInfo: undefined,
            loading: true,
            isAuthenticated: false,
            login: mockLogin,
        };

        const { rerender } = render(
            <AuthContext.Provider value={contextValueLoading}>
                <ProtectedRoute>
                    <div data-testid="protected-content">Protected Content</div>
                </ProtectedRoute>
            </AuthContext.Provider>
        );

        expect(screen.getByTestId('spinner')).toBeInTheDocument();
        expect(screen.queryByTestId('protected-content')).not.toBeInTheDocument();

        const contextValueAuthenticated: AuthContextType = {
            accountInfo: { username: 'testuser', roles: ['USER'] },
            loading: false,
            isAuthenticated: true,
            login: mockLogin,
        };

        rerender(
            <AuthContext.Provider value={contextValueAuthenticated}>
                <ProtectedRoute>
                    <div data-testid="protected-content">Protected Content</div>
                </ProtectedRoute>
            </AuthContext.Provider>
        );

        expect(screen.queryByTestId('spinner')).not.toBeInTheDocument();
        expect(screen.getByTestId('protected-content')).toBeInTheDocument();
    });

    it('should handle transition from loading to unauthenticated', () => {
        const contextValueLoading: AuthContextType = {
            accountInfo: undefined,
            loading: true,
            isAuthenticated: false,
            login: mockLogin,
        };

        const { rerender, container } = render(
            <AuthContext.Provider value={contextValueLoading}>
                <ProtectedRoute>
                    <div data-testid="protected-content">Protected Content</div>
                </ProtectedRoute>
            </AuthContext.Provider>
        );

        expect(screen.getByTestId('spinner')).toBeInTheDocument();
        expect(mockLogin).not.toHaveBeenCalled();

        const contextValueUnauthenticated: AuthContextType = {
            accountInfo: undefined,
            loading: false,
            isAuthenticated: false,
            login: mockLogin,
        };

        rerender(
            <AuthContext.Provider value={contextValueUnauthenticated}>
                <ProtectedRoute>
                    <div data-testid="protected-content">Protected Content</div>
                </ProtectedRoute>
            </AuthContext.Provider>
        );

        expect(mockLogin).toHaveBeenCalledTimes(1);
        expect(container.innerHTML).toBe('');
    });
});

