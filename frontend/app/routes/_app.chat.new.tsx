import {cn} from "~/lib/utils";
import {Button} from "~/components/ui/button";
import {SendIcon} from "lucide-react";
import {useEffect, useState} from "react";
import {useIsMobile} from "~/hooks/use-mobile";
import MyEditor from "~/widget/editor/MyEditor";
import ChatInputPannel from "~/widget/ChatInputPannel";
import {MAIN_HEIGHT_CLASS} from "~/lib/style_variables";


export default function AppChatNew()  {
	const [message, setMessage] = useState<string>("");
	const [chatEnabled, setChatEnabled] = useState(false);
	const [isEditable, setIsEditable] = useState(true);

	const isMobile = useIsMobile();

	useEffect(() => {
		if(message){
			setChatEnabled(true)
		} else {
			setChatEnabled(false)
		}
	}, [message]);

	function fnChat(){
		setIsEditable(false);
		setChatEnabled(false)
	}

	return (
		<div className={cn("flex justify-center flex-col items-center",MAIN_HEIGHT_CLASS)}>
			<div className={"flex flex-col pb-64 gap-3"}>
				<div className={"py-4 flex justify-center items-center flex-col gap-1"}>
					<h1 className={"text-3xl font-semibold"}>Username님, 안녕하세요!</h1>
					<span className={"text-xl"}>오늘은 무엇을 도와드릴까요?</span>
				</div>
				<ChatInputPannel className={"w-3xl"} />
			</div>
		</div>
	);
};