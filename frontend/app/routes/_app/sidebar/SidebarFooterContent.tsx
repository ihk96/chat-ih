import {SidebarMenu, SidebarMenuButton, SidebarMenuItem, useSidebar} from "~/components/ui/sidebar";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuSeparator,
    DropdownMenuTrigger
} from "~/components/ui/dropdown-menu";
import {ChevronUp, User2} from "lucide-react";
import {cn} from "~/lib/utils";
import {useIsMobile} from "~/hooks/use-mobile";
import {useNavigate} from "react-router";

export default function SidebarFooterContent() {
    const {open, toggleSidebar, openMobile} = useSidebar();
    const isMobile = useIsMobile();

    return (
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
                        <UserDropdownMenuItems />
                    </DropdownMenuContent>
                </DropdownMenu>
            </SidebarMenuItem>
        </SidebarMenu>
    )
}

function UserDropdownMenuItems() {
    const naviagate = useNavigate();

    return (
        <>
            <DropdownMenuItem>
                <span>설정</span>
            </DropdownMenuItem>
            <DropdownMenuItem onSelect={() => naviagate("/admin")}>
                <span>관리자 패널</span>
            </DropdownMenuItem>
            <DropdownMenuSeparator />
            <DropdownMenuItem>
                <span>도움말</span>
            </DropdownMenuItem>
            <DropdownMenuSeparator />
            <DropdownMenuItem>
                <span>로그아웃</span>
            </DropdownMenuItem>
        </>
    )
}