import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {FieldLabel, FieldSet, FieldGroup, FieldError, Field} from "@/components/ui/field";
import {Input} from "@/components/ui/input.tsx";
import {Button} from "@/components/ui/button.tsx";
import {Spinner} from "@/components/ui/spinner.tsx";
import {AudioWaveform} from "lucide-react";
import {useAuth} from "@/features/security/AuthContext.tsx";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import { z } from "zod";
import axios from "axios";
import {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";


const loginSchema = z.object({
    username: z.string().min(1, "Username is required"),
    password: z.string().min(1, "Password is required"),
});

type LoginFormData = z.infer<typeof loginSchema>;


export default function LoginPage() {

    const loginErrorMessage = "Login failed. Please check your credentials and try again.";

    const navigate = useNavigate();
    const { isAuthenticated, loading: isLoadingAuth } = useAuth();
    const [loginError, setLoginError] = useState<string | null>(null);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const {register, handleSubmit, formState: { errors }, setValue, setFocus} = useForm<LoginFormData>({
        resolver: zodResolver(loginSchema),
    });

    const handleLoginError = () => {
        setLoginError(loginErrorMessage);
        setValue("password", "");
        setTimeout(() => {
            setFocus("password");
        }, 10);
    }

    const onSubmit = async (data: LoginFormData) => {
        setIsSubmitting(true);

        try {
            const formData = new URLSearchParams();
            formData.append("username", data.username);
            formData.append("password", data.password);

            const response = await axios.post("/login", formData, {
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                withCredentials: true,
            });

            if (response.status === 200 && response.data.success) {
                setLoginError(null);
                navigate("/login/setup")
                return;
            }

            handleLoginError();
        } catch (err) {
            handleLoginError();
        } finally {
            setIsSubmitting(false);
        }
    }

    useEffect(() => {
        if (!isLoadingAuth && isAuthenticated)
            navigate("/login/setup");
    }, [isAuthenticated, isLoadingAuth]);

    useEffect(() => {
        if (!isLoadingAuth && !isAuthenticated)
            setFocus("username");
    }, [isLoadingAuth, isAuthenticated, setFocus]);

    if (isLoadingAuth || isAuthenticated)
        return <></>

    return (
        <div className={"grid h-screen w-screen place-items-center overflow-y-auto"}>
            <Card className="w-full max-w-sm backdrop-blur-xl bg-background/50 dark:bg-background/15">
                <CardHeader>
                    <CardTitle className={"flex flex-row items-center gap-2"}>
                        <AudioWaveform/>
                        <h2 className="text-2xl font-semibold">Interfero</h2>
                    </CardTitle>
                    <CardDescription>
                        Sign in to your account to continue
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <form onSubmit={handleSubmit(onSubmit)}>
                        <FieldSet>
                            <FieldGroup>
                                <Field>
                                    <FieldLabel htmlFor={"username"}>Username</FieldLabel>
                                    <Input id={"username"}
                                           type={"text"}
                                           placeholder={"Enter username here"}
                                           disabled={isSubmitting}
                                           {...register("username")}/>
                                    { errors.username && <FieldError>{errors.username.message}</FieldError> }
                                </Field>
                                <Field>
                                    <FieldLabel htmlFor="password">Password</FieldLabel>
                                    <Input id={"password"}
                                           type="password"
                                           placeholder={"Enter password here"}
                                           disabled={isSubmitting}
                                           {...register("password")}/>
                                    { errors.password && <FieldError>{errors.password.message}</FieldError> }
                                    { loginError && <FieldError>{loginError}</FieldError> }
                                </Field>
                            </FieldGroup>

                            <Button type="submit" className="w-full" disabled={isSubmitting}>
                                {isSubmitting  && <Spinner />}
                                {isSubmitting ? "Signing in..." : "Sign In"}
                            </Button>
                        </FieldSet>
                    </form>
                </CardContent>
            </Card>
        </div>
    )
}