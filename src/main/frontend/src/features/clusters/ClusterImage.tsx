import {AspectRatio} from "@/components/ui/aspect-ratio.tsx";
import {cn} from "@/lib/utils.ts";
import {AppWindow, Atom, Box} from "lucide-react";
import type {JSX} from "react";

interface ClusterImageProps {
    icon: string,
    color: string
    className?: string
}

const iconClassName = "h-[70%] w-[70%] text-white";

const iconMap: Record<string, JSX.Element> = {
    "app-window": <AppWindow className={iconClassName}/>,
    "atom": <Atom className={iconClassName}/>,
    "box": <Box className={iconClassName}/>
}

export default function ClusterImage({icon, color, className}: ClusterImageProps) {

    return (
        <AspectRatio ratio={1} className={"p-2"}>
            <div style={{"backgroundColor":color}} className={cn("min-w-10 min-h-10 rounded-lg shadow-lg flex items-center justify-center", className)}>
                {iconMap[icon] || iconMap["app-window"]}
            </div>
        </AspectRatio>
    )
}