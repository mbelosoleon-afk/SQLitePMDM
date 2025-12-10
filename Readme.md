Para hacer la tarea de sqlite, debemos crear una clase MainActivity y una clase que indique explícitamente el diseño del esquema de forma sistemática y autodocumentada.


En la clase FeedReaderContract, SQLite en mi caso, creamos un objeto donde definimos el nombre de la tabla y los nombres de las columnas.

    object FeedEntry : BaseColumns {
        const val TABLE_NAME = "entry"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_SUBTITLE = "subtitle"
    }