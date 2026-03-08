
import {
    Sidebar,
    SidebarContent,
    SidebarFooter,
    SidebarGroup,
    SidebarHeader,
} from "@/components/ui/sidebar"
import { ClusterSelector } from "@/features/clusters/ClusterSelector.tsx"

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
            <SidebarFooter />
        </Sidebar>
    )
}
