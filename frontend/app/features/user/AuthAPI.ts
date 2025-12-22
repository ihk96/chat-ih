import {client} from "~/lib/APIClient";

export default {
	login : async function(username: string, password: string){
		const response = await client.post("/v1/auth/login", {username, password})
		return response
	},
	register : async function(username: string, password: string){
		const response = await client.post("/v1/auth/register", {username, password})
		return response
	}
}