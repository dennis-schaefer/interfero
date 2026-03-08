import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface ClusterState {
    selectedClusterId: string | null;
    setSelectedClusterId: (id: string | null) => void;
}

export const useClusterStore = create<ClusterState>()(
    persist(
        (set) => ({
            selectedClusterId: null,
            setSelectedClusterId: (id) => set({ selectedClusterId: id }),
        }),
        {
            name: 'cluster-storage',
        }
    )
);
