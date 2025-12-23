import {createContext, useContext} from "react";

export type MainScrollContextProps = {
	scrollRef : React.MutableRefObject<HTMLDivElement | null>
}

export const MainScrollContext = createContext<MainScrollContextProps | null>(null)

export function useMainScroll(){
	const context = useContext(MainScrollContext)
	if (!context) {
		throw new Error("useMainScroll must be used within a MainScrollProvider.")
	}

	return context

}