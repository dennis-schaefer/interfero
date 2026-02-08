import type { ClusterInfo } from "@/api/schemas";
import { useEffect, useState } from "react";
import { Field, FieldLabel } from "@/components/ui/field.tsx";
import { Input } from "@/components/ui/input.tsx";
import ClusterImage from "@/features/clusters/ClusterImage.tsx";

interface ClusterInfoFormProps {
    initialClusterInfo?: ClusterInfo,
    onClusterInfoChanged: (clusterInfo: ClusterInfo | null) => void,
    disabled?: boolean
}

const emptyClusterInfo: ClusterInfo = {
    displayName: '',
    icon: 'box',
    color: '#009869'
}

export default function ClusterInfoForm({ initialClusterInfo, onClusterInfoChanged, disabled }: ClusterInfoFormProps) {

    const [clusterInfo, setClusterInfo] = useState<ClusterInfo>(initialClusterInfo || emptyClusterInfo);

    const handleDisplayNameChange = (displayName: string) => {
        setClusterInfo(prev => {
            return {
                ...prev,
                displayName
            }
        });
    }

    const handleIconChange = (icon: string) => {
        setClusterInfo(prev => {
            return {
                ...prev,
                icon
            }
        });
    }

    const handleColorChange = (color: string) => {
        setClusterInfo(prev => {
            return {
                ...prev,
                color
            }
        });
    }

    useEffect(() => {
        onClusterInfoChanged(clusterInfo.displayName ? clusterInfo : null);
    }, [clusterInfo]);

    return (
        <>
            <div>
                <h2 className="text-xl font-semibold">Cluster Information</h2>
                <p className="mb-4 text-sm text-secondary-foreground">Give your cluster a representative name and select an icon and color!</p>
            </div>
            <div className={"flex flex-row gap-4 items-center"}>
                <ClusterImage icon={clusterInfo.icon}
                    color={clusterInfo.color}
                    className={"size-20"}
                    editable={!disabled}
                    onIconChange={handleIconChange}
                    onColorChange={handleColorChange} />

                <Field>
                    <FieldLabel htmlFor={"displayName"}>Name of your Cluster</FieldLabel>
                    <Input id={"displayName"}
                        placeholder={"e.g. Cluster Prod (EU)"}
                        defaultValue={clusterInfo.displayName || ''}
                        value={clusterInfo.displayName}
                        disabled={disabled}
                        onChange={e => handleDisplayNameChange(e.target.value)} />
                </Field>
            </div>
        </>

    )
}