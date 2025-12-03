import {useEffect, useRef, useState} from "react";
import {Accordion, AccordionContent, AccordionItem, AccordionTrigger} from "~/components/ui/accordion";
import {Textarea} from "~/components/ui/textarea";
import {Button} from "~/components/ui/button";
import {Spinner} from "~/components/ui/spinner";
import {ArrowDown, SendHorizontal} from "lucide-react";

type messageType = "message" | "reasoning" | "functionCalls" | "tokens";

type MessageBlock = {
	type : messageType,
	content : string[]
};

interface ChatBlock {
	messages : MessageBlock[];
}


export default function AgentChat()  {
	const [message, setMessage] = useState("");
	const [progressingMessages, setProgressingMessages] = useState<MessageBlock[]>([]);
	const [id, setId] = useState<string>("");
	const [chatProgressing, setChatProgressing] = useState(false)
	const [chatStack, setChatStack] = useState<ChatBlock[]>([]);
	const scrollTarget = useRef<HTMLDivElement>(null);
	const scrollObserver = useRef<IntersectionObserver>(null);
	const [activateScroll, setActivateScroll] = useState(false)

	function callback(entries : IntersectionObserverEntry[], observer : IntersectionObserver) {
		entries.forEach(entry => {
			if (entry.isIntersecting) {
				console.log(`${entry.target} is intersecting`);
				setActivateScroll(false);
			} else {
				console.log(`${entry.target} is not intersecting`);
				setActivateScroll(true);
			}
		});
	}

	function scrollHandler(e : Event){
	}
	useEffect(() => {
		if(scrollTarget.current){
			scrollObserver.current = new IntersectionObserver(callback);
			scrollObserver.current.observe(scrollTarget.current);
		}

		window.addEventListener("scroll",scrollHandler)
		return ()=>{
			window.removeEventListener("scroll",scrollHandler)
		}
	}, []);

	function processing(type: messageType, content : string){
		setProgressingMessages(prev=>{
			let reasoningMessage;
			if(prev[prev.length-1].type ==type){
				const lastMessage = prev[prev.length-1]
				reasoningMessage = {
					...lastMessage,
					content: [...lastMessage.content, content]
				};
				// 마지막 요소만 교체
				return [...prev.slice(0, -1), reasoningMessage];
			} else {
				reasoningMessage = {type:type, content:[content]} as MessageBlock
				return [...prev, reasoningMessage];
			}
		})
		window.scrollTo({top: document.body.scrollHeight, behavior: 'smooth'});
	}

	const sendMessage = async () => {
		setChatProgressing(true)
		setProgressingMessages(prev=>([...prev,{type:"message", content:[message]}]));

		const response = await fetch("http://localhost:8080/chat/completions", {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
				'Accept': 'text/event-stream'
			},
			body: JSON.stringify({id : id, message : message})
		})
			.then(res=>res.body)
			.then(async (body) => {
				if(body){
					const reader = body.getReader();
					const decoder = new TextDecoder();

					while (true) {
						const { done, value } = await reader.read();
						if (done) {
							break;
						}

						const chunk = decoder.decode(value);
						let dataStr = "";
						if(chunk){
							if(chunk.startsWith("data:")){
								dataStr = chunk.replace("data:","");
							} else {
								dataStr = chunk;
							}
						} else {
							continue;
						}

						let json;
						try {
							json = JSON.parse(dataStr);
						} catch (e){
							// console.error("Failed to parse JSON chunk:", e);
						}
						if(json){
							if(json.type == "chat_id"){
								setId(json.content);
							}
							if(json.type == "thinking"){
								processing("reasoning", json.content)
							}
							if(json.type == "token"){
								processing("tokens", json.content)
							}
							if(json.type == "function_call"){
								processing("functionCalls", json.content)
							}
						}
					}
				}
		}).finally(()=>{
			setChatProgressing(false)
		})

	};
	useEffect(() => {
		if(!chatProgressing) {
			if(progressingMessages.length > 0){
				setChatStack(prev=>([...prev, {messages:progressingMessages}]))
				setMessage("")
				setProgressingMessages([])
			}
		}
	}, [chatProgressing]);

	return (
		<div className={"flex justify-center flex-col items-center"}>
			<div className={"w-[1080px] pb-40"}>
				<div className={"flex flex-col gap-5"}>
					{
						chatStack.map((item) => (
							<div className={"flex flex-col gap-3"}>
								{
									item.messages.map((message) => <MessageRenderer message={message} isProgressing={false} />)
								}
							</div>
						))
					}
					{
						chatProgressing &&
						<div className={"flex flex-col gap-3"}>
							{
								progressingMessages.map((message) => <MessageRenderer message={message} isProgressing={chatProgressing} />)
							}
						</div>
					}
				</div>
			</div>
			<div ref={scrollTarget}></div>
			{
				activateScroll &&
				<Button className={"fixed bottom-40 cursor-pointer"}
					size={"icon"}
			        onClick={()=>{
						window.scrollTo({top: document.body.scrollHeight, behavior: 'smooth'});
				}}><ArrowDown /></Button>
			}
			<div className={"fixed bottom-0 bg-white rounded-t-xl"}>
				<div className={"bg-neutral-300 rounded-xl mb-1 p-1"}>
					<div className={"flex flex-col justify-between gap-2 w-[1080px] "}>
						{ chatProgressing ?
							<>
								<Textarea className={"border-0 resize-none"} placeholder={"query"} disabled={true} />
							</>
							:
							<>
								<Textarea className={"border-0 shadow-none resize-none "} placeholder={"입력"} value={message} onChange={(e)=>{setMessage(e.target.value)}} onKeyDown={(e)=>{if(e.key === "Enter"){e.preventDefault();sendMessage();}} } />
							</>
						}
						<div className={"flex flex-row justify-end pb-3 px-4 pt-1"}>
							<Button size={"icon"} onClick={sendMessage}><SendHorizontal /></Button>
						</div>
					</div>
				</div>
			</div>
		</div>
	);
};



function MessageRenderer({message, isProgressing}: {message: MessageBlock, isProgressing : boolean}){
	if(!message.content.join("").trim()){
		return ""
	}
	if(message.type == "message"){
		return (
			<div className={"p-3 bg-neutral-600 rounded-md text-white w-fit"}>
				{message.content.join("").trim().split("\n").map((line, index) => <p key={index}>{line != "\n" ? line : <br />}</p>)}
			</div>
		)
	}

	if(message.type == "tokens" && message.content.length > 0){
		return (
			<div className={"px-1"}>
				{message.content.join("").trim().split("\n").map((line, index) => <p key={index}>{line != "\n" ? line : <br />}</p>)}
			</div>
		)
	}

	if(message.type == "functionCalls"){
		return (
			<Accordion
				type="single"
				collapsible
				className="w-full border border-neutral-400 rounded-md px-3"
				defaultValue={isProgressing ? "item-1" : ""}
			>
				<AccordionItem value="item-1">
					<AccordionTrigger className={"cursor-pointer"}>도구 사용</AccordionTrigger>
					<AccordionContent className="flex flex-col gap-4 text-balance">
						<p>
							{
								message.content.join("").trim().split("\n").map((line, index) => <p key={index}>{line != "\n" ? line : <br />}</p>)
							}
						</p>
					</AccordionContent>
				</AccordionItem>
			</Accordion>
		)
	}

	if(message.type == "reasoning"){
		return (
			<Accordion
				type="single"
				collapsible
				className="w-full border border-neutral-400 rounded-md px-3"
				defaultValue={isProgressing ? "item-1" : ""}
			>
				<AccordionItem value="item-1">
					<AccordionTrigger className={"cursor-pointer"}><span>{isProgressing ? <>생각 중 <Spinner className={"inline ml-1"} /></> : "사고 과정"}</span></AccordionTrigger>
					<AccordionContent className="flex flex-col gap-4 text-balance">
						<p>
							{
								message.content.join("").trim().split("\n").map((line, index) => <p key={index}>{line != "\n" ? line : <br />}</p>)
							}
						</p>
					</AccordionContent>
				</AccordionItem>
			</Accordion>
		)
	}
	return ""

}