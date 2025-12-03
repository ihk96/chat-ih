import LoginForm from "~/widget/LoginForm";
import {useNavigate} from "react-router";


export default function Login(){

	const naviagate = useNavigate();


	return (
		<div className={"w-full h-[100vh] flex justify-center items-center"}>
			<div className={"w-96"}>
				<div className={"flex justify-center mb-2"}>
					<h3 className={"text-xl font-bold"}>Chat IH에 로그인</h3>
				</div>
				<div>
					<LoginForm onLoginSuccess={() => naviagate("/chat/new")} />
				</div>
			</div>
		</div>
	)
}