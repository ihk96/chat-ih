import {Accordion, AccordionContent, AccordionItem, AccordionTrigger} from "~/components/ui/accordion";
import {Spinner} from "~/components/ui/spinner";
import type {ChatMessage} from "~/features/chat/types";
import {ScrollArea} from "~/components/ui/scroll-area";
import type {progressingType} from "~/features/chat/hooks";
import Markdown from "react-markdown";
import remarkGfm from "remark-gfm";
import { Prism as SyntaxHighlighter } from "react-syntax-highlighter";
import {coy} from "react-syntax-highlighter/dist/cjs/styles/prism";
import {useEffect} from "react";

export function UserMessageBlock(props:{
    message : ChatMessage
}){
    const {message} = props;

    return (
        <div className={"flex justify-end"}>
            <div className={"flex flex-col items-end"}>
                <div className={"p-3 bg-stone-500 rounded-md text-white w-fit"}>
                    <UserMessageMarkdownRender>{message.text}</UserMessageMarkdownRender>

                </div>
                {
                    message.attachments && message.attachments.length > 0 && (
                        <div className={"flex gap-2 mt-4"}>
                            {
                                message.attachments.map((attachment, index) => (
                                    <div key={index} className={"border border-stone-300 px-4 py-2 rounded-md flex flex-col gap-2"}>
                                        {attachment.fileName}
                                    </div>
                                ))
                            }
                        </div>
                    )
                }
            </div>
        </div>
    )
}

export function AiMessageBlock(props:{
    message : ChatMessage,
    processingType? : progressingType
}){
    const {message, processingType} = props;
    useEffect(() => {
        // console.log(message)
    }, [message]);

    return (
        <div className={"px-1 flex flex-col gap-4"}>
            {message.thinking && <AiThinkingBlock thinking={message.thinking} isProgressing={processingType === "thinking"} />}
            {message.toolRequests && message.toolRequests.length > 0 && <AiFunctionCallBlock functionNames={message.toolRequests} isProgressing={processingType === "function_call"} />}
            {message.text && <AiTokensBlock token={message.text} />}
        </div>
    )
}

function AiTokensBlock(props:{
    token : string
}){
    const {token} = props;
    return (
        <div className={"px-1"}>
            <AiMessageMarkdownRender>{token}</AiMessageMarkdownRender>
        </div>
    )
}

function AiThinkingBlock(props:{
    thinking : string,
    isProgressing? : boolean
}){
    const {thinking, isProgressing = false} = props;

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
                    <ScrollArea className={"h-52"}>
                        <div className={"px-2 pb-2 [&>p]:py-0.5"}>
                            {
                                thinking.trim().split("\n").map((line, index) => <p key={index}>{line != "\n" ? line : <br />}</p>)
                            }
                        </div>
                    </ScrollArea>
                </AccordionContent>
            </AccordionItem>
        </Accordion>
    )
}

function AiFunctionCallBlock(props : {
    functionNames : string[],
    isProgressing : boolean
}){
    const {functionNames, isProgressing = false} = props;

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
                            functionNames.map((line, index) => <p key={index}>{line != "\n" ? line : <br />}</p>)
                        }
                    </p>
                </AccordionContent>
            </AccordionItem>
        </Accordion>
    )
}

function UserMessageMarkdownRender({children} : {children : string}){
    return (
        <Markdown remarkPlugins={[remarkGfm]}
                  components={{
                      p : (props:any)=>{
                          return (
                              <p className={""} {...props}>{props.children}</p>
                          )
                      },
                      ul : (props:any)=>{
                          return (
                              <ul className={"[&>li]:list-disc"} {...props}>{props.children}</ul>
                          )
                      },
                      ol : (props:any)=>{
                          return (
                              <ol className={"[&>li]:list-decimal"} {...props}>{props.children}</ol>
                          )
                      },
                      li : (props:any)=>{
                          return (
                              <li className={"ml-10"} {...props}>{props.children}</li>
                          )
                      },
                      code : (props: any) => {
                          const match = /language-(\w+)/.exec(props.className || "");
                          return !props.inline && match ? (
                              <div className={"bg-stone-100 rounded-md border border-primary/30 mb-4"}>
                                  <div className={"px-4 pt-2"}>
                                      <span className={"text-xs font-mono"}>{match[1]}</span>
                                  </div>
                                  <SyntaxHighlighter
                                      children={String(props.children).replace(/\n$/, "")}
                                      style={{...coy, background:"black"}}
                                      customStyle={{
                                          backgroundColor : "none",
                                          paddingBottom : "1rem"
                                      }}
                                      language={match[1]}
                                      {...props}
                                  />
                              </div>
                          ) : (
                              <code {...props}>
                                  {props.children}
                              </code>
                          );
                      }
                  }}

        >{children}</Markdown>
    )
}

function AiMessageMarkdownRender({children} : {children : string}){

    return (
        <Markdown remarkPlugins={[remarkGfm]}
                  components={{
                      p : (props:any)=>{
                          return (
                              <p className={"pb-4"} {...props}>{props.children}</p>
                          )
                      },
                      ul : (props:any)=>{
                          return (
                              <ul className={"pb-4 [&>li]:list-disc"} {...props}>{props.children}</ul>
                          )
                      },
                      ol : (props:any)=>{
                          return (
                              <ol className={"pb-4 [&>li]:list-decimal"} {...props}>{props.children}</ol>
                          )
                      },
                      li : (props:any)=>{
                          return (
                              <li className={"pb-2 ml-10"} {...props}>{props.children}</li>
                          )
                      },
                      table : (props:any)=>{
                          return (
                              <table className={"mb-4"} {...props}>{props.children}</table>
                          )
                      },
                      hr : (props:any)=>{
                          return (
                              <hr className={"pb-4"} {...props}>{props.children}</hr>
                          )
                      },
                      h1 : (props:any)=>{
                          return (
                              <h1 className={"pb-4 font-bold text-4xl"} {...props}>{props.children}</h1>
                          )
                      },
                      h2 : (props:any)=>{
                          return (
                              <h2 className={"pb-4 font-bold text-3xl"} {...props}>{props.children}</h2>
                          )
                      },
                      h3 : (props:any)=>{
                          return (
                              <h3 className={"pb-4 font-bold text-2xl"} {...props}>{props.children}</h3>
                          )
                      },
                      h4 : (props:any)=>{
                          return (
                              <h4 className={"pb-4 font-bold text-xl"} {...props}>{props.children}</h4>
                          )
                      },
                      h5 : (props:any)=>{
                          return (
                              <h5 className={"pb-4 font-bold text-lg"} {...props}>{props.children}</h5>
                          )
                      },
                      h6 : (props:any)=>{
                          return (
                              <h6 className={"pb-4 font-bold"} {...props}>{props.children}</h6>
                          )
                      },
                      code : (props: any) => {
                          const match = /language-(\w+)/.exec(props.className || "");
                          return !props.inline && match ? (
                              <div className={"bg-stone-100 rounded-md border border-primary/30 mb-4"}>
                                  <div className={"px-4 pt-2"}>
                                      <span className={"text-xs font-mono"}>{match[1]}</span>
                                  </div>
                                  <SyntaxHighlighter
                                      children={String(props.children).replace(/\n$/, "")}
                                      style={{...coy, background:"black"}}
                                      customStyle={{
                                          backgroundColor : "none",
                                          paddingBottom : "1rem"
                                      }}
                                      language={match[1]}
                                      {...props}
                                  />
                              </div>
                          ) : (
                              <code {...props}>
                                  {props.children}
                              </code>
                          );
                      }
                  }}
        >
            {children}
        </Markdown>
    )
}