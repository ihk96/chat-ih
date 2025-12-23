import { http, HttpResponse } from 'msw'
import { getHost } from '../utils'

const host = getHost()

export const userHandlers = [
	http.post(`${host}/v1/users/me`, async ({ request }) => {
		const { username } = await request.json() as any
		return HttpResponse.json({
			data: {
				id: '1',
				username: username,
				roles: "ROLE_ADMIN"
			},
		})
	}),
]
