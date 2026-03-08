
import { SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar"
import { AppSidebar } from "@/components/AppSidebar"
import { Outlet } from "react-router-dom"
import { ProtectedPage } from "@/features/security/ProtectedPage"

export default function MainLayout() {
    return (
        <ProtectedPage>
            <SidebarProvider>
                <AppSidebar />
                <main className="w-full">
                    <SidebarTrigger />
                    <div className="p-4">
                        <Outlet />
                    </div>
                </main>
            </SidebarProvider>
        </ProtectedPage>
    )
}
