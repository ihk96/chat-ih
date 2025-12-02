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
				<SidebarGroup >
					<SidebarGroupContent className={"flex flex-col gap-1"}>
						<SidebarMenu>
							<SidebarMenuItem>
								<SidebarMenuButton asChild className={"bg-stone-500 text-white hover:bg-stone-600 hover:text-white"}>
									<Link to={"/chat/new"}>
										<MessageCirclePlus />
										<span>새 채팅</span>
									</Link>
								</SidebarMenuButton>
							</SidebarMenuItem>
						</SidebarMenu>
						<SidebarMenu>
							<SidebarMenuItem>
								<SidebarMenuButton asChild>
									<Link to={"/chat"}>
										<MessageCircle />
										<span>채팅</span>
									</Link>
								</SidebarMenuButton>
							</SidebarMenuItem>
							<SidebarMenuItem>
								<SidebarMenuButton asChild>
									<Link to={"/chat"}>
										<GalleryVerticalEnd />
										<span>프로젝트</span>
									</Link>
								</SidebarMenuButton>
							</SidebarMenuItem>
						</SidebarMenu>
					</SidebarGroupContent>
				</SidebarGroup>

				{
					open &&

					<>
						<Collapsible defaultOpen className="group/collapsible">
							<SidebarGroup>
								<SidebarGroupLabel asChild>
									<CollapsibleTrigger className={"cursor-pointer"}>
										즐겨찾기
										<ChevronDown className="ml-auto transition-transform group-data-[state=open]/collapsible:rotate-180" />
									</CollapsibleTrigger>
								</SidebarGroupLabel>
								<CollapsibleContent>
								</CollapsibleContent>
							</SidebarGroup>
						</Collapsible>
						<Collapsible defaultOpen className="group/collapsible">
							<SidebarGroup>
								<SidebarGroupLabel asChild>
									<CollapsibleTrigger className={"cursor-pointer"}>
										최근 항목
										<ChevronDown className="ml-auto transition-transform group-data-[state=open]/collapsible:rotate-180" />
									</CollapsibleTrigger>
								</SidebarGroupLabel>
								<CollapsibleContent>
									<SidebarGroupContent className={"flex flex-col gap-1"}>
										<SidebarMenu>
											<SidebarMenuItem>
												<SidebarMenuButton asChild>
													<Link to={"/chat"}>
														<span>프로젝트</span>
													</Link>
												</SidebarMenuButton>
												<DropdownMenu>
													<DropdownMenuTrigger asChild>
														<SidebarMenuAction
															className={"cursor-pointer"}
															showOnHover
														>
															<MoreHorizontal />
														</SidebarMenuAction>
													</DropdownMenuTrigger>
													<DropdownMenuContent
														className="w-24 rounded-lg"
														side={isMobile ? "bottom" : "right"}
														align={isMobile ? "end" : "start"}
													>
														<DropdownMenuItem>
															<span>Open</span>
														</DropdownMenuItem>
														<DropdownMenuItem>
															<span>Share</span>
														</DropdownMenuItem>
														<DropdownMenuSeparator />
														<DropdownMenuItem variant="destructive">
															<span>Delete</span>
														</DropdownMenuItem>
													</DropdownMenuContent>
												</DropdownMenu>
											</SidebarMenuItem>
										</SidebarMenu>
									</SidebarGroupContent>
								</CollapsibleContent>
							</SidebarGroup>
						</Collapsible>
					</>
				}
			</SidebarContent>
			<SidebarFooter className={"px-4 pb-4"}>
				<SidebarMenu>
					<SidebarMenuItem>
						<DropdownMenu modal={false}>
							<DropdownMenuTrigger asChild className={"cursor-pointer"}>
								<SidebarMenuButton>
									<User2 /> Username
									<ChevronUp className="ml-auto" />
								</SidebarMenuButton>
							</DropdownMenuTrigger>
							<DropdownMenuContent
								side="top"
								className={cn(open ? "w-56" : "w-60")}
								align={"start"}
								alignOffset={open ? 0 : 2}
							>
								<DropdownMenuItem>
									<span>Account</span>
								</DropdownMenuItem>
								<DropdownMenuItem>
									<span>Billing</span>
								</DropdownMenuItem>
								<DropdownMenuItem>
									<span>Sign out</span>
								</DropdownMenuItem>
							</DropdownMenuContent>
						</DropdownMenu>
					</SidebarMenuItem>
				</SidebarMenu>
			</SidebarFooter>
		</Sidebar>
	)
}