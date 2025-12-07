import {Collapsible, CollapsibleContent, CollapsibleTrigger} from "~/components/ui/collapsible";
import {
    SidebarGroup,
    SidebarGroupContent,
    SidebarGroupLabel,
    SidebarMenu, SidebarMenuAction, SidebarMenuButton,
    SidebarMenuItem, useSidebar
} from "~/components/ui/sidebar";
import {ChevronDown, MoreHorizontal} from "lucide-react";
import {Link} from "react-router";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuSeparator,
    DropdownMenuTrigger
} from "~/components/ui/dropdown-menu";
import {useIsMobile} from "~/hooks/use-mobile";

export default function PersonalMenuGroup() {
    const {open, toggleSidebar, openMobile} = useSidebar();
    const isMobile = useIsMobile();


    return (
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
                                        <Link to={"/chat/wewqe-34231qwewqedsda-ewqe"}>
                                            <span>Greetings</span>
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
    )
}