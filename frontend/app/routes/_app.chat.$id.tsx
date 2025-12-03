import {Textarea} from "~/components/ui/textarea";
import {Button} from "~/components/ui/button";
import {ArrowDown, SendHorizontal} from "lucide-react";
import ChatInputPannel from "~/widget/ChatInputPannel";



export default function AgentChat()  {

	return (
		<div className={"flex justify-center flex-col items-center h-full"}>
			<ChatInputPannel className={"w-3xl"} />
		</div>
	);
};

