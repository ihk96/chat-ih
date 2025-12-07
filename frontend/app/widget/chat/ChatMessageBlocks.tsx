import {Accordion, AccordionContent, AccordionItem, AccordionTrigger} from "~/components/ui/accordion";
import {Spinner} from "~/components/ui/spinner";

export function UserMessageBlock(props:{
    message : string
}){
    const {message} = props;

    return (
        <div className={"p-3 bg-stone-500 rounded-md text-white w-fit"}>
            {message.trim().split("\n").map((line, index) => <p key={index}>{line != "\n" ? line : <br />}</p>)}
        </div>
    )
}

export function AiMessageBlock(props:{
    message : string
}){
    const {message} = props;

    return (
        <div className={"px-1"}>
            {message.trim().split("\n").map((line, index) => <p key={index}>{line != "\n" ? line : <br />}</p>)}
        </div>
    )
}

export function ReasoningBlock(props:{
    reason : string,
    isProgressing? : boolean
}){
    const {reason, isProgressing = false} = props;

    return (
        <Accordion
            type="single"
            collapsible
            className="w-full border border-neutral-400 rounded-md px-3"
            defaultValue={isProgressing ? "item-1" : ""}
        >
            <AccordionItem value="item-1">
                <AccordionTrigger className={"cursor-pointer"}><span>{isProgressing ? <>생각 중 <Spinner className={"inline ml-1"} /></> : "사고 과정"}</span></AccordionTrigger>
                <AccordionContent className="flex flex-col text-balance">
                    <p className={"px-2 pb-2"}>
                        {
                            reason.trim().split("\n").map((line, index) => <p key={index}>{line != "\n" ? line : <br />}</p>)
                        }
                    </p>
                </AccordionContent>
            </AccordionItem>
        </Accordion>
    )
}

export function FunctionCallBlock(props : {
    message : string,
    isProgressing : boolean
}){
    const {message, isProgressing = false} = props;

    return(
        <Accordion
            type="single"
            collapsible
            className="w-full border border-neutral-400 rounded-md px-3"
            defaultValue={isProgressing ? "item-1" : ""}
        >
            <AccordionItem value="item-1">
                <AccordionTrigger className={"cursor-pointer"}>도구 사용</AccordionTrigger>
                <AccordionContent className="flex flex-col gap-4 text-balance">
                    <p>
                        {
                            message.trim().split("\n").map((line, index) => <p key={index}>{line != "\n" ? line : <br />}</p>)
                        }
                    </p>
                </AccordionContent>
            </AccordionItem>
        </Accordion>
    )
}