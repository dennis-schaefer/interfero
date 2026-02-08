import {
    createCluster,
    useGetAllClusterInfo,
    verifyAdminConnection,
    verifyClientConnection,
} from "@/api/endpoints/cluster-controller.ts";
import {useNavigate} from "react-router-dom";
import {
    Card,
    CardAction,
    CardContent,
    CardDescription,
    CardFooter,
    CardHeader,
    CardTitle
} from "@/components/ui/card.tsx";
import {Button} from "@/components/ui/button.tsx";
import {Separator} from "@/components/ui/separator.tsx";
import {Badge} from "@/components/ui/badge.tsx";
import {AudioWaveform, ChevronLeft, ChevronRight, Flag} from "lucide-react";
import ClusterConnectionForm from "@/features/clusters/ClusterConnectionForm.tsx";
import {useState} from "react";
import type {ClusterConnectionSettings, ClusterCreation} from "@/api/schemas";
import type {ClusterInfo} from "@/api/schemas";
import {cn} from "@/lib/utils.ts";
import ClusterInfoForm from "@/features/clusters/ClusterInfoForm.tsx";
import {useAuth} from "@/features/security/AuthContext.tsx";
import {toast} from "sonner";

export default function ClusterSetupPage() {

    const navigate = useNavigate();
    const {accountInfo, loading: loadingAuth} = useAuth();
    const [step, setStep] = useState(1);
    const [isCreatingCluster, setIsCreatingCluster] = useState(false);
    const [clientConnectionSettings, setClientConnectionSettings] = useState<ClusterConnectionSettings | undefined>(undefined);
    const [adminConnectionSettings, setAdminConnectionSettings] = useState<ClusterConnectionSettings | undefined>(undefined);
    const [clusterInfo, setClusterInfo] = useState<ClusterInfo | undefined>(undefined);
    const {data: clusters, isLoading: loadingClusters} = useGetAllClusterInfo();

    if (loadingClusters || loadingAuth)
        return (<></>);

    if ((clusters && clusters.length > 0) || (accountInfo && accountInfo.roles?.includes("ROLE_ADMIN"))) {
        navigate("/");
        return (<></>);
    }

    const handleClientConnectionVerification = async (connectionSettings: ClusterConnectionSettings | null): Promise<boolean> => {

        setClientConnectionSettings(undefined);
        if (!connectionSettings)
            return false;

        let succeeded = true;
        await verifyClientConnection(connectionSettings)
            .then(() => setClientConnectionSettings(connectionSettings))
            .catch(() => succeeded = false);

        return succeeded;
    }

    const handleAdminConnectionVerification = async (connectionSettings: ClusterConnectionSettings | null): Promise<boolean> => {

        setAdminConnectionSettings(undefined);
        if (!connectionSettings)
            return false;

        let succeeded = true;
        await verifyAdminConnection(connectionSettings)
            .then(() => setAdminConnectionSettings(connectionSettings))
            .catch(() => succeeded = false);

        return succeeded;
    }

    const handleClusterInfoChange = (clusterInfo: ClusterInfo | null) => {
        setClusterInfo(clusterInfo || undefined);
    }

    const nextStep = () => {
        if (step === 3) {
            finishSetup();
            return;
        }

        setStep(prevState => prevState + 1);
    }

    const previousStep = () => {
        setStep(prevState => prevState - 1);
    }

    const finishSetup = () => {
        setIsCreatingCluster(true);
        const cluster: ClusterCreation = {
            clusterInfo, clientConnectionSettings, adminConnectionSettings
        }

        toast.promise(
            () => createCluster(cluster)
                .then(() => closeSetup())
                .catch(() => setIsCreatingCluster(false)),
            {
                loading: "Creating cluster...",
                success: "Cluster created successfully!",
                error: (err) => "Failed to create cluster: " + err.message
            }
        );
    }

    const closeSetup = () => {
        setTimeout(() => navigate("/"), 2000);
    };

    const continueEnabled =
        (step === 1 && clientConnectionSettings) ||
        (step === 2 && adminConnectionSettings) ||
        (step === 3 && clusterInfo && !isCreatingCluster);

    return (
        <div className={"grid h-screen w-screen place-items-center"}>
            <Card className="w-full max-w-lg backdrop-blur-xl bg-background/50 dark:bg-background/15">
                <CardHeader>
                    <CardTitle className={"flex flex-row items-center gap-2"}>
                        <AudioWaveform/>
                        <h2 className="text-2xl font-semibold">Welcome to Interfero</h2>
                    </CardTitle>
                    <CardDescription>
                        Set up a connection to your first Pulsar Cluster
                    </CardDescription>
                    <CardAction>
                        <Badge variant={"secondary"} className={"leading-0"}>Step {step} of 3</Badge>
                    </CardAction>
                </CardHeader>

                <Separator/>

                <CardContent>
                    {step === 1 &&
                        <ClusterConnectionForm title={"Broker Connection"}
                                               description={"Configure how Interfero connects to the Pulsar broker"}
                                               serviceUrlLabel={"Broker Service URL"}
                                               serviceUrlPlaceholder={"cluster.pulsar.local:6650"}
                                               serviceUrlPrefix={"pulsar://"}
                                               serviceUrlRegex={/^pulsar:\/\/[a-zA-Z0-9.-]+(:\d+)?$/}
                                               message={clientConnectionSettings ? "Connection to Pulsar Broker successfully established!" : "Could not establish connection to the Pulsar broker"}
                                               initialConnectionSettings={clientConnectionSettings}
                                               onVerifyConnection={handleClientConnectionVerification}/>
                    }

                    {step === 2 &&
                        <ClusterConnectionForm title={"Admin API Connection"}
                                               description={"Configure how Interfero connects to the Pulsar Admin API"}
                                               serviceUrlLabel={"Admin API URL"}
                                               serviceUrlPlaceholder={"https://cluster.pulsar.local:8080"}
                                               serviceUrlPrefix={""}
                                               serviceUrlRegex={/^https?:\/\/[a-zA-Z0-9.-]+(:\d+)?$/}
                                               message={adminConnectionSettings ? "Connection to Pulsar Admin API successfully established" : "Could not establish connection to the Pulsar Admin API"}
                                               initialConnectionSettings={adminConnectionSettings}
                                               onVerifyConnection={handleAdminConnectionVerification}/>
                    }

                    {step === 3 &&
                        <ClusterInfoForm initialClusterInfo={clusterInfo}
                                         onClusterInfoChanged={handleClusterInfoChange}
                                         disabled={isCreatingCluster}/>
                    }
                </CardContent>

                <Separator/>

                <CardFooter className={"flex flex-row justify-between"}>
                    <Button variant={"ghost"}
                            disabled={step === 1 || isCreatingCluster}
                            className={cn("transition-opacity", step > 1 ? "opacity-100" : "opacity-0!")}
                            onClick={previousStep}>
                        <ChevronLeft/>
                        Previous Step
                    </Button>
                    <Button disabled={!continueEnabled}
                            onClick={nextStep}>
                        {step < 3 ? "Next Step" : "Finish Setup"}
                        {step < 3 ? <ChevronRight/> : <Flag/>}
                    </Button>
                </CardFooter>
            </Card>
        </div>
    );
}