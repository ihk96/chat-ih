import {Input} from "~/components/ui/input";
import {z} from "zod/v4";

export default function AdminGeneral() {

    return (
        <div className={"p-4"}>
            <div>
                <span className={"text-sm"}>데이터베이스</span>
            </div>

        </div>
    )
}


const DatabaseSettingSchema = z.object({
    host : z.string(),
    password : z.string().min(6),
})
function DatabaseSettings(){


    <div>
        <span className={"text-sm"}>데이터베이스</span>
        <Input />
    </div>
}