import {client, getDefaultOption} from "~/lib/APIClient";
import type {RestResponse} from "~/features/common/types";
import type {Provider} from "~/features/provider/types";

export default {
	getProviders : async function(request? : Request){
		const response = await client.get<RestResponse<Provider[]>>("/v1/admin/providers", getDefaultOption(request))
		return response.data
	},
	getProvider : async function(id : string, request? : Request){
		const response = await client.get<RestResponse<Provider>>(`/v1/admin/providers/${id}`, getDefaultOption(request))
		return response.data
	},
	getProviderModels : async function(id : string, request? : Request){
		const response = await client.get<RestResponse<string[]>>(`/v1/admin/providers/${id}/models`, getDefaultOption(request))
		return response.data
	},
	createProvider : async function(args : {
		name: string,
		provider: string,
		baseUrl?: string,
		apiKey?: string
	}, request? : Request){
		const response = await client.post<RestResponse<Provider>>("/v1/admin/providers", args, getDefaultOption(request))
		return response.data
	},
	updateProvider : async function(providerId: string, args: {
		name: string;
		provider: "OPENAI" | "OPENAI_COMPATIBLE" | "GOOGLE" | "ANTHROPIC";
		baseUrl: string | undefined;
		apiKey: string | undefined
	}) {
		const response = await client.put<RestResponse<Provider>>(`/v1/admin/providers/${providerId}`, args)
		return response.data
	}
}