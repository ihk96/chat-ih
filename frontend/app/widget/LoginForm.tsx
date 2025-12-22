import {z} from "zod/v4";
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from "~/components/ui/form";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {Input} from "~/components/ui/input";
import {Button} from "~/components/ui/button";
import {Loader2Icon} from "lucide-react";
import {useState} from "react";
import {cn} from "~/lib/utils";
import AuthAPI from "~/features/user/AuthAPI";
import {toast} from "sonner";
import type {AxiosError} from "axios";

const LoginFormSchema = z.object({
	id : z.string(),
	password : z.string().min(6),
})

type LoginFormProps = {
	defaultId?: string
	defaultPassword?: string
	onLoginSuccess?: () => void
	onLoginFailure?: () => void
} & React.HTMLAttributes<HTMLDivElement>

export default function LoginForm(props : LoginFormProps){
	const [loginPending, setLoginPending] = useState(false);
	const form = useForm({
		resolver: zodResolver(LoginFormSchema),
		defaultValues: {
			id: props.defaultId ?? "",
			password: props.defaultPassword ?? "",
		},
		mode : "all"
	});
	async function submitLogin(data: z.infer<typeof LoginFormSchema>) {

		setLoginPending(true);
		AuthAPI.register(data.id,data.password).then(()=>{
			AuthAPI.login(data.id, data.password).then(result => {
				props.onLoginSuccess?.()
				setLoginPending(false);
			}).catch((e: AxiosError<any>)=>{
				toast.warning("로그인에 실패했습니다.",{
					// @ts-ignore
					description : e.response.data.message,
					position : "top-center",
				})
				setLoginPending(false);
				props.onLoginFailure?.()
			});
		})

	}


	return (
		<Form {...form}>
			<form onSubmit={form.handleSubmit(submitLogin)}>
				<div className={cn("flex flex-col gap-6",props.className)} {...props}>
					<div className="grid gap-2">
						<FormField control={form.control}
						           name={"id"}
						           render={({field})=>(
							           <FormItem>
								           <FormLabel>ID</FormLabel>
								           <FormControl>
									           <Input placeholder={"id"}
									                  {...field}
									           />
								           </FormControl>
								           <FormMessage />
							           </FormItem>
						           )}
						/>
					</div>
					<div className="grid gap-2">
						<FormField control={form.control}
						           name={"password"}
						           render={({field})=>(
							           <FormItem>
								           <FormLabel>Password</FormLabel>
								           <FormControl>
									           <Input placeholder={"Password"}
									                  type={"password"}
									                  {...field}
									           />
								           </FormControl>
								           <FormMessage />
							           </FormItem>
						           )}
						/>
					</div>
					<button type={"submit"} className={"hidden"}>submit</button>
					<Button className="w-full" disabled={loginPending} onClick={form.handleSubmit(submitLogin)}>
						{
							loginPending ? <Loader2Icon className="animate-spin" /> : "로그인"
						}
					</Button>
				</div>
				{/*<div className="mt-4 text-center text-sm">*/}
				{/*	신규 입사자이신가요?{" "}*/}
				{/*	<a onClick={()=>{navigate("/signup")}} className="underline underline-offset-4 hover:cursor-pointer">*/}
				{/*		계정 만들기*/}
				{/*	</a>*/}
				{/*</div>*/}
			</form>
		</Form>
	)
}