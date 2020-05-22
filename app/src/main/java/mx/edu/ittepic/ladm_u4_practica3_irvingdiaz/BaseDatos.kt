package mx.edu.ittepic.ladm_u4_practica3_irvingdiaz

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDatos(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE CALIFICACION(NOMBRE VARCHAR(200), NUMEROCONTROL VARCHAR(8), UNIDAD VARCHAR(2), CALIFICACION VARCHAR(3))")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

}