import {Link} from "react-router";

export default function AppIndex(){

	return (
		<div>
			<Link to={"/chat"}>Chat</Link>
		</div>
	)
}