import {
	Sidebar,
	SidebarContent,
	SidebarFooter,
	SidebarGroup, SidebarGroupContent, SidebarGroupLabel,
	SidebarHeader, SidebarMenu, SidebarMenuAction, SidebarMenuButton, SidebarMenuItem, SidebarTrigger, useSidebar,
} from "~/components/ui/sidebar"
import {
	ChevronDown,
	ChevronUp,
	GalleryVerticalEnd,
	MessageCircle,
	MessageCirclePlus,
	MoreHorizontal,
	User2
} from "lucide-react";
import {Link} from "react-router";
import {Collapsible, CollapsibleContent, CollapsibleTrigger} from "~/components/ui/collapsible";
import {cn} from "~/lib/utils";
import {
	DropdownMenu,
	DropdownMenuContent,
	DropdownMenuItem,
	DropdownMenuSeparator,
	DropdownMenuTrigger
} from "~/components/ui/dropdown-menu";
import {useIsMobile} from "~/hooks/use-mobile";
import CommonMenuGroup from "~/routes/_app/sidebar/CommonMenuGroup";
import SidebarFooterContent from "~/routes/_app/sidebar/SidebarFooterContent";
import PersonalMenuGroup from "~/routes/_app/sidebar/PersonalMenuGroup";

export default function AppSidebar() {
	const {open, toggleSidebar, openMobile} = useSidebar();
	const isMobile = useIsMobile();

	return (
		<Sidebar collapsible={"icon"}>
			<SidebarHeader className={"px-4 pt-4"}>
				<div className={"flex items-center justify-between"}>
					{
						open ? <h2 className={"text-xl font-bold"}>Chat IH</h2> : <h2 className={"text-xl font-bold text-center w-full"}>IH</h2>
					}
				</div>
			</SidebarHeader>
			<SidebarContent className={cn("px-2",open ? "" : "cursor-pointer")}
			                onClick={()=>{
				                if(!open) {
					                toggleSidebar()
				                }
			                }}
			>
				<CommonMenuGroup />

				{
					open &&
					<PersonalMenuGroup />
				}
			</SidebarContent>
			<SidebarFooter className={"px-4 pb-4"}>
				<SidebarFooterContent />
			</SidebarFooter>
		</Sidebar>
	)
}