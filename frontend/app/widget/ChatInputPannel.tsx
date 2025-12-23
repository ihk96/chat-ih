import {cn} from "~/lib/utils";
import MyEditor, {type MyEditorRef} from "~/widget/editor/MyEditor";
import {Button} from "~/components/ui/button";
import {SendIcon} from "lucide-react";
import {useEffect, useRef, useState} from "react";
import {useIsMobile} from "~/hooks/use-mobile";
import {
	Select,
	SelectContent,
	SelectGroup,
	SelectItem,
	SelectLabel,
	SelectTrigger,
	SelectValue
} from "~/components/ui/select";
import type {LLModel, UserLLModel} from "~/features/model/types";

type ChatInputPannelProps = {
	models: UserLLModel[],
	model?: UserLLModel,
	message?: string,
	onChangeMessage?: (message: string) => void,
	onChangeModel?: (model?: UserLLModel) => void,
	onSend?: (message: string, model: UserLLModel) => void,
	isEditable?: boolean,
	isSendable?: boolean,
} & React.HTMLAttributes<HTMLDivElement>

export default function ChatInputPannel(props : ChatInputPannelProps){
	const {
		models,
		onChangeMessage,
		onChangeModel,
		onSend,
		isEditable,
		isSendable
	} = props;
	const [message, setMessage] = useState<string>(props.message ?? "");
	const editorRef = useRef<MyEditorRef>(null);
	const [model, setModel] = useState<UserLLModel | undefined>(props.model?? models[0] ?? undefined);

	const isMobile = useIsMobile();

	function fnChat(){
		onSend?.(message, model ?? models[0]);
	}

	useEffect(() => {
		onChangeModel?.(model);
	}, [model]);

	useEffect(() => {
		setMessage(props.message ?? "");
	}, [props.message]);

	function handleChange(value: string){
		setMessage(value);
		onChangeMessage?.(value);
	}


	return (
		<div {...props}
		     className={cn("bg-white w-2xl border rounded-xl", isMobile ? "w-full":"", props.className)}
		>
			<MyEditor onEdit={handleChange}
			          ref={editorRef}
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
				<div className={"flex gap-2"}>
					{/* Model Selector */}
					<div>
						<Select value={model?.id ?? ""} onValueChange={(value) => setModel(props.models.find((model) => model.id === value))}>
							<SelectTrigger className="w-fit">
								<SelectValue placeholder="Select Model" />
							</SelectTrigger>
							<SelectContent>
								<SelectGroup>
									{
										props.models.map((model, index) => (
											<SelectItem key={model.id} value={model.id}>{model.modelName}</SelectItem>
										))
									}
								</SelectGroup>
							</SelectContent>
						</Select>
					</div>
					<div>
						<Button size={"icon"}
						        className={cn(isSendable ? "bg-stone-500 hover:bg-stone-600 cursor-pointer" : "bg-stone-400")}
						        disabled={!isSendable}
						        onClick={fnChat}
						>
							<SendIcon/>
						</Button>
					</div>
				</div>
			</div>
		</div>
	)
}