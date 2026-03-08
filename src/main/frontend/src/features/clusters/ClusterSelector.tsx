
import { useGetAllClusterInfo } from "@/api/endpoints/cluster-controller.ts";
import { useClusterStore } from "@/stores/useClusterStore.ts";
import ClusterImage from "@/features/clusters/ClusterImage.tsx";
import { SidebarMenu, SidebarMenuButton, SidebarMenuItem } from "@/components/ui/sidebar.tsx";
import { useSidebar } from "@/components/ui/sidebar.tsx";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuGroup,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu.tsx";
import { useEffect } from "react";
import {Check, ChevronsUpDown, Plus} from "lucide-react";
import { useNavigate } from "react-router";

export function ClusterSelector() {
    const { data: clusters, isLoading } = useGetAllClusterInfo();
    const { selectedClusterId, setSelectedClusterId } = useClusterStore();
    const { isMobile } = useSidebar();
    const navigate = useNavigate();

    useEffect(() => {
        if (!isLoading && clusters && clusters.length > 0 && !selectedClusterId) {
            if (clusters[0].clusterId) {
                setSelectedClusterId(clusters[0].clusterId);
            }
        }
    }, [clusters, isLoading, selectedClusterId, setSelectedClusterId]);

    const selectedCluster = clusters?.find(c => c.clusterId === selectedClusterId);

    if (isLoading) {
        return <div className="h-12 w-full animate-pulse rounded-md" />
    }

    return (
        <SidebarMenu>
            <SidebarMenuItem>
                <DropdownMenu>
                    <DropdownMenuTrigger className={"w-full"}>
                        <SidebarMenuButton
                            size="lg"

                        >
                            {selectedCluster && (
                                <>
                                    <div className="flex aspect-square size-8">
                                        <ClusterImage
                                            icon={selectedCluster.icon}
                                            color={selectedCluster.color}
                                            className="size-8"
                                        />
                                    </div>
                                    <div className="grid flex-1 text-left text-sm leading-tight">
                                        <span className="truncate font-medium">{selectedCluster.displayName}</span>
                                        <span className="truncate text-xs text-muted-foreground">{selectedCluster.internalName}</span>
                                    </div>
                                </>
                            )}
                            <ChevronsUpDown className="ml-auto" />
                        </SidebarMenuButton>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent
                        className="min-w-56 rounded-lg"
                        side={isMobile ? "bottom" : "right"}
                        align="start"
                        sideOffset={4}
                    >
                        <DropdownMenuGroup>
                            <DropdownMenuLabel className="text-muted-foreground text-xs">
                                Clusters
                            </DropdownMenuLabel>
                            {clusters?.map((cluster) => (
                                <DropdownMenuItem
                                    key={cluster.clusterId}
                                    onClick={() => cluster.clusterId && setSelectedClusterId(cluster.clusterId)}
                                    className="gap-3 p-2"
                                >
                                    <div className="flex size-7.5 shrink-0 items-center justify-center">
                                        <ClusterImage
                                            icon={cluster.icon}
                                            color={cluster.color}
                                            className="size-7.5"
                                        />
                                    </div>
                                    <div className="grid flex-1 text-left text-sm leading-tight">
                                        <span className="truncate font-medium">{cluster.displayName}</span>
                                        <span className="truncate text-xs text-muted-foreground">{cluster.internalName}</span>
                                    </div>
                                    {selectedClusterId === cluster.clusterId && (
                                        <Check/>
                                    )}
                                </DropdownMenuItem>
                            ))}
                        </DropdownMenuGroup>
                        <DropdownMenuSeparator />
                        <DropdownMenuGroup>
                            <DropdownMenuItem
                                className="gap-2 p-2 text-muted-foreground"
                                onClick={() => navigate("/clusters/create")}
                            >
                                <Plus className="size-4" />
                                <span className=" font-medium">Add Cluster</span>
                            </DropdownMenuItem>
                        </DropdownMenuGroup>
                    </DropdownMenuContent>
                </DropdownMenu>
            </SidebarMenuItem>
        </SidebarMenu>
    );
}
