import {type Route} from "../../.react-router/types/app/routes/+types/_app.admin.provider";
import ProviderAdminAPI from "~/features/provider/ProviderAdminAPI";
import {useLoaderData, useRevalidator} from "react-router";
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from "~/components/ui/table";
import {Button} from "~/components/ui/button";
import {
    Dialog, DialogClose,
    DialogContent,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "~/components/ui/dialog";
import {Input} from "~/components/ui/input";
import {useEffect, useState} from "react";
import {z} from "zod/v4";
import {ProviderEnum} from "~/features/provider/schemes";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {cn} from "~/lib/utils";
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from "~/components/ui/form";
import {
    Select,
    SelectContent,
    SelectGroup,
    SelectItem,
    SelectLabel,
    SelectTrigger,
    SelectValue
} from "~/components/ui/select";
import {FieldError} from "~/components/ui/field";
import {Skeleton} from "~/components/ui/skeleton";
import {Pencil, Trash} from "lucide-react";
import {Tooltip, TooltipContent, TooltipTrigger} from "~/components/ui/tooltip";
import {useConfirm} from "~/hooks/use-confirm";
import {toast} from "sonner";

export async function loader({request} : Route.LoaderArgs){

    const providers = await ProviderAdminAPI.getProviders(request).then(res => res.data).catch(err => console.error(err));

    return {
        providers
    }
}

export default function AdminProvider() {
    const {providers} = useLoaderData<typeof loader>();
    const [dialogOpen, setDialogOpen] = useState(false);
    const [selectedProviderId, setSelectedProviderId] = useState<string>();

    const confirm = useConfirm()

    return (
        <div className={"p-4"}>
            <div className={"flex justify-end"}>
                <Button onClick={()=>{setDialogOpen(true); setSelectedProviderId("")}}>
                    공급자 추가
                </Button>
            </div>
            <Table className={"w-full"}>
                <TableHeader>
                    <TableRow>
                        <TableHead>이름</TableHead>
                        <TableHead>공급자 유형</TableHead>
                        <TableHead></TableHead>
                    </TableRow>
                </TableHeader>
                <TableBody>
                    {providers && providers.map(provider => (
                        <TableRow key={provider.id}>
                            <TableCell>{provider.name}</TableCell>
                            <TableCell>{provider.provider}</TableCell>
                            <TableCell>
                                <div className={"flex gap-2 justify-end"}>
                                    <Tooltip delayDuration={500}>
                                        <TooltipTrigger>
                                            <Button size={"icon-sm"} onClick={()=>{setDialogOpen(true); setSelectedProviderId(provider.id)}}>
                                                <Pencil />
                                            </Button>
                                        </TooltipTrigger>
                                        <TooltipContent>
                                            Edit
                                        </TooltipContent>
                                    </Tooltip>
                                    <Tooltip delayDuration={500}>
                                        <TooltipTrigger>
                                            <Button size={"icon-sm"} onClick={()=>{
                                                confirm("Delete Provider","Are you sure you want to delete this provider")
                                            }}>
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
                    ))}
                </TableBody>
            </Table>
            <ProviderDialog isOpen={dialogOpen} onClose={()=>{setDialogOpen(false); setSelectedProviderId(undefined)}} providerId={selectedProviderId}/>
        </div>
    )
}



const ProviderFormSchama = z.object({
    name: z.string().nonempty("Name is required."),
    provider: ProviderEnum,
    baseUrl: z.string().optional(),
    apiKey: z.string().optional(),
})

function ProviderDialog(props : {
    isOpen? : boolean,
    onClose? : () => void
    providerId? : string
}){
    const revalidator = useRevalidator();
    const [ready, setReady] = useState(false);
    const form = useForm<z.infer<typeof ProviderFormSchama>>({
        resolver: zodResolver(ProviderFormSchama),
        defaultValues: {
            name: "",
            provider: ProviderEnum.enum.OPENAI,
            baseUrl: "",
            apiKey: ""
        },
        mode: "onChange"
    });

    useEffect(() => {
        if(props.providerId){
            ProviderAdminAPI.getProvider(props.providerId)
                .then(res=> {
                    if(res){
                        return res.data
                    } else {
                        throw new Error("Provider not found.")
                    }
                })
                .then(data=>{
                    if(data){
                        form.setValue("name", data.name)
                        form.setValue("provider", data.provider)
                        form.setValue("baseUrl", data.baseUrl)
                        form.setValue("apiKey", data.apiKey)
                        setReady(true)
                    }
                })
        } else {
            setReady(true)
        }
    }, [props.providerId]);


    function handleClose(){
        props.onClose?.();
        setTimeout(()=>{
            form.reset();
            if(props.providerId) setReady(false);
        }, 100);
    }

    const watchProvider = form.watch("provider")
    useEffect(() => {
        if(watchProvider !== ProviderEnum.enum.OPENAI_COMPATIBLE){
            form.setValue("baseUrl", "")
        }
    }, [watchProvider]);


    function submitProvider(values : z.infer<typeof ProviderFormSchama>){
        if(props.providerId){
            ProviderAdminAPI.updateProvider(props.providerId, {
                name : values.name,
                provider: values.provider,
                baseUrl: values.baseUrl,
                apiKey: values.apiKey,
            }).then(()=>{
                revalidator.revalidate();
                handleClose();
                toast.success("Provider has been updated",{
                    position: "top-center",
                    duration: Infinity,
                    closeButton : true
                })
            })
        } else {
            ProviderAdminAPI.createProvider({
                name : values.name,
                provider: values.provider,
                baseUrl: values.baseUrl,
                apiKey: values.apiKey,
            }).then(()=>{
                revalidator.revalidate();
                handleClose();
                toast.success("Provider has been created",{
                    position: "top-center",
                    duration: Infinity,
                    closeButton : true
                })
            })
        }
    }

    return (
        <Dialog open={props.isOpen} onOpenChange={(open)=>{if(!open) handleClose()}}>
            <Form {...form}>
                <form onSubmit={form.handleSubmit(submitProvider)}>
                    <DialogContent className="sm:max-w-[425px]">
                        <DialogHeader>
                            <DialogTitle>Edit Provider</DialogTitle>
                        </DialogHeader>
                        {
                            ready ?
                                (
                                    <div className={cn("flex flex-col gap-6")}>
                                        <div className="grid gap-2">
                                            <FormField control={form.control}
                                                       name={"name"}
                                                       render={({field, fieldState})=>(
                                                           <FormItem>
                                                               <FormLabel><span>Name <span className={"text-red-500"}>*</span></span></FormLabel>
                                                               <FormControl>
                                                                   <Input placeholder={"name"}
                                                                          {...field}
                                                                   />
                                                               </FormControl>
                                                               {fieldState.invalid && (
                                                                   <FieldError errors={[fieldState.error]} />
                                                               )}
                                                           </FormItem>
                                                       )}
                                            />
                                        </div>
                                        <div className="grid gap-2">
                                            <FormField control={form.control}
                                                       name={"provider"}
                                                       render={({field})=>(
                                                           <FormItem>
                                                               <FormLabel>Provider</FormLabel>
                                                               <FormControl>
                                                                   <Select name={field.name}
                                                                           value={field.value}
                                                                           onValueChange={field.onChange}
                                                                   >
                                                                       <SelectTrigger className="w-[250px]">
                                                                           <SelectValue placeholder="Select a Provider" />
                                                                       </SelectTrigger>
                                                                       <SelectContent>
                                                                           <SelectGroup>
                                                                               <SelectLabel>Provider</SelectLabel>
                                                                               <SelectItem value={ProviderEnum.enum.OPENAI}>Open AI</SelectItem>
                                                                               <SelectItem value={ProviderEnum.enum.GOOGLE}>Google</SelectItem>
                                                                               <SelectItem value={ProviderEnum.enum.ANTHROPIC}>Anthropic</SelectItem>
                                                                               <SelectItem value={ProviderEnum.enum.OPENAI_COMPATIBLE}>Open AI Compatible</SelectItem>
                                                                           </SelectGroup>
                                                                       </SelectContent>
                                                                   </Select>
                                                               </FormControl>
                                                               <FormMessage />
                                                           </FormItem>
                                                       )}
                                            />
                                        </div>
                                        {
                                            watchProvider === ProviderEnum.enum.OPENAI_COMPATIBLE ? (
                                                <div className="grid gap-2">
                                                    <FormField control={form.control}
                                                               name={"baseUrl"}
                                                               render={({field})=>(
                                                                   <FormItem>
                                                                       <FormLabel>Base URL</FormLabel>
                                                                       <FormControl>
                                                                           <Input placeholder={"Base URL"}
                                                                                  {...field}
                                                                           />
                                                                       </FormControl>
                                                                       <FormMessage />
                                                                   </FormItem>
                                                               )}
                                                    />
                                                </div>
                                            ) : ""
                                        }
                                        <div className="grid gap-2">
                                            <FormField control={form.control}
                                                       name={"apiKey"}
                                                       render={({field})=>(
                                                           <FormItem>
                                                               <FormLabel>API Key</FormLabel>
                                                               <FormControl>
                                                                   <Input placeholder={"apiKey"}
                                                                          {...field}
                                                                   />
                                                               </FormControl>
                                                               <FormMessage />
                                                           </FormItem>
                                                       )}
                                            />
                                        </div>
                                    </div>
                                )
                            :

                                (
                                    <Skeleton className={"h-56 w-full"}/>
                                )
                        }
                        <DialogFooter>
                            <DialogClose asChild>
                                <Button variant="outline">Cancel</Button>
                            </DialogClose>
                            <Button type="submit" onClick={form.handleSubmit(submitProvider)}>Save</Button>
                        </DialogFooter>
                    </DialogContent>
                </form>
            </Form>
        </Dialog>
    )
}
