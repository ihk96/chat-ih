import ChatInputPannel, {type ChatInputPannelRef} from "~/widget/ChatInputPannel";
import {useEffect, useRef, useState} from "react";
import {AiMessageBlock, UserMessageBlock} from "~/widget/chat/ChatMessageBlocks";
import {type Route} from "../../.react-router/types/app/routes/+types/_app.chat.$id";
import ModelUserAPI from "~/features/model/ModelUserAPI";
import {useLoaderData} from "react-router";
import useChatSession from "~/features/chat/hooks";
import type {UserLLModel} from "~/features/model/types";
import {Spinner} from "~/components/ui/spinner";
import {Button} from "~/components/ui/button";
import {ArrowDown} from "lucide-react";
import {useMainScroll} from "~/routes/_app/MainScrollContext";

export async function loader({request, params} : Route.LoaderArgs) {
	const sessionId = params.id;
	const models = await ModelUserAPI.getModels(request).then(res => res.data) ?? []
	return {
		models,
		sessionId
	}
}

export default function AgentChat()  {
	const {models, sessionId} = useLoaderData<typeof loader>()
	const {messages, session, subscribe, fetchSession, sendMessage, isProcessing, progressingMessage, progressingType} = useChatSession(sessionId)
	const [message, setMessage] = useState<string>("");
	const [isSendable, setIsSendable] = useState(false);
	const [isEditable, setIsEditable] = useState(true);
	const inputPannelRef = useRef<HTMLDivElement>(null);
	const contentAreaRef = useRef<HTMLDivElement>(null);

	const scrollTarget = useRef<HTMLDivElement>(null);
	const scrollObserver = useRef<IntersectionObserver>(null);
	const [activateScroll, setActivateScroll] = useState(false)
	const [autoScroll, setAutoScroll] = useState(true)
	const chatInputRef = useRef<ChatInputPannelRef>(null)

	const {scrollRef} = useMainScroll()


	function callback(entries : IntersectionObserverEntry[], observer : IntersectionObserver) {
		entries.forEach(entry => {
			if (entry.isIntersecting) {
				console.log(`${entry.target} is intersecting`);
				setActivateScroll(false);
				setAutoScroll(true);
			} else {
				console.log(`${entry.target} is not intersecting`);
				setActivateScroll(true);
			}
		});
	}

	function scrollHandler(e : Event){
		setAutoScroll(false);
	}
	useEffect(() => {
		if(scrollTarget.current){
			scrollObserver.current = new IntersectionObserver(callback);
			scrollObserver.current.observe(scrollTarget.current);
		}
		if(scrollRef.current){
			scrollRef.current.addEventListener("scroll",scrollHandler)
		}
		return ()=>{
			if(scrollRef.current){
				scrollRef.current.removeEventListener("scroll",scrollHandler)
			}
		}
	}, []);

	useEffect(() => {
		fetchSession();
		subscribe();
	}, [sessionId]);

	useEffect(() => {
		if(message){
			setIsSendable(true)
		} else {
			setIsSendable(false)
		}
	}, [message]);

	useEffect(()=>{
		if(autoScroll){
			if(scrollRef.current) {
				scrollRef.current.scrollTo({top: scrollRef.current.scrollHeight, behavior: 'smooth'});
			}
		}
	},[progressingMessage, messages])

	useEffect(() => {
		if(inputPannelRef.current){
			const resizeObserver = new ResizeObserver((entries: ResizeObserverEntry[]) => {
				const height = entries[0].contentRect.height;
				if(contentAreaRef.current){
					contentAreaRef.current.style.paddingBottom = `${height}px`;
				}
			});
			resizeObserver.observe(inputPannelRef.current);
		}
	}, [inputPannelRef]);

	function fnChat(_message : string, _model : UserLLModel){
		setIsEditable(false);
		setIsSendable(false)
		sendMessage(_message, _model.id).then(()=>{
			if(chatInputRef.current){
				chatInputRef.current.reset()
			}
		}).finally(()=>{
			setIsSendable(true)
			setIsEditable(true)
		})
	}

	return (
		<div className={"flex items-center flex-col gap-1"}>
			<div ref={contentAreaRef} className={"w-3xl"}>
				<div className={"flex flex-col gap-6 justify-around py-6"}>
					{
						messages.map((message, index) => {
							if(message.type === "USER"){
								return <UserMessageBlock key={index} message={message}/>
							}else{
								return <AiMessageBlock key={index} message={message}/>
							}
						})
					}
					{
						isProcessing && !progressingMessage &&
						<Spinner />
					}
					{
						isProcessing && progressingMessage &&
						<AiMessageBlock message={progressingMessage} processingType={progressingType}/>
					}
				</div>
				<div className={"h-2"}>
				{/*	Bottom Observer */}
				</div>
			</div>
			<div ref={scrollTarget}></div>
			<div ref={inputPannelRef} className={"w-4xl flex bg-stone-50 rounded-t-xl justify-center fixed bottom-0 pb-4"}>
				{
					activateScroll &&
					<Button className={"cursor-pointer absolute top-[-60px]"}
					        size={"icon"}
					        variant={"outline"}
					        onClick={()=>{
						        if(scrollRef.current) {
							        scrollRef.current.scrollTo({top: scrollRef.current.scrollHeight, behavior: 'smooth'});
						        }
					        }}><ArrowDown /></Button>
				}
				<ChatInputPannel className={"w-3xl"}
								 ref={chatInputRef}
				                 models={models}
				                 onSend={fnChat}
				                 message={message}
				                 isEditable={isEditable && !isProcessing}
				                 isSendable={isSendable && !isProcessing}
				                 onChangeMessage={setMessage}
				/>
			</div>
		</div>
	);
};

