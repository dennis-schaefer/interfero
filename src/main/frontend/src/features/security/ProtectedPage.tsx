import type { ReactNode } from 'react';
import { useAuth } from './AuthContext';
import {Spinner} from "@/components/ui/spinner.tsx";

interface ProtectedPageProps {
    children: ReactNode;
}

export function ProtectedPage({ children }: ProtectedPageProps) {
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