import { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import axios from "axios";
import {Field, FieldError, FieldGroup, FieldLabel, FieldSet} from "../components/ui/field.tsx";
import {Button} from "../components/ui/button.tsx";
import {Card, CardContent} from "../components/ui/card.tsx";
import {Input} from "../components/ui/input.tsx";
import {useAuth} from "../features/security/AuthContext.tsx";
import {Spinner} from "../components/ui/spinner.tsx";

const loginSchema = z.object({
    username: z.string().min(1, "Username is required"),
    password: z.string().min(1, "Password is required"),
});

type LoginFormData = z.infer<typeof loginSchema>;

export default function LoginPage() {
    const loginErrorMessage = "Login failed. Please check your credentials and try again.";

    const { isAuthenticated, loading } = useAuth();
    const [error, setError] = useState<string | null>(null);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const {
        register,
        handleSubmit,
        formState: { errors },
    } = useForm<LoginFormData>({
        resolver: zodResolver(loginSchema),
    });

    useEffect(() => {
        if (!loading && isAuthenticated) {
            window.location.href = "/";
        }
    }, [isAuthenticated, loading]);

    const onSubmit = async (data: LoginFormData) => {
        setIsSubmitting(true);
        setError(null);

        try {
            const formData = new URLSearchParams();
            formData.append("username", data.username);
            formData.append("password", data.password);

            const response = await axios.post("/login", formData, {
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                },
                withCredentials: true,
            });

            if (response.status === 200 && response.data.success) {
                window.location.href = "/";
                return;
            }

            setError(loginErrorMessage);
        } catch (err) {
            setError(loginErrorMessage);
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <>
            { loading && (
                <div className={"w-full h-full absolute flex justify-center items-center"}>
                    <Spinner className={"size-10"} />
                </div>
            )}

            {!loading && !isAuthenticated && (
                <div className={"grid h-screen w-screen place-items-center"}>
                    <Card className="w-full max-w-sm">
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
                                            { error && <FieldError>{error}</FieldError> }
                                        </Field>
                                    </FieldGroup>

                                    <Button type="submit" className="w-full" disabled={isSubmitting}>
                                        {isSubmitting ? "Signing in..." : "Sign In"}
                                    </Button>
                                </FieldSet>
                            </form>
                        </CardContent>
                    </Card>
                </div>
            )}
        </>
    );
}