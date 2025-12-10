Para hacer la tarea de sqlite, debemos crear una clase MainActivity y una clase que indique explícitamente el diseño del esquema de forma sistemática y autodocumentada.


En la clase FeedReaderContract, SQLite en mi caso, creamos un objeto donde definimos el nombre de la tabla y los nombres de las columnas.

    object FeedEntry : BaseColumns {
        const val TABLE_NAME = "entry"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_SUBTITLE = "subtitle"
    }

Después de definir tu tabla, ya puedes implementar métodos básicos para crear y borrar la tabla.

    private const val SQL_CREATE_ENTRIES =
        "CREATE TABLE ${FeedEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${FeedEntry.COLUMN_NAME_TITLE} TEXT," +
                "${FeedEntry.COLUMN_NAME_SUBTITLE} TEXT)"


    private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${FeedEntry.TABLE_NAME}"


Para administrar la base de datos, podemos usar la clase SQLiteOpenHelper, para implementar métodos como el onCreate(), onUpgrade() y los métodos onDowngrade() y onOpen(), aunque estos dos no son oblgatorios.

    class FeedReaderDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        override fun onCreate(db: SQLiteDatabase) {
            Log.d(TAG,"Creando db")
            db.execSQL(SQL_CREATE_ENTRIES)
            Log.d(TAG,"Creada db")
        }
        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            Log.d(TAG,"Actualizando db")
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES)
            onCreate(db)
        }
        override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            Log.d(TAG,"desactualizando db")
            onUpgrade(db, oldVersion, newVersion)
        }
        companion object {
            // If you change the database schema, you must increment the database version.
            const val DATABASE_VERSION = 1
            const val DATABASE_NAME = "FeedReader.db"
        }
    }

Para poder acceder a la base de datos desde el MainActivity, debemos instanciar la subclase de SQLiteOpenHelper

        val dbHelper = SQLite.FeedReaderDbHelper(application)

Para insertar los datos en la tabla, le pasamos un objeto ContentValues al método insert()

        val db = dbHelper.writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(SQLite.FeedEntry.COLUMN_NAME_TITLE, "titulo")
            put(SQLite.FeedEntry.COLUMN_NAME_SUBTITLE, "prueba")
        }
        Log.d(TAG,"Columnas creadas")

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db?.insert(SQLite.FeedEntry.TABLE_NAME, null, values)
        Log.d(TAG,"Datos insertados")

Para leer la tabla, usamos el método query(), que combina el método insert() y el update(), devolviendo un objeto cursor.

        val dbl = dbHelper.readableDatabase
        Log.d(TAG,"Conectando base lectura")


// Define a projection that specifies which columns from the database
// you will actually use after this query.
val projection = arrayOf(BaseColumns._ID, SQLite.FeedEntry.COLUMN_NAME_TITLE, SQLite.FeedEntry.COLUMN_NAME_SUBTITLE)

// Filter results WHERE "title" = 'My Title'
Log.d(TAG,"Filtrando select")

        val selection = "${SQLite.FeedEntry.COLUMN_NAME_TITLE} = ?"
        val selectionArgs = arrayOf("My Title")

// How you want the results sorted in the resulting Cursor
Log.d(TAG,"Ordenando select")

        val sortOrder = "${SQLite.FeedEntry.COLUMN_NAME_SUBTITLE} DESC"

        val cursor = dbl.query(
            SQLite.FeedEntry.TABLE_NAME,   // The table to query
            projection,             // The array of columns to return (pass null to get all)
            selection,              // The columns for the WHERE clause
            selectionArgs,          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            sortOrder               // The sort order
        )
        Log.d(TAG,"valores recibidos")
        val itemIds = mutableListOf<String>()

        with(cursor) {
            while (moveToNext()) {
                val itemId = getString(getColumnIndexOrThrow(SQLite.FeedEntry.COLUMN_NAME_TITLE))
                Log.d(TAG,"Valor = $itemId")

                itemIds.add(itemId)
            }
        }
        Log.d(TAG,"Valores = $itemIds")

        cursor.close()

Para borrar los datos de la tabla, usamos el método delte()

        // Define 'where' part of query.
        val selection2 = "${SQLite.FeedEntry.COLUMN_NAME_TITLE} LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs2 = arrayOf("MyTitle")
        // Issue SQL statement.
        val deletedRows = db.delete(SQLite.FeedEntry.TABLE_NAME, selection2, selectionArgs2)
        Log.d(TAG,"Valores borrados")

Por último para actualizar los datos, usamos el método update()

        val db3 = dbHelper.writableDatabase

    // New value for one column
        val title = "MyNewTitle"
        val values2 = ContentValues().apply {
            put(SQLite.FeedEntry.COLUMN_NAME_TITLE, title)
        }

    // Which row to update, based on the title
        val selection3 = "${SQLite.FeedEntry.COLUMN_NAME_TITLE} LIKE ?"
        val selectionArgs3 = arrayOf("MyOldTitle")
        val count = db3.update(
            SQLite.FeedEntry.TABLE_NAME,
            values2,
            selection3,
            selectionArgs3)

Cerramos la base de datos con el método onDestroy()

    override fun onDestroy() {

        super.onDestroy()
    }