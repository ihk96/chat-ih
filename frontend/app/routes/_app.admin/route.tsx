import {ScrollArea} from "~/components/ui/scroll-area";
import {Button} from "~/components/ui/button";
import {cn} from "~/lib/utils";
import {MAIN_HEIGHT_CLASS} from "~/lib/style_variables";
import {Outlet, useLocation, useNavigate} from "react-router";

export default function AdminLayout() {


    return (
        <div className={"flex"}>
            {/* Admin Sidebar */}
            <AdminSidebar />

            {/* Admin Content */}
            <div className={"flex-1"}>
                <Outlet />
            </div>
        </div>
    )
}


function AdminSidebar() {
    const location = useLocation();
    const path = location.pathname;

    return (
        <div className={cn("border-r  w-64", MAIN_HEIGHT_CLASS)}>
            <ScrollArea className={"h-full w-full"}>
                <div className={"flex flex-col gap-1 p-4"}>
                    <AdminSidebarItem label={"일반"} to={"/admin/general"} isActive={path.startsWith("/admin/general")} />
                    <AdminSidebarItem label={"사용자"} to={"/admin/user"} isActive={path.startsWith("/admin/user")} />
                    <AdminSidebarItem label={"연결"} to={"/admin/connect"} isActive={path.startsWith("/admin/connect")} />
                    <AdminSidebarItem label={"모델"} to={"/admin/model"} isActive={path.startsWith("/admin/model")} />
                </div>
            </ScrollArea>
        </div>
    )
}

function AdminSidebarItem(props : {
    label : string,
    to : string,
    isActive ?: boolean
}) {
    const navigate = useNavigate();
    const {label, to, isActive = false} = props;

    return (
        <Button variant={"ghost"}
                className={cn("justify-start", isActive ? "bg-stone-300 hover:bg-stone-300" : "hover:bg-stone-200")}
                onClick={() => {
                    if(isActive) return;
                    navigate(to)}
                }
        >
            {label}
        </Button>
    )
}