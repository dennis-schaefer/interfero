import { ChevronDown, LogOut } from "lucide-react";
import { useState } from "react";
import { useAuth } from "@/features/security/AuthContext.tsx";
import { useSidebar } from "@/components/ui/sidebar.tsx";
import { Avatar, AvatarFallback } from "@/components/ui/avatar.tsx";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover.tsx";
import {
    SidebarMenu,
    SidebarMenuButton,
    SidebarMenuItem,
} from "@/components/ui/sidebar.tsx";

export function NavAccount() {
    const { accountInfo } = useAuth();
    const { state } = useSidebar();
    const isCollapsed = state === "collapsed";
    const [open, setOpen] = useState(false);

    const username = accountInfo?.username ?? "Unknown";
    const roles = accountInfo?.roles?.join(", ") ?? "";
    const initials = username.slice(0, 2).toUpperCase();

    return (
        <SidebarMenu>
            <SidebarMenuItem>
                <Popover open={open} onOpenChange={setOpen}>
                    <PopoverTrigger className="w-full">
                        <SidebarMenuButton
                            size="lg"
                            tooltip={username}
                        >
                            <Avatar className="h-8 w-8">
                                <AvatarFallback>{initials}</AvatarFallback>
                            </Avatar>
                            {!isCollapsed && (
                                <>
                                    <div className="flex flex-col items-start truncate text-left min-w-0 flex-1">
                                        <span className="font-medium truncate text-sm w-full">{username}</span>
                                        <span className="text-[0.65rem] text-muted-foreground truncate w-full">{roles}</span>
                                    </div>
                                    <ChevronDown className={`ml-auto h-4 w-4 text-muted-foreground transition-transform duration-200 ${open ? "-rotate-90" : ""}`} />
                                </>
                            )}
                        </SidebarMenuButton>
                    </PopoverTrigger>
                    <PopoverContent side="right" align="end" className="w-48 p-1">
                        <div
                            className="flex items-center gap-2 px-2 py-1.5 text-sm cursor-default hover:bg-accent hover:text-accent-foreground rounded-sm"
                            onClick={() => { window.location.href = "/logout"; }}
                        >
                            <LogOut className="h-4 w-4" />
                            <span>Logout</span>
                        </div>
                    </PopoverContent>
                </Popover>
            </SidebarMenuItem>
        </SidebarMenu>
    );
}
