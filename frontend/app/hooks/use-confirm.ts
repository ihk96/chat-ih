import {type ReactNode, useContext} from "react";
import {type Confirm, ConfirmContext} from "~/context/ConfirmContext";




export function useConfirm() {
	const {setConfirm} = useContext(ConfirmContext);

	function confirm(title : ReactNode, description? : ReactNode) : Promise<boolean>{
		const promise = new Promise((resolve)=>{
			const newConfirm : Confirm = {
				title : title,
				description : description,
				resolve : resolve
			}
			setConfirm(newConfirm);
		});
		//@ts-ignore
		return promise
	}

	return confirm
}