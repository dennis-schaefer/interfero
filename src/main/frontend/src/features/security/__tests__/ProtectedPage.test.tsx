import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { render, screen, act } from '@testing-library/react';
import { ProtectedPage } from '../ProtectedPage';
import { AuthContext, type AuthContextType } from '../AuthContext';
import type { ReactNode } from 'react';

// Mock for Spinner component
vi.mock('@/components/ui/spinner.tsx', () => ({
  Spinner: ({ className }: { className?: string }) => (
    <div data-testid="spinner" className={className}>Loading...</div>
  ),
}));

describe('ProtectedPage', () => {
  beforeEach(() => {
    vi.useFakeTimers();
    // Mock window.location.href
    delete (window as any).location;
    window.location = { href: '' } as any;
  });

  afterEach(() => {
    vi.restoreAllMocks();
    vi.useRealTimers();
  });

  const createWrapper = (authValue: AuthContextType) => {
    return ({ children }: { children: ReactNode }) => (
      <AuthContext.Provider value={authValue}>
        {children}
      </AuthContext.Provider>
    );
  };

  describe('Authenticated User', () => {
    it('should display children when user is authenticated', () => {
      const mockAuthValue: AuthContextType = {
        accountInfo: {
          authenticated: true,
          username: 'testuser',
          roles: ['USER'],
        },
        loading: false,
        isAuthenticated: true,
        login: vi.fn(),
      };

      const wrapper = createWrapper(mockAuthValue);

      render(
        <ProtectedPage>
          <div data-testid="protected-content">Protected Content</div>
        </ProtectedPage>,
        { wrapper }
      );

      expect(screen.getByTestId('protected-content')).toBeInTheDocument();
      expect(screen.getByTestId('protected-content')).toHaveTextContent('Protected Content');
    });
  });

  describe('Loading State with Delay', () => {
    it('should NOT show spinner when loading takes less than 200ms', () => {
      const mockLogin = vi.fn();
      let authValue: AuthContextType = {
        accountInfo: undefined,
        loading: true,
        isAuthenticated: false,
        login: mockLogin,
      };

      function TestWrapper({ children }: { children: ReactNode }) {
        return (
          <AuthContext.Provider value={authValue}>
            {children}
          </AuthContext.Provider>
        );
      }

      const { rerender } = render(
        <TestWrapper>
          <ProtectedPage>
            <div data-testid="protected-content">Protected Content</div>
          </ProtectedPage>
        </TestWrapper>
      );

      // Spinner should not be displayed immediately
      expect(screen.queryByTestId('spinner')).not.toBeInTheDocument();

      // Advance timer by 100ms (less than 150ms)
      vi.advanceTimersByTime(100);

      // Spinner should still not be visible
      expect(screen.queryByTestId('spinner')).not.toBeInTheDocument();

      // Simulate fast loading - set loading to false
      authValue = {
        accountInfo: {
          authenticated: true,
          username: 'testuser',
          roles: ['USER'],
        },
        loading: false,
        isAuthenticated: true,
        login: mockLogin,
      };

      rerender(
        <TestWrapper>
          <ProtectedPage>
            <div data-testid="protected-content">Protected Content</div>
          </ProtectedPage>
        </TestWrapper>
      );

      // Spinner should never have been displayed
      expect(screen.queryByTestId('spinner')).not.toBeInTheDocument();

      // Content should now be visible
      expect(screen.getByTestId('protected-content')).toBeInTheDocument();
    });

    it('should show spinner when loading takes longer than 150ms', () => {
      const mockAuthValue: AuthContextType = {
        accountInfo: undefined,
        loading: true,
        isAuthenticated: false,
        login: vi.fn(),
      };

      const wrapper = createWrapper(mockAuthValue);

      render(
        <ProtectedPage>
          <div data-testid="protected-content">Protected Content</div>
        </ProtectedPage>,
        { wrapper }
      );

      // Spinner should not be displayed immediately
      expect(screen.queryByTestId('spinner')).not.toBeInTheDocument();

      // Advance timer by 150ms
      act(() => {
        vi.advanceTimersByTime(150);
      });

      // Spinner should now appear
      expect(screen.getByTestId('spinner')).toBeInTheDocument();
      expect(screen.queryByTestId('protected-content')).not.toBeInTheDocument();
    });

    it('should render spinner with correct CSS classes', () => {
      const mockAuthValue: AuthContextType = {
        accountInfo: undefined,
        loading: true,
        isAuthenticated: false,
        login: vi.fn(),
      };

      const wrapper = createWrapper(mockAuthValue);

      render(
        <ProtectedPage>
          <div data-testid="protected-content">Protected Content</div>
        </ProtectedPage>,
        { wrapper }
      );

      // Advance timer by 150ms
      act(() => {
        vi.advanceTimersByTime(150);
      });

      expect(screen.getByTestId('spinner')).toBeInTheDocument();

      const spinnerContainer = screen.getByTestId('spinner').parentElement;
      expect(spinnerContainer).toHaveClass('w-full', 'h-full', 'absolute', 'flex', 'justify-center', 'items-center');
    });

    it('should correctly cleanup timer when component unmounts', () => {
      const mockAuthValue: AuthContextType = {
        accountInfo: undefined,
        loading: true,
        isAuthenticated: false,
        login: vi.fn(),
      };

      const wrapper = createWrapper(mockAuthValue);

      const { unmount } = render(
        <ProtectedPage>
          <div data-testid="protected-content">Protected Content</div>
        </ProtectedPage>,
        { wrapper }
      );

      // Unmount before timer expires
      unmount();

      // Advance timer
      vi.advanceTimersByTime(150);

      // Spinner should not be in DOM
      expect(screen.queryByTestId('spinner')).not.toBeInTheDocument();
    });
  });

  describe('Unauthenticated User', () => {
    it('should call login() when user is not authenticated and not loading', () => {
      const mockLogin = vi.fn();
      const mockAuthValue: AuthContextType = {
        accountInfo: undefined,
        loading: false,
        isAuthenticated: false,
        login: mockLogin,
      };

      const wrapper = createWrapper(mockAuthValue);

      render(
        <ProtectedPage>
          <div data-testid="protected-content">Protected Content</div>
        </ProtectedPage>,
        { wrapper }
      );

      expect(mockLogin).toHaveBeenCalledTimes(1);
      expect(screen.queryByTestId('protected-content')).not.toBeInTheDocument();
    });

    it('should render nothing when login() is called', () => {
      const mockLogin = vi.fn();
      const mockAuthValue: AuthContextType = {
        accountInfo: undefined,
        loading: false,
        isAuthenticated: false,
        login: mockLogin,
      };

      const wrapper = createWrapper(mockAuthValue);

      const { container } = render(
        <ProtectedPage>
          <div data-testid="protected-content">Protected Content</div>
        </ProtectedPage>,
        { wrapper }
      );

      expect(mockLogin).toHaveBeenCalled();
      expect(container.firstChild).toBeNull();
    });
  });

  describe('State Transitions', () => {
    it('should correctly transition from Loading to Authenticated', () => {
      const mockLogin = vi.fn();
      let authValue: AuthContextType = {
        accountInfo: undefined,
        loading: true,
        isAuthenticated: false,
        login: mockLogin,
      };

      function TestWrapper({ children }: { children: ReactNode }) {
        return (
          <AuthContext.Provider value={authValue}>
            {children}
          </AuthContext.Provider>
        );
      }

      const { rerender } = render(
        <TestWrapper>
          <ProtectedPage>
            <div data-testid="protected-content">Protected Content</div>
          </ProtectedPage>
        </TestWrapper>
      );

      // Advance timer by 150ms - Spinner should appear
      act(() => {
        vi.advanceTimersByTime(150);
      });

      expect(screen.getByTestId('spinner')).toBeInTheDocument();

      // Simulate successful authentication
      authValue = {
        accountInfo: {
          authenticated: true,
          username: 'testuser',
          roles: ['USER'],
        },
        loading: false,
        isAuthenticated: true,
        login: mockLogin,
      };

      rerender(
        <TestWrapper>
          <ProtectedPage>
            <div data-testid="protected-content">Protected Content</div>
          </ProtectedPage>
        </TestWrapper>
      );

      // Spinner should disappear, content should appear
      expect(screen.queryByTestId('spinner')).not.toBeInTheDocument();
      expect(screen.getByTestId('protected-content')).toBeInTheDocument();
      expect(mockLogin).not.toHaveBeenCalled();
    });

    it('should correctly transition from Loading to Unauthenticated', () => {
      const mockLogin = vi.fn();
      let authValue: AuthContextType = {
        accountInfo: undefined,
        loading: true,
        isAuthenticated: false,
        login: mockLogin,
      };

      function TestWrapper({ children }: { children: ReactNode }) {
        return (
          <AuthContext.Provider value={authValue}>
            {children}
          </AuthContext.Provider>
        );
      }

      const { rerender } = render(
        <TestWrapper>
          <ProtectedPage>
            <div data-testid="protected-content">Protected Content</div>
          </ProtectedPage>
        </TestWrapper>
      );

      // Advance timer by 150ms - Spinner should appear
      act(() => {
        vi.advanceTimersByTime(150);
      });

      expect(screen.getByTestId('spinner')).toBeInTheDocument();

      // Simulate failed authentication
      authValue = {
        accountInfo: undefined,
        loading: false,
        isAuthenticated: false,
        login: mockLogin,
      };

      rerender(
        <TestWrapper>
          <ProtectedPage>
            <div data-testid="protected-content">Protected Content</div>
          </ProtectedPage>
        </TestWrapper>
      );

      // login() should be called (at least once)
      expect(mockLogin).toHaveBeenCalled();
      expect(screen.queryByTestId('spinner')).not.toBeInTheDocument();
      expect(screen.queryByTestId('protected-content')).not.toBeInTheDocument();
    });

    it('should hide spinner when loading switches from true to false before timer expires', () => {
      const mockLogin = vi.fn();
      let authValue: AuthContextType = {
        accountInfo: undefined,
        loading: true,
        isAuthenticated: false,
        login: mockLogin,
      };

      function TestWrapper({ children }: { children: ReactNode }) {
        return (
          <AuthContext.Provider value={authValue}>
            {children}
          </AuthContext.Provider>
        );
      }

      const { rerender } = render(
        <TestWrapper>
          <ProtectedPage>
            <div data-testid="protected-content">Protected Content</div>
          </ProtectedPage>
        </TestWrapper>
      );

      // Spinner should not be visible yet
      expect(screen.queryByTestId('spinner')).not.toBeInTheDocument();

      // Advance timer by 100ms (less than 150ms)
      vi.advanceTimersByTime(100);

      // End loading
      authValue = {
        accountInfo: {
          authenticated: true,
          username: 'testuser',
          roles: ['USER'],
        },
        loading: false,
        isAuthenticated: true,
        login: mockLogin,
      };

      rerender(
        <TestWrapper>
          <ProtectedPage>
            <div data-testid="protected-content">Protected Content</div>
          </ProtectedPage>
        </TestWrapper>
      );

      // Complete the timer
      vi.advanceTimersByTime(100);

      // Spinner should never have appeared
      expect(screen.queryByTestId('spinner')).not.toBeInTheDocument();
      expect(screen.getByTestId('protected-content')).toBeInTheDocument();
    });
  });

  describe('Edge Cases', () => {
    it('should handle multiple switches between Loading states', () => {
      const mockLogin = vi.fn();
      let authValue: AuthContextType = {
        accountInfo: undefined,
        loading: true,
        isAuthenticated: false,
        login: mockLogin,
      };

      function TestWrapper({ children }: { children: ReactNode }) {
        return (
          <AuthContext.Provider value={authValue}>
            {children}
          </AuthContext.Provider>
        );
      }

      const { rerender } = render(
        <TestWrapper>
          <ProtectedPage>
            <div data-testid="protected-content">Protected Content</div>
          </ProtectedPage>
        </TestWrapper>
      );

      // First loading cycle
      act(() => {
        vi.advanceTimersByTime(50);
      });

      // End loading
      authValue = {
        accountInfo: undefined,
        loading: false,
        isAuthenticated: false,
        login: mockLogin,
      };

      rerender(
        <TestWrapper>
          <ProtectedPage>
            <div data-testid="protected-content">Protected Content</div>
          </ProtectedPage>
        </TestWrapper>
      );

      // Start second loading cycle
      authValue = {
        accountInfo: undefined,
        loading: true,
        isAuthenticated: false,
        login: mockLogin,
      };

      rerender(
        <TestWrapper>
          <ProtectedPage>
            <div data-testid="protected-content">Protected Content</div>
          </ProtectedPage>
        </TestWrapper>
      );

      act(() => {
        vi.advanceTimersByTime(150);
      });

      // Spinner should now appear
      expect(screen.getByTestId('spinner')).toBeInTheDocument();
    });

    it('should correctly render children even when they are complex', () => {
      const mockAuthValue: AuthContextType = {
        accountInfo: {
          authenticated: true,
          username: 'testuser',
          roles: ['USER'],
        },
        loading: false,
        isAuthenticated: true,
        login: vi.fn(),
      };

      const wrapper = createWrapper(mockAuthValue);

      render(
        <ProtectedPage>
          <div data-testid="parent">
            <h1>Title</h1>
            <p>Paragraph</p>
            <div data-testid="nested">Nested Content</div>
          </div>
        </ProtectedPage>,
        { wrapper }
      );

      expect(screen.getByTestId('parent')).toBeInTheDocument();
      expect(screen.getByText('Title')).toBeInTheDocument();
      expect(screen.getByText('Paragraph')).toBeInTheDocument();
      expect(screen.getByTestId('nested')).toBeInTheDocument();
    });
  });
});

