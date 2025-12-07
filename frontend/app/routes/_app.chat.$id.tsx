import {Textarea} from "~/components/ui/textarea";
import {Button} from "~/components/ui/button";
import {ArrowDown, SendHorizontal} from "lucide-react";
import ChatInputPannel from "~/widget/ChatInputPannel";
import {ScrollArea} from "~/components/ui/scroll-area";
import {useEffect, useRef, useState} from "react";
import {AiMessageBlock, ReasoningBlock, UserMessageBlock} from "~/widget/chat/ChatMessageBlocks";



export default function AgentChat()  {
	const inputPannelRef = useRef<HTMLDivElement>(null);
	const contentAreaRef = useRef<HTMLDivElement>(null);

	useEffect(() => {
		if(inputPannelRef.current){
			const resizeObserver = new ResizeObserver((entries: ResizeObserverEntry[]) => {
				const height = entries[0].contentRect.height;
				if(contentAreaRef.current){
					contentAreaRef.current.style.paddingBottom = `${height}px`;
				}
			});
			resizeObserver.observe(inputPannelRef.current);
		}
	}, [inputPannelRef]);

	return (
		<div className={"flex items-center flex-col gap-1"}>
			<div ref={contentAreaRef} className={"w-3xl"}>
				<div className={"flex flex-col gap-6 justify-around py-6"}>
					<UserMessageBlock message={"javascript에서 div 요소의 높이가 변하는 것을 감지하여 너비를 추적하는 방법이 있을까요?"}/>
					<ReasoningBlock reason={"사용자가 JavaScript에서 div 요소의 높이 변화를 감지하여 너비를 추적하는 방법을 묻고 있습니다.\n" +
						"사용자 선호사항을 확인해보니:\n" +
						"\n" +
						"1. 설명을 간단명료하게 유지\n" +
						"2. Java와 TypeScript를 주로 사용\n" +
						"3. 상세한 답변 전에 명확히 하는 질문을 먼저\n" +
						"\n" +
						"이 질문은 비교적 명확하지만, 몇 가지 확인할 사항이 있을 수 있습니다:\n" +
						"\n" +
						"\"너비를 추적\"이 정확히 무엇을 의미하는지 (높이에 따라 너비를 조정하는 건지, 아니면 단순히 현재 너비를 로깅하는 건지)\n" +
						"React나 다른 프레임워크를 사용하는지, 아니면 순수 JavaScript인지\n" +
						"\n" +
						"하지만 일반적인 경우의 답변을 먼저 제공하고, 필요하면 구체화하는 게 더 나을 것 같습니다.\n" +
						"가장 현대적인 방법은 ResizeObserver API를 사용하는 것입니다."} />

					<AiMessageBlock message={"네, ResizeObserver API를 사용하면 됩니다.\n" +
						"```javascript" +
						"const div = document.querySelector('#myDiv');\n" +
						"\n" +
						"const resizeObserver = new ResizeObserver(entries => {\n" +
						"  for (let entry of entries) {\n" +
						"    const { width, height } = entry.contentRect;\n" +
						"    console.log(`높이: ${height}px, 너비: ${width}px`);\n" +
						"    \n" +
						"    // 여기서 너비 추적/조정 로직 구현\n" +
						"  }\n" +
						"});\n" +
						"\n" +
						"resizeObserver.observe(div);\n" +
						"\n" +
						"// 필요시 해제\n" +
						"// resizeObserver.disconnect();" +
						"```\n" +
						"TypeScript 버전:\n" +
						"```typescript" +
						"const div = document.querySelector<HTMLDivElement>('#myDiv')!;\n" +
						"\n" +
						"const resizeObserver = new ResizeObserver((entries: ResizeObserverEntry[]) => {\n" +
						"  entries.forEach(entry => {\n" +
						"    const { width, height } = entry.contentRect;\n" +
						"    console.log(`높이: ${height}px, 너비: ${width}px`);\n" +
						"  });\n" +
						"});\n" +
						"\n" +
						"resizeObserver.observe(div);\n" +
						"```" +
						"몇 가지 확인하고 싶은 점:\n" +
						"\n" +
						"높이 변화에 따라 너비를 자동으로 조정하고 싶으신 건가요, 아니면 단순히 값을 추적만 하면 되나요?\n" +
						"React, Vue 같은 프레임워크를 사용 중이신가요?" +
						""}/>
					<UserMessageBlock message={"흠.. 같은 부모요소 안에 있는 두 div 요소가 있는데, 한 div 요소는 다른 형제 div 요소의 높이에 따라서 부모의 높이 - 형제의 높이가 되도록 하고 싶습니다. 단, 형제 div요소는 텍스트 입력기가 있기 때문에 입력에 따라서 높이가 변화할 수 있습니다."}/>
				</div>
				<div className={"h-2"}>
				{/*	Bottom Observer */}
				</div>
			</div>
			<div ref={inputPannelRef} className={"w-4xl flex bg-stone-50 rounded-t-xl justify-center fixed bottom-0 pb-4"}>
				<ChatInputPannel className={"w-full"} />
			</div>
		</div>
	);
};

