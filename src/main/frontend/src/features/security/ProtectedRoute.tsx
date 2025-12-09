import type { ReactNode } from 'react';
import { useAuth } from './AuthContext';
import {Spinner} from "../../components/ui/spinner.tsx";

interface ProtectedRouteProps {
    children: ReactNode;
}

export function ProtectedRoute({ children }: ProtectedRouteProps) {
    const { isAuthenticated, loading, login } = useAuth();

    if (loading) {
        return (
            <div className={"w-full h-full absolute flex justify-center items-center"}>
                <Spinner className={"size-10"} />
            </div>
        );
    }

    if (!isAuthenticated) {
        login();
        return null;
    }

    return <>{children}</>;
}

