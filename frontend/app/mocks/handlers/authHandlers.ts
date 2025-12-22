import { http, HttpResponse } from 'msw'
import { getHost } from '../utils'

const host = getHost()

export const authHandlers = [
	http.post(`${host}/v1/auth/login`, async ({ request }) => {
		const { username } = await request.json() as any
		return HttpResponse.json({
			data: {
				id: '1',
				username: username,
				token: 'mock-token'
			},
			message: 'Login successful'
		})
	}),
	http.post(`${host}/v1/auth/register`, async ({ request }) => {
		const { username } = await request.json() as any
		return HttpResponse.json({
			data: {
				id: '1',
				username: username
			},
			message: 'Registration successful'
		})
	})
]
