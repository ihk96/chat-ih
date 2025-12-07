import {
    SidebarGroup,
    SidebarGroupContent,
    SidebarMenu,
    SidebarMenuButton,
    SidebarMenuItem
} from "~/components/ui/sidebar";
import {Link} from "react-router";
import {GalleryVerticalEnd, MessageCircle, MessageCirclePlus} from "lucide-react";

export default function CommonMenuGroup() {

    return (
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
    )
}