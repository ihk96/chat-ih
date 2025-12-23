import {cn} from "~/lib/utils";
import {Button} from "~/components/ui/button";
import {SendIcon} from "lucide-react";
import {useEffect, useState} from "react";
import {useIsMobile} from "~/hooks/use-mobile";
import MyEditor from "~/widget/editor/MyEditor";
import ChatInputPannel from "~/widget/ChatInputPannel";
import {MAIN_HEIGHT_CLASS} from "~/lib/style_variables";
import {type Route} from "../../.react-router/types/app/routes/+types/_app.chat.new";
import ModelUserAPI from "~/features/model/ModelUserAPI";
import {useLoaderData, useNavigate} from "react-router";
import ChatAPI from "~/features/chat/ChatAPI";
import type { UserLLModel} from "~/features/model/types";

export async function loader({request} : Route.LoaderArgs){

	const models = await ModelUserAPI.getModels(request).then(res => res.data) ?? []

	return {
		models
	}
}

export default function AppChatNew()  {
	const {models} = useLoaderData<typeof loader>();
	const [message, setMessage] = useState<string>("");
	const [isSendable, setIsSendable] = useState(false);
	const [isEditable, setIsEditable] = useState(true);

	const navigate = useNavigate();

	useEffect(() => {
		if(message){
			setIsSendable(true)
		} else {
			setIsSendable(false)
		}
	}, [message]);

	function fnChat(_message : string, _model : UserLLModel){
		setIsEditable(false);
		setIsSendable(false)
		ChatAPI.initChat({message:_message, modelId: _model.id}).then(res => {
			const sessionId = res.data
			if(sessionId){
				navigate(`/chat/${sessionId}`)
			}
		})
	}

	return (
		<div className={cn("flex justify-center flex-col items-center",MAIN_HEIGHT_CLASS)}>
			<div className={"flex flex-col pb-64 gap-3"}>
				<div className={"py-4 flex justify-center items-center flex-col gap-1"}>
					<h1 className={"text-3xl font-semibold"}>Username님, 안녕하세요!</h1>
					<span className={"text-xl"}>오늘은 무엇을 도와드릴까요?</span>
				</div>
				<ChatInputPannel className={"w-3xl"}
				                 models={models}
				                 onSend={fnChat}
				                 message={message}
				                 isEditable={isEditable}
				                 isSendable={isSendable}
				                 onChangeMessage={setMessage}
				/>
			</div>
		</div>
	);
};