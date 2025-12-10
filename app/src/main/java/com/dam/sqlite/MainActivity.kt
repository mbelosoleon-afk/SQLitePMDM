package com.dam.sqlite

import android.R.attr.subtitle
import android.content.ContentValues
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dam.sqlite.ui.theme.SQLiteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SQLiteTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        val dbHelper = SQLite.FeedReaderDbHelper(application)
        val TAG = "SQLitePruebas"

        // Gets the data repository in write mode
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


        // Define 'where' part of query.
        val selection2 = "${SQLite.FeedEntry.COLUMN_NAME_TITLE} LIKE ?"
// Specify arguments in placeholder order.
        val selectionArgs2 = arrayOf("MyTitle")
// Issue SQL statement.
        val deletedRows = db.delete(SQLite.FeedEntry.TABLE_NAME, selection2, selectionArgs2)
        Log.d(TAG,"Valores borrados")

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


        dbHelper.close()
    }
    override fun onDestroy() {

        super.onDestroy()
    }
}




@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SQLiteTheme {
        Greeting("Android")
    }
}