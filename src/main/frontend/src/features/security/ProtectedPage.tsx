import type { ReactNode } from 'react';
import { useEffect, useState } from 'react';
import { useAuth } from './AuthContext';
import {Spinner} from "@/components/ui/spinner.tsx";

interface ProtectedPageProps {
    children: ReactNode;
}

export function ProtectedPage({ children }: ProtectedPageProps) {
    const { isAuthenticated, loading, login } = useAuth();
    const [showSpinner, setShowSpinner] = useState(false);

    useEffect(() => {
        if (!loading)
        {
            setShowSpinner(false);
            return;
        }

        const timer = setTimeout(() => {
            setShowSpinner(true);
        }, 150);

        return () => {
            clearTimeout(timer);
            setShowSpinner(false);
        };
    }, [loading]);

    if (isAuthenticated)
        return <>{children}</>;

    if (showSpinner && loading) {
        return (
            <div className={"w-full h-full absolute flex justify-center items-center"}>
                <Spinner className={"size-10"} />
            </div>
        );
    }

    if (!isAuthenticated && !loading) {
        login();
        return null;
    }
}