import {cn} from "~/lib/utils";
import {Button} from "~/components/ui/button";
import {SendIcon} from "lucide-react";
import {useEffect, useState} from "react";
import {useIsMobile} from "~/hooks/use-mobile";


export default function AppChatNew()  {
	const [message, setMessage] = useState("");
	const [chatEnabled, setChatEnabled] = useState(false)

	const isMobile = useIsMobile();

	useEffect(() => {
		if(message){
			setChatEnabled(true)
		} else {
			setChatEnabled(false)
		}
	}, [message]);

	return (
		<div className={"flex justify-center flex-col items-center h-full"}>
			<div className={cn("bg-white w-2xl border rounded-xl", isMobile ? "w-full":"")}>
				<div className={cn("w-full p-4 focus:outline-none")}
				     contentEditable
				     onInput={(e) => setMessage(e.currentTarget.textContent)}
				>
				</div>
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