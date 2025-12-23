import { http, HttpResponse } from 'msw'
import { getHost } from '../utils'

const host = getHost()

const models = [
	{ id: '1', publicName: 'GPT1', originName: 'gpt-5', providerId: '1', completionUrl: '/v1/chat/completions' },
	{ id: '2', publicName: 'GPT2', originName: 'gpt-5.1', providerId: '1', completionUrl: '/v1/chat/completions' },
	{ id: '3', publicName: 'Gemini', originName: 'gemini-2.5-flash', providerId: '2', completionUrl: '/v1/chat/completions' },
	{ id: '4', publicName: 'Qwen', originName: 'Qwen/Qwen3-32b', providerId: '3', completionUrl: '/v1/chat/completions' }
]

export const modelHandlers = [
	http.get(`${host}/v1/models`, () => {
		return HttpResponse.json({
			data: models
		})
	}),
	http.get(`${host}/v1/admin/models`, () => {
		return HttpResponse.json({
			data: models
		})
	}),
	http.get(`${host}/v1/admin/models/:id`, ({ params }) => {
		const { id } = params
		const model = models.find(m => m.id === id)
		if (!model) {
			return HttpResponse.json({ error: 'Model not found' }, { status: 404 })
		}
		return HttpResponse.json({
			data: model
		})
	}),
	http.post(`${host}/v1/admin/models`, async ({ request }) => {
		const body = await request.json() as any
		const newModel = {
			id: Math.random().toString(36).substring(2, 9),
			...body
		}
		models.push(newModel)
		return HttpResponse.json({
			data: newModel
		}, { status: 201 })
	}),
	http.put(`${host}/v1/admin/models/:id`, async ({ params, request }) => {
		const { id } = params
		const body = await request.json() as any
		const model = models.find(m => m.id === id)
		if (!model) {
			return HttpResponse.json({ error: 'Model not found' }, { status: 404 })
		}
		model.publicName = body.publicName || model.publicName
		model.originName = body.originName || model.originName
		model.providerId = body.providerId || model.providerId
		model.completionUrl = body.completionUrl || model.completionUrl
		return HttpResponse.json({
			data: model
		})
	}),
	http.delete(`${host}/v1/admin/models/:id`, ({ params }) => {
		const { id } = params
		const index = models.findIndex(m => m.id === id)
		if (index !== -1) {
			models.splice(index, 1)
		}
		return new HttpResponse(null, { status: 204 })
	})
]
