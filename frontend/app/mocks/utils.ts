
export function getHost(){
	try {
		if(window){

		}
		return import.meta.env.VITE_API_SERVER;
	} catch {
		return process.env.API_SERVER
	}

}