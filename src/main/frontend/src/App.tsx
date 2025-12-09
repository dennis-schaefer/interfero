import {createBrowserRouter, RouterProvider} from "react-router-dom";
import DashboardView from "./pages/DashboardPage.tsx";
import LoginPage from "./pages/LoginPage.tsx";
import {AuthProvider} from "./features/security/AuthProvider.tsx";
import {ProtectedRoute} from "./features/security/ProtectedRoute.tsx";

const router = createBrowserRouter([
    {
        path: "/",
        element: <ProtectedRoute><DashboardView/></ProtectedRoute>,
    },
    {
        path: "/login",
        element: <LoginPage/>
    }
]);

export default function App() {
    return (
        <AuthProvider>
            <RouterProvider router={router}/>
        </AuthProvider>
    )
}

