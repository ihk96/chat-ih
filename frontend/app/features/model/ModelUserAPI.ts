import {client, getDefaultOption} from "~/lib/APIClient";
import type {RestResponse} from "~/features/common/types";
import type {LLModel, UserLLModel} from "~/features/model/types";

export default {
	getModels : async function(request? : Request){
		const response = await client.get<RestResponse<UserLLModel[]>>("/v1/models", getDefaultOption(request))
		return response.data
	}
}