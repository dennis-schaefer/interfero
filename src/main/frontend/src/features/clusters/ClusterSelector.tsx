
import { useGetAllClusterInfo } from "@/api/endpoints/cluster-controller.ts";
import { useClusterStore } from "@/stores/useClusterStore.ts";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select.tsx";
import ClusterImage from "@/features/clusters/ClusterImage.tsx";
import { useSidebar } from "@/components/ui/sidebar.tsx";
import { useEffect } from "react";

export function ClusterSelector() {
    const { data: clusters, isLoading } = useGetAllClusterInfo();
    const { selectedClusterId, setSelectedClusterId } = useClusterStore();
    const { state } = useSidebar();
    const isCollapsed = state === "collapsed";

    useEffect(() => {
        if (!isLoading && clusters && clusters.length > 0 && !selectedClusterId) {
            if (clusters[0].clusterId) {
                setSelectedClusterId(clusters[0].clusterId);
            }
        }
    }, [clusters, isLoading, selectedClusterId, setSelectedClusterId]);

    const selectedCluster = clusters?.find(c => c.clusterId === selectedClusterId);

    if (isLoading) {
        return <div className="h-12 w-full animate-pulse bg-sidebar-accent/10 rounded-md" />
    }

    return (
        <Select value={selectedClusterId || ""} onValueChange={setSelectedClusterId}>
            <SelectTrigger className={`h-12 border-none bg-sidebar-accent/10 hover:bg-sidebar-accent/20 focus:ring-0 [&>svg]:transition-transform [&>svg]:duration-200 data-[popup-open]:[&>svg]:-rotate-90 ${isCollapsed ? "w-8 p-0 justify-center [&>[data-slot=select-value]]:contents [&_svg.text-muted-foreground]:hidden" : "w-full px-2"}`}>
                <SelectValue placeholder="Select Cluster">
                    {selectedCluster && (
                        <div className="flex items-center gap-2 overflow-hidden">
                            <div className="h-8 w-8 flex-shrink-0">
                                <ClusterImage
                                    icon={selectedCluster.icon}
                                    color={selectedCluster.color}
                                    className="text-sm p-1"
                                />
                            </div>
                            {!isCollapsed && (
                                <div className="flex flex-col items-start truncate text-left min-w-0 flex-1">
                                    <span className="font-medium truncate text-sm w-full">{selectedCluster.displayName}</span>
                                    <span className="text-xs text-muted-foreground truncate w-full">{selectedCluster.internalName}</span>
                                </div>
                            )}
                        </div>
                    )}
                </SelectValue>
            </SelectTrigger>
            <SelectContent className="min-w-48" side="right" alignItemWithTrigger={false}>
                {clusters?.map((cluster) => (
                    <SelectItem key={cluster.clusterId} value={cluster.clusterId || "unknown"} className={cluster.clusterId === selectedClusterId ? "bg-accent" : ""}>
                        <div className="flex items-center gap-2">
                            <div className="h-8 w-8 flex-shrink-0">
                                <ClusterImage
                                    icon={cluster.icon}
                                    color={cluster.color}
                                    className="text-sm p-1"
                                />
                            </div>
                            <div className="flex flex-col items-start text-left">
                                <span className="font-medium text-sm">{cluster.displayName}</span>
                                <span className="text-xs text-muted-foreground">{cluster.internalName}</span>
                            </div>
                        </div>
                    </SelectItem>
                ))}
            </SelectContent>
        </Select>
    );
}