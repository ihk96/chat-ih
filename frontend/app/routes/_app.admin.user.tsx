import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from "~/components/ui/table";
import {Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue} from "~/components/ui/select";
import {Tabs, TabsContent, TabsList, TabsTrigger} from "~/components/ui/tabs";
import {Button} from "~/components/ui/button";
import {
    Pagination,
    PaginationContent, PaginationEllipsis,
    PaginationItem,
    PaginationLink, PaginationNext,
    PaginationPrevious
} from "~/components/ui/pagination";
import {useMemo, useState} from "react";
import {Tooltip, TooltipContent, TooltipTrigger} from "~/components/ui/tooltip";
import {Pencil, Trash} from "lucide-react";
import {useConfirm} from "~/hooks/use-confirm";

export default function AdminUser() {
    const [page, setPage] = useState(1);

    return (
        <div className={"p-4"}>
            <Tabs defaultValue={"users"}
            >
                <TabsList className={'w-[300px]'}>
                    <TabsTrigger value={"users"}>사용자</TabsTrigger>
                    <TabsTrigger value={"groups"}>그룹</TabsTrigger>
                </TabsList>
                <TabsContent value={"users"}>
                    <UsersTable />
                    <TablePagination page={page} count={10} onChange={(page)=>setPage(page)} />
                </TabsContent>
                <TabsContent value={"groups"}>
                    <UserGroupTable />
                    <TablePagination />
                </TabsContent>
            </Tabs>
        </div>
    )
}

function UserGroupTable(){
    const confirm = useConfirm();

    return (
        <Table className={"w-full"}>
            <TableHeader>
                <TableRow>
                    <TableHead>그룹명</TableHead>
                    <TableHead>생성일</TableHead>
                    <TableHead></TableHead>
                </TableRow>
            </TableHeader>
            <TableBody>
                <TableRow>
                    <TableCell>테스터 그룹</TableCell>
                    <TableCell>{new Date().toLocaleString()}</TableCell>
                    <TableCell>
                        <div className={"flex gap-2 justify-end"}>
                            <Tooltip delayDuration={500}>
                                <TooltipTrigger>
                                    <Button size={"icon-sm"}
                                            // onClick={()=>{setDialogOpen(true); setSelectedProviderId(provider.id)}}
                                    >
                                        <Pencil />
                                    </Button>
                                </TooltipTrigger>
                                <TooltipContent>
                                    Edit
                                </TooltipContent>
                            </Tooltip>
                            <Tooltip delayDuration={500}>
                                <TooltipTrigger>
                                    <Button size={"icon-sm"}
                                            onClick={()=>{
                                                confirm("Delete Group","Are you sure you want to delete this Group")
                                            }}
                                    >
                                        <Trash />
                                    </Button>
                                </TooltipTrigger>
                                <TooltipContent>
                                    Delete
                                </TooltipContent>
                            </Tooltip>
                        </div>
                    </TableCell>
                </TableRow>
            </TableBody>
        </Table>
    )
}


function UsersTable(){
    const confirm = useConfirm();

    return (
        <Table className={"w-full"}>
            <TableHeader>
                <TableRow>
                    <TableHead>역할</TableHead>
                    <TableHead>ID</TableHead>
                    <TableHead>이름</TableHead>
                    <TableHead>이메일</TableHead>
                    <TableHead>마지막 로그인</TableHead>
                    <TableHead>생성일</TableHead>
                    <TableHead></TableHead>
                </TableRow>
            </TableHeader>
            <TableBody>
                <TableRow>
                    <TableCell>
                        <UserRoleSelect />
                    </TableCell>
                    <TableCell>admin</TableCell>
                    <TableCell>어드민</TableCell>
                    <TableCell>admin@admin.com</TableCell>
                    <TableCell>{new Date().toLocaleString()}</TableCell>
                    <TableCell>{new Date().toLocaleString()}</TableCell>
                    <TableCell>
                        <div className={"flex gap-2 justify-end"}>
                            <Tooltip delayDuration={500}>
                                <TooltipTrigger>
                                    <Button size={"icon-sm"}
                                        // onClick={()=>{setDialogOpen(true); setSelectedProviderId(provider.id)}}
                                    >
                                        <Pencil />
                                    </Button>
                                </TooltipTrigger>
                                <TooltipContent>
                                    Edit
                                </TooltipContent>
                            </Tooltip>
                            <Tooltip delayDuration={500}>
                                <TooltipTrigger>
                                    <Button size={"icon-sm"}
                                            onClick={()=>{
                                                confirm("Delete User","Are you sure you want to delete this User")
                                            }}
                                    >
                                        <Trash />
                                    </Button>
                                </TooltipTrigger>
                                <TooltipContent>
                                    Delete
                                </TooltipContent>
                            </Tooltip>
                        </div>
                    </TableCell>
                </TableRow>
            </TableBody>
        </Table>
    )
}


function UserRoleSelect(props : React.ComponentProps<typeof Select>){

    return (
        <Select {...props}>
            <SelectTrigger>
                <SelectValue placeholder="역할 선택" />
            </SelectTrigger>
            <SelectContent>
                <SelectGroup>
                    <SelectItem value="admin">관리자</SelectItem>
                    <SelectItem value="user">일반 사용자</SelectItem>
                </SelectGroup>
            </SelectContent>
        </Select>
    )
}

function TablePagination(props: {
    count? : number,
    page? : number,
    onChange? : (page : number) => void
}){
    const {count = 0, page = 1, onChange = ()=>{}} = props;

    const firstNumber = useMemo(()=> {
        let minus = 3
        if(page <= 5) minus = 4;
        let first = Math.max(Math.max(page - minus, 1),1)
        if(page > (count - 4)) first -= (page - count + 4);
        return first
    },[count, page]);
    const lastNumber = useMemo(()=> {
        let plus = 3
        if(count - page <= 4) plus = 4;
        let last = Math.min(Math.min(page + plus, 9), count)
        if(page < 5) last += (5 -page);
        return last + 1 === count ? count : last
    },[count, page]);

    const pages = useMemo(()=>Array.from({length: lastNumber - firstNumber + 1}, (_, i) => firstNumber + i),[count, page]);

    return (
        <Pagination>
            <PaginationContent>
                <PaginationItem>
                    <PaginationPrevious  />
                </PaginationItem>
                {
                    firstNumber > 1 &&
                    <>
                        <PaginationItem>
                            <PaginationLink onClick={()=>onChange(1)}>1</PaginationLink>
                        </PaginationItem>
                        <PaginationItem>
                            <PaginationEllipsis />
                        </PaginationItem>
                    </>
                }
                {
                    pages.map(p => (
                        <PaginationItem>
                            <PaginationLink isActive={p === page}
                                            onClick={()=>onChange(p)}
                            >
                                {p}
                            </PaginationLink>
                        </PaginationItem>
                    ))
                }
                {
                    lastNumber < count &&
                    <>
                        <PaginationItem>
                            <PaginationEllipsis />
                        </PaginationItem>
                        <PaginationItem>
                            <PaginationLink onClick={()=>onChange(count)}>{count}</PaginationLink>
                        </PaginationItem>
                    </>
                }
                <PaginationItem>
                    <PaginationNext/>
                </PaginationItem>
            </PaginationContent>
        </Pagination>
    )
}