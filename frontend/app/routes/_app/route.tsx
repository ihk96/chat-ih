import {Outlet} from "react-router";
import AppSidebar from "~/routes/_app/AppSideBar";
import {SidebarInset, SidebarProvider, SidebarTrigger} from "~/components/ui/sidebar";

export default function AppLayout(){

	return (
		<SidebarProvider
			style={{
				//@ts-ignore
				"--sidebar-width-icon": "4rem",
			}}
		>
			<AppSidebar />
			<SidebarInset className={"p-4 w-[]"}>
				<div>
					<SidebarTrigger className={"cursor-pointer"} />
				</div>
				<Outlet />
			</SidebarInset>
		</SidebarProvider>
	)
}