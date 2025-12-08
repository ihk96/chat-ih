@Entity
@Table(name = "ll_model")
class LLModel(

    @Id
    val id : String,
    val publicName : String,
    val originName : String,
    
    val connectionId: String, // Links to ModelProviderConnection
    
    val completionUrl : String,

    )