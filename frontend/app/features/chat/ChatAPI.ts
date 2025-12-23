import {API_SERVER, client, getDefaultOption} from "~/lib/APIClient";
import type {RestResponse} from "~/features/common/types";
import type {ChatSession} from "~/features/chat/types";

export default {
	initChat : async function(args : {message: string, modelId : string}, request? : Request){
		const response = await client.post<RestResponse<string>>("/v1/chat/sessions", {
			message: args.message,
			model: args.modelId
		}, getDefaultOption(request))
		return response.data
	},
	subscribeChat : async function(sessionId : string){
		const response = await fetch(`${API_SERVER}/v1/chat/sessions/${sessionId}/subscribe`, {
			method: 'GET',
			credentials : 'include',
			headers: {
				'Content-Type': 'application/json',
				'Accept': 'text/event-stream'
			},
		})

		if(response.status === 401) {
			location.href="/login";
			return;
		}

		if(!response.ok) throw new Error(`Failed to subscribe to chat session: ${response.status} ${response.statusText}`)

		return response.body?.getReader()
	},
	sendMessage : async function(sessionId : string, args: { message: string, modelId: string }){
		const response = await fetch(`${API_SERVER}/v1/chat/sessions/${sessionId}/messages`, {
			method: 'POST',
			credentials : 'include',
			headers: {
				'Content-Type': 'application/json',
				'Accept': 'text/event-stream'
			},
			body: JSON.stringify({
				message: args.message,
				model: args.modelId
			})
		})

		if(response.status === 401) {
			location.href="/login";
			return;
		}

		if(!response.ok) throw new Error(`Failed to send message to chat session: ${response.status} ${response.statusText}`)

		return response.body?.getReader()
	},
	getSession : async function(sessionId : string, request? : Request){
		const response = await client.get<RestResponse<ChatSession>>(`/v1/chat/sessions/${sessionId}`, getDefaultOption(request))
		return response.data
	},
	deleteSession : async function(sessionId : string, request? : Request){
		const response = await client.delete(`/v1/chat/sessions/${sessionId}`, getDefaultOption(request))
		return response
	}
}