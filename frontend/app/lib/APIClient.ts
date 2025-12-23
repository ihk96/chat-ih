import axios, {type AxiosRequestConfig} from "axios";
import {redirect} from "react-router";

export const API_SERVER = import.meta.env.VITE_API_SERVER;

export const client = (()=>{

	try {
		if(window){

		}
		const axiosInstance = axios.create({
			// @ts-ignore
			baseURL : import.meta.env.VITE_API_SERVER,
			withCredentials : true
		});
		axiosInstance.interceptors.response.use(response=>{
			return response;
		},error=>{
			if(error.status == 401){
				location.href="/login";
			}
			throw error;
		});

		return axiosInstance;

	} catch (error) {
		const axiosInstance = axios.create({
			baseURL : process.env.API_SERVER
		});

		axiosInstance.interceptors.response.use(response=>{
			return response;
		},(error)=>{
			if(error.status == 401){
				throw redirect("/login");
			}
		});

		return axiosInstance;
	}

})();

export function getDefaultOption(request?:Request){
	return request ? getServerRequestOption(request) : undefined;
}
function getServerRequestOption(request:Request){
	return {
		headers : {
			Cookie : request.headers.get("Cookie"),
			"x-forwarded-for" : request.headers.get("x-forwarded-for")
		}
	} as AxiosRequestConfig
}