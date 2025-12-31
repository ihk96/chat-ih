import { http, HttpResponse } from 'msw'
import { getHost } from '../utils'
import type {ChatMessage, ChatSession} from '~/features/chat/types'
import {ChatAttachmentTypeEnum} from "~/features/chat/schemes";

const host = getHost()

const sessions: Record<string, ChatSession> = {
	'test-session-id': {
		id: 'test-session-id',
		userId: 'user-1',
		title: '테스트 채팅 세션',
	}
}
const attachments: Record<string, string> = {
	'test-attachment-id': 'test-attachment-content'
}
const messages : ChatMessage[] = [
	{ type: 'USER', text: '안녕하세요', attachments: [{id: 'test-attachment-id', contentType: 'DOCUMENT', fileName: "test file.txt"}, {id: 'test-attachment-id2', contentType: 'DOCUMENT', fileName: "test file.pdf"}]
	},
	{ type: 'AI', text: '안녕하세요! 무엇을 도와드릴까요?' }
]

export const chatHandlers = [
	http.post(`${host}/v1/chat/sessions`, async ({ request }) => {
		const { message, modelId, attachments } = await request.json() as { message: string, modelId: string, attachments: string[] }
		const sessionId = Math.random().toString(36).substring(2, 9)
		
		sessions[sessionId] = {
			id: sessionId,
			userId: 'user-1',
			title: message.substring(0, 10),
		}

		return HttpResponse.json({
			data: sessionId
		})
	}),
	http.get(`${host}/v1/chat/sessions/:sessionId/messages`, () => {
		return HttpResponse.json({
			data: messages
		})
	}),

	http.get(`${host}/v1/chat/sessions/:sessionId`, ({ params }) => {
		const { sessionId } = params as { sessionId: string }
		const session = sessions[sessionId]

		if (!session) {
			return HttpResponse.json({
				data : {
					id: 'test-session-id2',
					userId: 'user-1',
					title: '테스트 채팅 세션',
				}
			})
		}

		return HttpResponse.json({
			data: session
		})
	}),
	http.post(`${host}/v1/chat/attachment/upload`, async ({ params, request }) => {

		return HttpResponse.json({
			data: {
				id: 'test-attachment-id',
				contentType: ChatAttachmentTypeEnum.enum.DOCUMENT,
				fileName: "test file.txt"
			}
		})
	}),
	http.get(`${host}/v1/chat/sessions/:sessionId/subscribe`,()=>{
		const stream = new ReadableStream({
			start(controller){
				
				sendThinkingEvnet(controller, "안녕하세요.")
				setTimeout(()=>{
					sendThinkingEvnet(controller, "사고 내용")
				},1000)
				setTimeout(()=>{
					sendThinkingEvnet(controller, "사고 내용")
				},2000)
				setTimeout(()=>{
					sendFunctionCallEvnet(controller, "도구호출 이름")
				},3000)
				setTimeout(()=>{
					sendThinkingEvnet(controller, "사고 내용")
				},4000)
				setTimeout(()=>{
					sendThinkingEvnet(controller, "사고 내용")
				},5000)
				setTimeout(()=>{
					sendFunctionCallEvnet(controller, "도구 호출 이름")
				},6000)
				setTimeout(()=>{
					sendThinkingEvnet(controller, "사고 내용")
				},7000)
				setTimeout(()=>{
					sendThinkingEvnet(controller, "사고 내용")
				},8000)
				setTimeout(()=>{
					sendTokenEvnet(controller, "응답 내용")
				},9000)
				setTimeout(()=>{
					sendTokenEvnet(controller, "응답 내용")
				},10000)
				setTimeout(()=>{
					sendTokenEvnet(controller, "응답 내용")
				},11000)
				setTimeout(()=>{
					controller.close()
				},12000)

				
			}
		})

		return new HttpResponse(stream, {
			headers: {
				'Content-Type': 'text/event-stream',
				'Cache-Control': 'no-cache',
				'Connection': 'keep-alive'
			}
		})
	}),
	http.post(`${host}/v1/chat/sessions/:sessionId/messages`,()=>{
		const stream = new ReadableStream({
			start(controller){

				sendThinkingEvnet(controller, "안녕하세요.")
				setTimeout(()=>{
					sendThinkingEvnet(controller, "사고 내용")
				},1000)
				setTimeout(()=>{
					sendThinkingEvnet(controller, "사고 내용")
				},2000)
				setTimeout(()=>{
					sendFunctionCallEvnet(controller, "도구호출 이름")
				},3000)
				setTimeout(()=>{
					sendThinkingEvnet(controller, "사고 내용")
				},4000)
				setTimeout(()=>{
					sendThinkingEvnet(controller, "사고 내용")
				},5000)
				setTimeout(()=>{
					sendFunctionCallEvnet(controller, "도구 호출 이름")
				},6000)
				setTimeout(()=>{
					sendThinkingEvnet(controller, "사고 내용")
				},7000)
				setTimeout(()=>{
					sendThinkingEvnet(controller, "사고 내용")
				},8000)
				setTimeout(()=>{
					sendTokenEvnet(controller, "응답 내용")
				},9000)
				setTimeout(()=>{
					sendTokenEvnet(controller, "응답 내용")
				},10000)
				setTimeout(()=>{
					sendTokenEvnet(controller, "응답 내용")
				},11000)
				setTimeout(()=>{
					controller.close()
				},12000)


			}
		})

		return new HttpResponse(stream, {
			headers: {
				'Content-Type': 'text/event-stream',
				'Cache-Control': 'no-cache',
				'Connection': 'keep-alive'
			}
		})
	})

]


const encoder = new TextEncoder()
const sendEvent = (controller : ReadableStreamDefaultController<any>, data: string) => {
	controller.enqueue(encoder.encode(`data: ${data}\n\n`))
}
function sendThinkingEvnet(controller:  ReadableStreamDefaultController<any>, content: string){
	sendEvent(controller,JSON.stringify({
		type: "thinking",
		content : content
	}))
}
function sendFunctionCallEvnet(controller:  ReadableStreamDefaultController<any>, content: string){
	sendEvent(controller,JSON.stringify({
		type: "function_call",
		content : content
	}))
}
function sendTokenEvnet(controller:  ReadableStreamDefaultController<any>, content: string){
	sendEvent(controller,JSON.stringify({
		type: "token",
		content : content
	}))
}

