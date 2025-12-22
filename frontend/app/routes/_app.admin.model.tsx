import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from "~/components/ui/table";
import {type Route} from "../../.react-router/types/app/routes/+types/_app.admin.model";
import ModelAdminAPI from "~/features/model/ModelAdminAPI";
import {useLoaderData, useRevalidator} from "react-router";
import {Button} from "~/components/ui/button";
import ProviderAdminAPI from "~/features/provider/ProviderAdminAPI";
import {Tooltip, TooltipContent, TooltipTrigger} from "~/components/ui/tooltip";
import {Pencil, Trash} from "lucide-react";
import {useConfirm} from "~/hooks/use-confirm";
import {ProviderEnum} from "~/features/provider/schemes";
import {useEffect, useMemo, useState} from "react";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {Dialog, DialogClose, DialogContent, DialogFooter, DialogHeader, DialogTitle} from "~/components/ui/dialog";
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from "~/components/ui/form";
import {cn} from "~/lib/utils";
import {Input} from "~/components/ui/input";
import {FieldError} from "~/components/ui/field";
import {
    Select,
    SelectContent,
    SelectGroup,
    SelectItem,
    SelectLabel,
    SelectTrigger,
    SelectValue
} from "~/components/ui/select";
import {Skeleton} from "~/components/ui/skeleton";
import {z} from "zod/v4";
import type {Provider} from "~/features/provider/types";
import {toast} from "sonner";

export async function loader({request} : Route.LoaderArgs){

    const models = await ModelAdminAPI.getModels(request).then(res => res.data).catch(err => console.error(err))
    const providers = await ProviderAdminAPI.getProviders(request).then(res => res.data).catch(err => console.error(err))

    return {
        models,
        providers
    }
}

export default function AdminModel() {
    const {models, providers} = useLoaderData<typeof loader>();
    const confirm = useConfirm()
    const [dialogOpen, setDialogOpen] = useState(false);
    const [selectedModelId, setSelectedModelId] = useState<string>();

    return (
        <div className={"p-4"}>
            <div className={"flex justify-end"}>
                <Button onClick={()=>{setDialogOpen(true); setSelectedModelId("")}}>
                    모델 추가
                </Button>
            </div>
            <Table className={"w-full"}>
                <TableHeader>
                    <TableRow>
                        <TableHead>공개 이름</TableHead>
                        <TableHead>원본 이름</TableHead>
                        <TableHead>공급자</TableHead>
                        <TableHead></TableHead>
                    </TableRow>
                </TableHeader>
                <TableBody>
                    {models && models.map(model => (
                        <TableRow key={model.id}>
                            <TableCell>{model.publicName}</TableCell>
                            <TableCell>{model.originName}</TableCell>
                            <TableCell>{providers?.find(provider => provider.id === model.providerId)?.name ?? ""}</TableCell>
                            <TableCell>
                                <div className={"flex gap-2 justify-end"}>
                                    <Tooltip delayDuration={500}>
                                        <TooltipTrigger>
                                            <Button size={"icon-sm"}
                                                    onClick={()=>{setDialogOpen(true); setSelectedModelId(model.id)}}
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
                                                        confirm("Delete Model","Are you sure you want to delete this model")
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
            <ModelDialogForm isOpen={dialogOpen} onClose={()=>{setDialogOpen(false); setSelectedModelId(undefined)}} modelId={selectedModelId}/>
        </div>
    )
}



const ModelFormSchama = z.object({
    publicName: z.string().nonempty("Public name is required."),
    originName: z.string().nonempty("Origin Model name is required."),
    providerId: z.string().nonempty("Provider is required."),
    completionUrl: z.string().optional()
})

function ModelDialogForm(props : {
    isOpen? : boolean,
    onClose? : () => void
    modelId? : string
}){
    const [ready, setReady] = useState(false);
    const [providers, setProviders] = useState<Provider[]>([]);
    const revalidator = useRevalidator();

    const form = useForm<z.infer<typeof ModelFormSchama>>({
        resolver: zodResolver(ModelFormSchama),
        defaultValues: {
            publicName: "",
            originName: "",
            providerId: "",
            completionUrl: "",
        },
        mode: "onChange"
    });

    useEffect(() => {
        if(props.isOpen){
            ProviderAdminAPI.getProviders()
                .then(res=>setProviders(res.data ?? []))
                .catch(err=>console.error(err))
        }
    }, [props.isOpen]);

    useEffect(() => {
        if(props.modelId){
            ModelAdminAPI.getModel(props.modelId)
                .then(res=> {
                    if(res){
                        return res.data
                    } else {
                        throw new Error("Model not found.")
                    }
                })
                .then(data=>{
                    if(data){
                        form.setValue("publicName", data.publicName)
                        form.setValue("originName", data.originName)
                        form.setValue("providerId", data.providerId)
                        form.setValue("completionUrl", data.completionUrl)
                        setReady(true)
                    }
                })
        } else {
            setReady(true)
        }
    }, [props.modelId]);


    function handleClose(){
        props.onClose?.();
        setTimeout(()=>{
            form.reset();
            if(props.modelId) setReady(false);
        }, 100);
    }

    const watchProvider = form.watch("providerId")

    const [selectedProvider, setSelectedProvider] = useState<Provider|undefined>(undefined)
    useEffect(()=>{
        if(watchProvider){
            ProviderAdminAPI.getProvider(watchProvider)
                .then(res=>setSelectedProvider(res.data))
                .catch(err=>console.error(err))
        } else {
            setSelectedProvider(undefined)
        }
    },[watchProvider])

    const modelSelectorType = useMemo(()=>{
        if(selectedProvider){
            return selectedProvider.provider === ProviderEnum.enum.OPENAI ? "openai" : "default"
        } else {
            return "default"
        }
    },[selectedProvider])

    useEffect(() => {
        if(selectedProvider){
            if(selectedProvider.provider !== ProviderEnum.enum.OPENAI_COMPATIBLE){
                form.setValue("completionUrl", "")
            }
        }
    }, [selectedProvider]);
    console.log(form.getValues())

    function submitModel(values : z.infer<typeof ModelFormSchama>){
        if(props.modelId){
            ModelAdminAPI.updateModel(props.modelId, {
                publicName : values.publicName,
                originName : values.originName,
                providerId : values.providerId,
                completionUrl : values.completionUrl,
            }).then(()=>{
                revalidator.revalidate()
                handleClose()
                toast.success("Model has been updated", {
                    closeButton: true,
                    duration : Infinity,
                    position : "top-center",
                })
            }).catch(err=>{
                console.error(err)
            })
        } else {
            ModelAdminAPI.createModel({
                publicName : values.publicName,
                originName : values.originName,
                providerId : values.providerId,
                completionUrl : values.completionUrl,
            }).then(()=>{
                revalidator.revalidate()
                handleClose()
                toast.success("Model has been created", {
                    closeButton: true,
                    duration : Infinity,
                    position : "top-center",
                })
            }).catch(err=>{
                console.error(err)
            })
        }
    }

    return (
        <Dialog open={props.isOpen} onOpenChange={(open)=>{if(!open) handleClose()}}>
            <Form {...form}>
                <form onSubmit={form.handleSubmit(submitModel)}>
                    <DialogContent className="sm:max-w-[425px]">
                        <DialogHeader>
                            <DialogTitle>Edit Model</DialogTitle>
                        </DialogHeader>
                        {
                            ready ?
                                (
                                    <div className={cn("flex flex-col gap-6")}>
                                        <div className="grid gap-2">
                                            <FormField control={form.control}
                                                       name={"publicName"}
                                                       render={({field, fieldState})=>(
                                                           <FormItem>
                                                               <FormLabel><span>PublicName <span className={"text-red-500"}>*</span></span></FormLabel>
                                                               <FormControl>
                                                                   <Input placeholder={"publicName"}
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
                                                       name={"providerId"}
                                                       render={({field})=>(
                                                           <FormItem>
                                                               <FormLabel><span>Provider <span className={"text-red-500"}>*</span></span></FormLabel>
                                                               <FormControl>
                                                                   <Select name={field.name}
                                                                           value={field.value}
                                                                           onValueChange={(value) => {
                                                                                field.onChange(value)
                                                                                form.setValue("originName","")
                                                                            }}
                                                                   >
                                                                       <SelectTrigger className="w-[250px]">
                                                                           <SelectValue placeholder="Select a Provider" />
                                                                       </SelectTrigger>
                                                                       <SelectContent>
                                                                           <SelectGroup>
                                                                               <SelectLabel>Provider</SelectLabel>
                                                                               {
                                                                                   providers?.map(provider=><SelectItem key={provider.id} value={provider.id}>{provider.name}</SelectItem>)
                                                                               }
                                                                           </SelectGroup>
                                                                       </SelectContent>
                                                                   </Select>
                                                               </FormControl>
                                                               <FormMessage />
                                                           </FormItem>
                                                       )}
                                            />
                                        </div>
                                        <div className="grid gap-2">
                                            <FormField control={form.control}
                                                       name={"originName"}
                                                       render={({field})=>(
                                                           <FormItem>
                                                               <FormLabel><span>Origin Model Name <span className={"text-red-500"}>*</span></span></FormLabel>
                                                               <FormControl>
                                                                   {modelSelectorType == "default" ?
                                                                       (
                                                                           <Input placeholder={"Origin Model Name"}
                                                                                  {...field}
                                                                           />
                                                                       )
                                                                       :
                                                                       (<OpenAIModelSelector providerId={watchProvider}
                                                                                             value={field.value}
                                                                                             onValueChange={field.onChange}
                                                                       />)
                                                                   }

                                                               </FormControl>
                                                               <FormMessage />
                                                           </FormItem>
                                                       )}
                                            />
                                        </div>
                                        {
                                            selectedProvider && selectedProvider.provider === ProviderEnum.enum.OPENAI_COMPATIBLE && (
                                                <div className="grid gap-2">
                                                    <FormField control={form.control}
                                                               name={"completionUrl"}
                                                               render={({field})=>(
                                                                   <FormItem>
                                                                       <FormLabel>Completion URL</FormLabel>
                                                                       <FormControl>
                                                                           <Input placeholder={"Completion URL"}
                                                                                  {...field}
                                                                           />
                                                                       </FormControl>
                                                                       <FormMessage />
                                                                   </FormItem>
                                                               )}
                                                    />
                                                </div>
                                            )
                                        }

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
                            <Button type="submit" onClick={form.handleSubmit(submitModel)}>Save</Button>
                        </DialogFooter>
                    </DialogContent>
                </form>
            </Form>
        </Dialog>
    )
}

function OpenAIModelSelector(props : {
    providerId : string,
} & React.ComponentProps<typeof Select>){
    const {providerId} = props

    const [models, setModels] = useState<string[]>([]);
    useEffect(() => {
        if(providerId){
            ProviderAdminAPI.getProviderModels(providerId).then(res=>{
                if(res.data){
                    setModels(res.data)
                }
            }).catch(err=>console.error(err))
        }
    }, [providerId]);

    return (
        <Select
            {...props}
        >
            <SelectTrigger className="w-[250px]">
                <SelectValue placeholder="Select a Model" />
            </SelectTrigger>
            <SelectContent>
                <SelectGroup>
                    <SelectLabel>Origin Model</SelectLabel>
                    {
                        models && models.map(model=><SelectItem key={model} value={model}>{model}</SelectItem>)
                    }
                </SelectGroup>
            </SelectContent>
        </Select>
    )
}
