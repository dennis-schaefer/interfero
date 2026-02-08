import { AspectRatio } from "@/components/ui/aspect-ratio.tsx";
import { cn } from "@/lib/utils.ts";
import { AppWindow, Atom, Box, Cloud, Cpu, Database, Globe, HardDrive, Layers, Package, Pencil, Server } from "lucide-react";
import type { JSX } from "react";
import React, { useState } from "react";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover.tsx";

interface ClusterImageProps {
    icon: string,
    color: string
    className?: string
    editable?: boolean
    onIconChange?: (icon: string) => void
    onColorChange?: (color: string) => void
}

const iconClassName = "h-[70%] w-[70%] text-white";

const iconMap: Record<string, JSX.Element> = {
    "app-window": <AppWindow className={iconClassName} />,
    "atom": <Atom className={iconClassName} />,
    "box": <Box className={iconClassName} />,
    "database": <Database className={iconClassName} />,
    "server": <Server className={iconClassName} />,
    "cloud": <Cloud className={iconClassName} />,
    "globe": <Globe className={iconClassName} />,
    "layers": <Layers className={iconClassName} />,
    "package": <Package className={iconClassName} />,
    "cpu": <Cpu className={iconClassName} />,
    "hard-drive": <HardDrive className={iconClassName} />
}

const availableIcons = Object.keys(iconMap);

const availableColors = [
    { name: "Emerald", value: "#10b981" },
    { name: "Blue", value: "#3b82f6" },
    { name: "Purple", value: "#a855f7" },
    { name: "Pink", value: "#ec4899" },
    { name: "Orange", value: "#f97316" },
    { name: "Red", value: "#ef4444" },
    { name: "Yellow", value: "#eab308" },
    { name: "Teal", value: "#14b8a6" },
    { name: "Indigo", value: "#6366f1" },
    { name: "Cyan", value: "#06b6d4" }
];

export default function ClusterImage({ icon, color, className, editable = false, onIconChange, onColorChange }: ClusterImageProps) {
    const [isHovered, setIsHovered] = useState(false);
    const [isOpen, setIsOpen] = useState(false);

    const handleIconSelect = (selectedIcon: string) => {
        if (onIconChange) {
            onIconChange(selectedIcon);
        }
        //setIsOpen(false);
    };

    const handleColorSelect = (selectedColor: string) => {
        if (onColorChange) {
            onColorChange(selectedColor);
        }
        //setIsOpen(false);
    };

    const imageContent = (
        <div
            className="relative"
            onMouseEnter={() => editable && setIsHovered(true)}
            onMouseLeave={() => editable && setIsHovered(false)}
        >
            <AspectRatio ratio={1} className={"p-2"}>
                <div style={{ "backgroundColor": color }} className={cn("min-w-10 min-h-10 rounded-lg shadow-lg flex items-center justify-center", className)}>
                    {iconMap[icon] || iconMap["app-window"]}
                </div>
            </AspectRatio>

            {editable && isHovered && (
                <div className="absolute inset-0 bg-black/50 rounded-lg flex items-center justify-center cursor-pointer transition-opacity">
                    <Pencil className="h-8 w-8 text-white" />
                </div>
            )}
        </div>
    );

    if (!editable) {
        return imageContent;
    }

    return (
        <Popover open={isOpen} onOpenChange={setIsOpen}>
            <PopoverTrigger>
                {imageContent}
            </PopoverTrigger>
            <PopoverContent className="w-80">
                <div className="space-y-4">
                    <div>
                        <h4 className="font-medium text-sm mb-3">Select Icon</h4>
                        <div className="grid grid-cols-6 gap-2">
                            {availableIcons.map((iconKey) => (
                                <button
                                    key={iconKey}
                                    onClick={() => handleIconSelect(iconKey)}
                                    className={cn(
                                        "p-2 rounded-md border-2 hover:bg-accent transition-colors cursor-pointer",
                                        icon === iconKey ? "border-primary bg-accent" : "border-transparent"
                                    )}
                                    title={iconKey}
                                >
                                    <div className="h-6 w-6 flex items-center justify-center">
                                        {React.cloneElement(iconMap[iconKey], { className: "h-5 w-5 text-foreground" })}
                                    </div>
                                </button>
                            ))}
                        </div>
                    </div>

                    <div className="border-t pt-4">
                        <h4 className="font-medium text-sm mb-3">Select Color</h4>
                        <div className="grid grid-cols-5 gap-2">
                            {availableColors.map((colorOption) => (
                                <button
                                    key={colorOption.value}
                                    onClick={() => handleColorSelect(colorOption.value)}
                                    className={cn(
                                        "h-10 w-10 rounded-md border-2 transition-all hover:scale-110 cursor-pointer",
                                        color === colorOption.value ? "border-foreground ring-2 ring-offset-2 ring-foreground" : "border-transparent"
                                    )}
                                    style={{ backgroundColor: colorOption.value }}
                                    title={colorOption.name}
                                />
                            ))}
                        </div>
                    </div>
                </div>
            </PopoverContent>
        </Popover>
    )
}
