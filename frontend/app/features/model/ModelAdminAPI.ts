import {client, getDefaultOption} from "~/lib/APIClient";
import {id} from "zod/locales";
import type {LLModel} from "~/features/model/types";
import type {RestResponse} from "~/features/common/types";

export default {
	getModels : async function(request? : Request){
		const response = await client.get<RestResponse<LLModel[]>>("/v1/admin/models", getDefaultOption(request))
		return response.data
	},
	getModel : async function(id : string, request? : Request){
		const response = await client.get<RestResponse<LLModel>>(`/v1/admin/models/${id}`, getDefaultOption(request))
		return response.data
	},
	createModel : async function(args : {
		publicName: string,
		originName: string,
		providerId: string,
		completionUrl?: string
	}, request? : Request){
		const response = await client.post<RestResponse<LLModel>>("/v1/admin/models", args, getDefaultOption(request))
		return response.data
	},
	deleteModel : async function(id : string, request? : Request){
		const response = await client.delete(`/v1/admin/models/${id}`, getDefaultOption(request))
		return response
	},
	updateModel : async function(id : string, args : {
		publicName: string,
		originName: string,
		providerId: string,
		completionUrl?: string
	}, request? : Request){
		const response = await client.put<RestResponse<LLModel>>(`/v1/admin/models/${id}`, args, getDefaultOption(request))
		return response.data
	}

}