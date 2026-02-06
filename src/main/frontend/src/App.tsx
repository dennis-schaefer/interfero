import {createBrowserRouter, RouterProvider} from "react-router-dom";
import DashboardPage from "./pages/DashboardPage.tsx";
import {AuthProvider} from "@/features/security/AuthProvider.tsx";
import {ProtectedPage} from "@/features/security/ProtectedPage.tsx";
import LoginPage from "@/pages/LoginPage.tsx";
import {useEffect} from "react";
import axios from "axios";
import SetupLayout from "@/layouts/SetupLayout.tsx";
import ClusterSetupPage from "@/pages/ClusterSetupPage.tsx";

const router = createBrowserRouter([
    {
        path: "/",
        Component: SetupLayout,
        children: [
            {
                index: true,
                element: <ProtectedPage><ClusterSetupPage/></ProtectedPage>
            },
            {
                path: "login",
                Component: LoginPage
            },
        ]
    },
    {
        path: "/dashboard",
        children: [
            {
                index: true,
                element: <ProtectedPage><DashboardPage /></ProtectedPage>
            }
        ]
    },

]);

export default function App() {
    useEffect(() => {
        // To retrieve CSRF token cookie
        axios.get("/api/csrf").then();
    }, []);

    return (
        <AuthProvider>
            <RouterProvider router={router}/>
        </AuthProvider>
    )
}

