import {Link, redirect} from "react-router";

export function loader(){
	return redirect("/chat/new")
}

export default function AppIndex(){

	return (
		<div>
		</div>
	)
}