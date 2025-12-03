import {cn} from "~/lib/utils";
import {Button} from "~/components/ui/button";
import {SendIcon} from "lucide-react";
import {useEffect, useState} from "react";
import {useIsMobile} from "~/hooks/use-mobile";
import MyEditor from "~/widget/editor/MyEditor";


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
		<div className={"flex justify-center flex-col items-center h-full"}>
			<div className={cn("bg-white w-2xl border rounded-xl", isMobile ? "w-full":"")}>
				<MyEditor onEdit={(state) => setMessage(state)}
				          className={cn("w-full p-4 focus:outline-none")}
				          isEditable={isEditable}
				          onKeyDown={(e) => {
								if(e.key === "Enter" && !e.shiftKey && !e.ctrlKey){
									e.preventDefault();
									fnChat();
								}
				          }}
				/>
				<div className={"flex justify-between p-4"}>
					{/* Action Buttons */}
					<div>

					</div>
					<div>
						{/* Model Selector */}
						<div>

						</div>
						<div>
							<Button size={"icon"}
							        className={cn(chatEnabled ? "bg-stone-500 hover:bg-stone-600 cursor-pointer" : "bg-stone-400")}
							        disabled={!chatEnabled}
							        onClick={fnChat}
							>
								<SendIcon/>
							</Button>
						</div>
					</div>
				</div>
			</div>
		</div>
	);
};