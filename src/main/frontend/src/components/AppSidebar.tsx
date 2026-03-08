
import {
    Sidebar,
    SidebarContent,
    SidebarFooter,
    SidebarGroup,
    SidebarHeader,
} from "@/components/ui/sidebar"
import { ClusterSelector } from "@/features/clusters/ClusterSelector.tsx"
import { NavAccount } from "@/features/security/NavAccount.tsx"

export function AppSidebar() {
    return (
        <Sidebar collapsible={"icon"}>
            <SidebarHeader>
                <ClusterSelector />
            </SidebarHeader>
            <SidebarContent>
                <SidebarGroup />
                <SidebarGroup />
            </SidebarContent>
            <SidebarFooter>
                <NavAccount />
            </SidebarFooter>
        </Sidebar>
    )
}
