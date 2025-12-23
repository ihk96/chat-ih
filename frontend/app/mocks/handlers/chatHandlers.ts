import { http, HttpResponse } from 'msw'
import { getHost } from '../utils'
import type { ChatSession } from '~/features/chat/types'

const host = getHost()

const sessions: Record<string, ChatSession> = {
	'test-session-id': {
		id: 'test-session-id',
		userId: 'user-1',
		title: '테스트 채팅 세션',
		messages: [
			{ type: 'USER', text: '안녕하세요' },
			{ type: 'AI', text: '안녕하세요! 무엇을 도와드릴까요?' }
		]
	}
}

export const chatHandlers = [
	http.post(`${host}/v1/chat/sessions`, async ({ request }) => {
		const { message } = await request.json() as { message: string, modelId: string }
		const sessionId = Math.random().toString(36).substring(2, 9)
		
		sessions[sessionId] = {
			id: sessionId,
			userId: 'user-1',
			title: message.substring(0, 10),
			messages: [
				{ type: 'USER', text: message }
			]
		}

		return HttpResponse.json({
			data: sessionId
		})
	}),

	http.get(`${host}/v1/chat/sessions/:sessionId`, ({ params }) => {
		const { sessionId } = params as { sessionId: string }
		const session = sessions[sessionId]

		if (!session) {
			return HttpResponse.json({
				code: '404',
				message: 'Session not found'
			}, { status: 404 })
		}

		return HttpResponse.json({
			data: session
		})
	})
]
