import { http, HttpResponse } from 'msw'
import { getHost } from '../utils'
import {ProviderEnum} from "~/features/provider/schemes";

const host = getHost()

const providers = [
	{ id: '1', name: 'OpenAI', provider: ProviderEnum.enum.OPENAI, baseUrl: 'https://api.openai.com/v1' },
	{ id: '2', name: 'Google Gemini', provider: ProviderEnum.enum.GOOGLE, baseUrl: 'https://generativelanguage.googleapis.com' },
	{ id: '3', name: 'Local LLM', provider: ProviderEnum.enum.OPENAI_COMPATIBLE, baseUrl: 'http://localhost:14234' }
]

export const providerHandlers = [
	http.get(`${host}/v1/admin/providers`, () => {
		return HttpResponse.json({
			data: providers
		})
	}),
	http.get(`${host}/v1/admin/providers/:id`, ({ params }) => {
		const { id } = params
		const provider = providers.find(p => p.id === id)
		if (!provider) {
			return HttpResponse.json({ error: 'Provider not found' }, { status: 404 })
		}
		return HttpResponse.json({
			data: provider
		})
	}),
	http.get(`${host}/v1/admin/providers/:id/models`, () => {
		return HttpResponse.json({
			data: ["gpt-5","gpt-5.1"]
		})
	}),
	http.post(`${host}/v1/admin/providers`, async ({ request }) => {
		const body = await request.json() as any
		const newProvider = {
			id: Math.random().toString(36).substring(2, 9),
			name: body.publicName,
			provider: body.originName,
			baseUrl: body.completionUrl || ''
		}
		providers.push(newProvider)
		return HttpResponse.json({
			data: newProvider
		}, { status: 201 })
	}),
	http.put(`${host}/v1/admin/providers/:id`, async ({ params, request }) => {
		const { id } = params
		const body = await request.json() as any
		const provider = providers.find(p => p.id === id)
		if (!provider) {
			return HttpResponse.json({ error: 'Provider not found' }, { status: 404 })
		}
		provider.name = body.publicName || provider.name
		provider.provider = body.originName || provider.provider
		provider.baseUrl = body.completionUrl || provider.baseUrl
		return HttpResponse.json({
			data: {
				id,
				name: provider.name,
				provider: provider.provider,
				baseUrl: provider.baseUrl
			}
		})
	})
]
