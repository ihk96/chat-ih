import {useEffect, useState} from "react";
import ChatAPI from "~/features/chat/ChatAPI";
import type {ChatMessage, ChatMessageType} from "~/features/chat/types";
import {ChatMessageTypeEnum} from "~/features/chat/schemes";

export type progressingType = "thinking" | "function_call" | "token";


export default function useChatSession(sessionId : string) {
	const [reader, setReader] = useState<ReadableStreamDefaultReader<Uint8Array<ArrayBuffer>> | undefined>(undefined)
	const [session, setSession] = useState<{
		id : string,
		title : string
	}>({id : "", title : ""})
	const [messages, setMessages] = useState<ChatMessage[]>([])
	const [progressingMessage, setProgressingMessage] = useState<ChatMessage|undefined>();
	const isProcessing = reader != undefined
	const [progressingType, setProgressingType] = useState<progressingType|undefined>();


	async function subscribe() {
		const reader = await ChatAPI.subscribeChat(sessionId)
		setReader(reader)
	}

	async function fetchSession(){
		const session = await ChatAPI.getSession(sessionId).then(res=>res.data)
		if(session){
			setSession({
				id : session.id,
				title : session.title
			})
			setMessages(session.messages)
		}
	}

	async function sendMessage(message: string, modelId: string){
		fetchSession()
		setProgressingMessage(undefined)
		const reader = await ChatAPI.sendMessage(sessionId, {message: message, modelId: modelId})
		setReader(reader)
		setMessages(prev=>[...prev,{type: ChatMessageTypeEnum.enum.USER, text: message}])
	}

	useEffect(() => {
		if(reader){
			processingReader()
		}
	}, [reader]);

	async function processingReader(){
		if(!reader) return;

		const decoder = new TextDecoder();
		let isThinking = false;
		let isToken = false;
		let isFunctionCall = false;

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
				if(json.type == "thinking"){
					isThinking = true;
					setProgressingType("thinking")
					const needReset = isToken || isFunctionCall;
					processing("thinking", json.content, needReset)
					if(needReset){
						isToken = false;
						isFunctionCall = false;
						fetchSession()
					}
				}
				if(json.type == "token"){
					isToken = true;

					setProgressingType("token")
					processing("token", json.content)
				}
				if(json.type == "function_call"){
					isFunctionCall = true;
					const needReset = isToken;

					setProgressingType("function_call")
					processing("function_call", json.content, needReset)
					if(needReset){
						isToken = false;
						isThinking = false;
						fetchSession()
					}
				}
			}
		}
		setReader(undefined)
		fetchSession()
		setProgressingType(undefined)
	}

	function processing(emmitType: string, content : string, reset? : boolean){
		setProgressingMessage(prev=>{
			const m = reset || prev == undefined ?
				{
					type: ChatMessageTypeEnum.enum.AI,
					text: "",
					thinking: "",
					toolRequests: []
				}
				:  prev
			switch (emmitType){
				case "token":
					return {
						...m,
						text: m.text + content
					}
				case "thinking":
					return {
						...m,
						thinking: m.thinking + content
					}
				case "function_call":
					return {
						...m,
						toolRequests: [...m.toolRequests??[],content]
					}
				default:
					return m
			}
		})
	}

	return {subscribe, reader, fetchSession, session, messages, sendMessage, isProcessing, progressingMessage, progressingType}
}