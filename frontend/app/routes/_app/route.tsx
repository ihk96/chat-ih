import {Outlet} from "react-router";
import AppSidebar from "~/routes/_app/AppSideBar";
import {SidebarInset, SidebarProvider, SidebarTrigger} from "~/components/ui/sidebar";
import {ScrollArea} from "~/components/ui/scroll-area";

export default function AppLayout(){

	return (
		<SidebarProvider
			style={{
				//@ts-ignore
				"--sidebar-width-icon": "4rem",
			}}
		>
			<AppSidebar />
			<SidebarInset className={"h-screen"}>
				<div className={"px-4 py-2 border-b"}>
					<SidebarTrigger className={"cursor-pointer"} />
				</div>
				{/*<div className={"h-[calc(100%-28px)]"}>*/}
					<ScrollArea className={"h-[calc(100%-45px)] w-full"}>
						<Outlet />
					</ScrollArea>
				{/*</div>*/}
			</SidebarInset>
		</SidebarProvider>
	)
}