export type RestResponse<T> = {
	data?: T,
	code? : string,
	message? : string
}