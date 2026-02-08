import type {ClusterConnectionSettings} from "@/api/schemas";
import {useEffect, useState} from "react";
import {Field, FieldLabel, FieldDescription} from "@/components/ui/field.tsx";
import {Tabs, TabsContent, TabsList, TabsTrigger} from "@/components/ui/tabs.tsx";
import {IdCard, KeyRound, ShieldOff, SquareAsterisk} from "lucide-react";
import {useDebounce} from "use-debounce";
import {InputGroup, InputGroupAddon, InputGroupInput, InputGroupText} from "@/components/ui/input-group.tsx";
import {Spinner} from "@/components/ui/spinner.tsx";

interface ClusterConnectionFormProps {
    initialConnectionSettings?: ClusterConnectionSettings,
    title?: string,
    description?: string,
    serviceUrlLabel: string,
    serviceUrlPlaceholder: string,
    serviceUrlPrefix: string,
    serviceUrlRegex: RegExp,
    message?: string,
    onVerifyConnection: (connectionSettings: ClusterConnectionSettings | null) => Promise<boolean>
}

const emptyClusterConnectionSettings: ClusterConnectionSettings = {
    serviceUrl: '',
    authenticationMethod: "NO_AUTH"
}

export default function ClusterConnectionForm({initialConnectionSettings, title, description, serviceUrlLabel,
                                               serviceUrlPlaceholder, serviceUrlPrefix, serviceUrlRegex,
                                               message, onVerifyConnection}: ClusterConnectionFormProps) {

    const [clusterConnectionSettings, setClusterConnectionSettings] = useState<ClusterConnectionSettings>(initialConnectionSettings || emptyClusterConnectionSettings);
    const [debouncedConnectionSettings] = useDebounce<ClusterConnectionSettings>(clusterConnectionSettings, 700);
    const [validConnection, setValidConnection] = useState<boolean | null>(null);
    const [isValidating, setIsValidating] = useState(false);

    const handleServiceUrlChange = (serviceUrl: string) => {
        setIsValidating(true);
        setClusterConnectionSettings(prev => {
            return {
                ...prev,
                serviceUrl: serviceUrlPrefix + serviceUrl
            }
        });
    };

    const verifyConnection = () => {
        const isComplete = debouncedConnectionSettings.serviceUrl.match(serviceUrlRegex) !== null;

        if (!isComplete) {
            onVerifyConnection(null)
                .then(() => setValidConnection(null))
                .finally(() => setIsValidating(false));
            return;
        }

        setIsValidating(false)
        onVerifyConnection(debouncedConnectionSettings)
            .then(setValidConnection);
    };

    useEffect(() => {
        verifyConnection();
    }, [debouncedConnectionSettings]);

    return (
        <div>
            <div>
                {title && <h2 className="text-xl font-semibold">{title}</h2>}
                {description && <p className="mb-4 text-sm text-secondary-foreground">{description}</p>}
            </div>

            <Field>
                <FieldLabel htmlFor={"serviceUrl"}>{serviceUrlLabel}</FieldLabel>
                <InputGroup>
                    <InputGroupInput id={"serviceUrl"}
                                     placeholder={serviceUrlPlaceholder}
                                     defaultValue={clusterConnectionSettings.serviceUrl || ''}
                                     value={clusterConnectionSettings.serviceUrl.replace(serviceUrlPrefix, '') }
                                     onChange={e => handleServiceUrlChange(e.target.value)}/>
                    { serviceUrlPrefix &&
                        <InputGroupAddon>
                            <InputGroupText>{serviceUrlPrefix}</InputGroupText>
                        </InputGroupAddon>
                    }
                    { isValidating &&
                        <InputGroupAddon align="inline-end">
                            <Spinner />
                        </InputGroupAddon>
                    }
                </InputGroup>
                { validConnection !== null &&
                    <FieldDescription className={validConnection ? "text-primary" : "text-destructive"}>
                        {message}
                    </FieldDescription>
                }
            </Field>

            <Tabs defaultValue={"no-auth"} className={"mt-4"}>
                <TabsList variant={"line"}>
                    <TabsTrigger value={"no-auth"}>
                        <ShieldOff/>
                        No Auth
                    </TabsTrigger>
                    <TabsTrigger value={"basic-auth"} disabled>
                        <SquareAsterisk/>
                        Basic Auth
                    </TabsTrigger>
                    <TabsTrigger value={"jwt"} disabled>
                        <KeyRound/>
                        JWT
                    </TabsTrigger>
                    <TabsTrigger value={"oidc"} disabled>
                        <IdCard/>
                        OIDC
                    </TabsTrigger>
                </TabsList>

                <TabsContent value={"no-auth"} className={"my-1"}>
                    <p className={"text-sm text-secondary-foreground"}>Connect without authentication.</p>
                </TabsContent>
            </Tabs>
        </div>
    );
}