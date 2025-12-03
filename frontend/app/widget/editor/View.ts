import {EditorState} from "prosemirror-state";
import {schema as basicSchema} from "prosemirror-schema-basic";
import {history, redo, undo} from "prosemirror-history";
import {keymap} from "prosemirror-keymap";
import {
	baseKeymap,
	chainCommands,
	deleteSelection,
	joinBackward, joinTextblockBackward,
	selectNodeBackward,
	splitBlock
} from "prosemirror-commands";
import {EditorView} from "prosemirror-view";
import {Node, Schema} from "prosemirror-model";
import {liftListItem, sinkListItem, splitListItem} from "prosemirror-schema-list";
import {inputRules, wrappingInputRule} from "prosemirror-inputrules";
import {placeholder} from "prosemirror-placeholder";
import {Node as StateNode} from "prosemirror-model";
import {defaultMarkdownParser, MarkdownParser} from "prosemirror-markdown";

export default {
	initializeEditor
}

const schema = new Schema({
	nodes: basicSchema.spec.nodes
		.addToEnd("bullet_list",{
			content : "list_item+",
			group : "block",
			parseDOM: [{tag: "ul"}],
			toDOM() {
				return ["ul",{class:"dul"}, 0]
			}
		})
		.addToEnd("ordered_list",{
			content : "list_item+",
			group : "block",
			parseDOM: [{tag: "ol"}],
			toDOM() {
				return ["ol",{class:"dol"}, 0]
			}
		})
		.addToStart("list_item",{
			content : "paragraph block*",
			parseDOM: [{tag: "li"}],
			toDOM() {
				return ["li", 0]
			}
		}),
	// marks: basicSchema.spec.marks.addToEnd("underline", {
	// 	parseDOM: [{tag: "u"}, {style: "text-decoration=underline"}],
	// 	toDOM() { return ["u", 0] }
	// })
});

export const markdownParser = new MarkdownParser(schema, defaultMarkdownParser.tokenizer, defaultMarkdownParser.tokens);

const inputRule = [
	wrappingInputRule(/^\s*([-+*])\s$/,schema.nodes.bullet_list),
	wrappingInputRule(/^\s*(\d+)\.\s$/,schema.nodes.ordered_list),
]

function initializeEditor({
	element
	,setState
	,defaultDoc = Node.fromJSON(schema,JSON.parse(""))
	,editable = true
}:{
	element: HTMLElement,
	setState : (state : EditorState) => void
	defaultDoc? : StateNode
	editable? : boolean
}){
	const state = EditorState.create({
		doc : defaultDoc //defaultMarkdownParser.parse(content)
		,schema
		,plugins: [
			placeholder("여기에 입력하세요..."),
			history(),
			keymap({
				"Enter": () => true,
				"Shift-Enter": chainCommands(
					splitListItem(schema.nodes.list_item),
					liftListItem(schema.nodes.list_item),  // 비어있으면 list 벗어나기
					splitBlock
				),
				// "Enter": splitListItem(schema.nodes.list_item),
				"Tab": sinkListItem(schema.nodes.list_item),
				"Shift-Tab": liftListItem(schema.nodes.list_item),
				"Backspace" : chainCommands(deleteSelection,joinTextblockBackward, selectNodeBackward),
			}),
			keymap({"Mod-z":undo, "Mod-y":redo}),
			keymap(baseKeymap),
			inputRules({rules : inputRule}),
		]
	});
	const view = new EditorView(element, {
		state
		,editable: ()=> editable
		,dispatchTransaction(transaction){
			const newState = view.state.apply(transaction);
			view.updateState(newState);
			setState(newState);
		},
	});

	setState(state);

	return view
}
