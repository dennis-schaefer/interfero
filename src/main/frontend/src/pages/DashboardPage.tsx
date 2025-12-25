import {useHello} from "@/api/endpoints/hello-controller.ts";

export default function DashboardPage() {

    const { data, isLoading } = useHello();

    return (
        <div className={"h-full grid items-center justify-center"}>
            { isLoading && <h1>Loading...</h1>}
            { !isLoading && <h1 className={"text-4xl"}>{data}</h1> }
        </div>
    )
}