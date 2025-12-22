import {createContext, type ReactNode, useContext, useEffect, useState} from "react";
import {
	Dialog, DialogClose,
	DialogContent,
	DialogDescription,
	DialogFooter,
	DialogHeader,
	DialogTitle
} from "~/components/ui/dialog";
import {Button} from "~/components/ui/button";

export type ConfirmContextType = {
	confirm? : Confirm
	setConfirm : (confirm : Confirm) => void;
	endConfirm : () => void;
}
export type Confirm = {
	title : ReactNode,
	description? : ReactNode,
	resolve : (value : any) => void
}

export const ConfirmContext = createContext<ConfirmContextType>({
	confirm : undefined,
	setConfirm : ()=>{},
	endConfirm : ()=>{},
});


export  function ConfirmContextProvider({
	                                             children
                                             } : {
	children : ReactNode
}){
	const [confirm, setConfirm] = useState<Confirm | undefined>(undefined);

	function endConfirm(){
		setConfirm(undefined);
	}

	return(
		<ConfirmContext.Provider value={{
			confirm,
			setConfirm,
			endConfirm,
		}}>
			{children}
			<ConfirmContextConsumer />
		</ConfirmContext.Provider>
	)
}

function ConfirmContextConsumer(){
	const {confirm, setConfirm, endConfirm} = useContext(ConfirmContext);
	const [open, setOpen] = useState(false);
	useEffect(() => {
		if(confirm){
			setOpen(true);
			setConfirm(confirm);
		}
	},[confirm]);

	const handleAgree = () => {
		confirm?.resolve(true)
		setOpen(false);
		setTimeout(()=>{
			endConfirm();
		},200)
	};
	const handleDisagree = () => {
		confirm?.resolve(false)
		setOpen(false);
		setTimeout(()=>{
			endConfirm();
		},200)
	};

	return (
		<Dialog open={open} onOpenChange={handleDisagree}>
			<form>
				<DialogContent className="sm:max-w-[425px]">
					<DialogHeader>
						<DialogTitle>{confirm?.title ??""}</DialogTitle>
						<DialogDescription>
							{confirm?.description??""}
						</DialogDescription>
					</DialogHeader>
					<div className="grid gap-4">
					</div>
					<DialogFooter>
						<DialogClose asChild>
							<Button variant="outline">Cancel</Button>
						</DialogClose>
						<Button onClick={handleAgree}>Confirm</Button>
					</DialogFooter>
				</DialogContent>
			</form>
		</Dialog>
	);
}