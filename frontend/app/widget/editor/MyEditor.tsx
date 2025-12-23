import {EditorView} from "prosemirror-view";
import {forwardRef, useEffect, useImperativeHandle, useRef, useState} from "react";
import {EditorState, TextSelection} from "prosemirror-state";
import {Node as StateNode} from "prosemirror-model";

import "./editor.css";
import View, {markdownParser} from "~/widget/editor/View";
import {defaultMarkdownParser, defaultMarkdownSerializer} from "prosemirror-markdown";
import view from "~/widget/editor/View";

type EditorProps = React.HTMLAttributes<HTMLDivElement> & {
	onEdit? : (doc:string) => void,
	defaultDoc?: string,
	isEditable?: boolean
}

export type MyEditorRef = {
	reset : () => void
}

const MyEditor = forwardRef((props:EditorProps,ref)=> {
	const viewRef = useRef<EditorView>(null);
	const editorWrapperRef = useRef<HTMLDivElement>(null)
	const [state, setState] = useState<EditorState>();
	const {defaultDoc = "",isEditable = true} = props;

	useImperativeHandle(ref, ()=>({
		reset(){
			if(viewRef.current){
				initializeEditor()
			}
		}
	}));

	useEffect(() => {
		if(state){
			if(props.onEdit){
				props.onEdit(defaultMarkdownSerializer.serialize(state!.doc))
			}
		}
	}, [state]);

	useEffect(() => {
		if(viewRef.current){
			if(isEditable !== viewRef.current.editable){
				viewRef.current?.setProps({editable : ()=>isEditable})
			}
		}
	}, [props.isEditable]);

	function initializeEditor(){
		if(editorWrapperRef.current && !viewRef.current){
			viewRef.current = View.initializeEditor({
				element: editorWrapperRef.current,
				setState,
				defaultDoc : markdownParser.parse(defaultDoc),
				editable : isEditable
			});
			viewRef.current!.dom.addEventListener("click", (e) => {
				e.stopPropagation();
			});
		}
	}

	useEffect(() => {

		initializeEditor()

		return () => {
			if(viewRef.current){
				viewRef.current.destroy();
				viewRef.current = null;
			}
		}
	},[])

	function focusEditor(){
		if(viewRef.current){
			const endSelection = TextSelection.atEnd(state!.doc);

			const tr = state!.tr.setSelection(endSelection).scrollIntoView();
			viewRef.current.dispatch(tr);
			viewRef.current.focus();
		}
	}

	return (
		<div onClick={focusEditor} ref={editorWrapperRef} {...props}>

		</div>
	)
});

export default MyEditor