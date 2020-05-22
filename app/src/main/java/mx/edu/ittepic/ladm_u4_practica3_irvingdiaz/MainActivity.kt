package mx.edu.ittepic.ladm_u4_practica3_irvingdiaz

import android.content.pm.PackageManager
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
        //Variables Listas Data
            var dataLista = ArrayList<String>()
            var listaNoControl = ArrayList<String>()
        //Variables permisos
            val siPermiso = 1
            val siPermisoReceiver = 2
            val siPermisoLectura = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTitle("Calificaciones - Irving Díaz")

        //permisos
        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.RECEIVE_SMS),siPermisoReceiver)
        }

        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.READ_SMS),siPermisoLectura)
        }

        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.SEND_SMS) !=
            PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.SEND_SMS), siPermiso)
        }

        //Actualizar Lista Calificaciones
        leerCalificaciones()

        //Boton agregar Calificación
        btnAgregarCalificacion.setOnClickListener {
            var nombre = ""
            var noControl = ""
            var calificacion = ""
            var unidad = ""

            //Verificación de campos
            if(txtNombre.text.toString().isEmpty()||
                    txtNumeroDeControl.text.toString().isEmpty()||
                        txtCalificacion.text.toString().isEmpty()||
                            txtUnidad.text.toString().isEmpty()){
                mensaje("Llenar todos los campos")
                return@setOnClickListener
            }

            //verificacion tamaño no. control
            if(txtNumeroDeControl.text.toString().length != 8){
                mensaje("El número de control debe ser de 8 números exactamente")
                return@setOnClickListener
            }
            //verificacion tamaño calificación
            if(txtCalificacion.text.toString().toInt()<0 || txtCalificacion.text.toString().toInt()>100){
                mensaje("La calificación debe estar entre 0 y 100")
                return@setOnClickListener
            }
            //verificacion unidad
            if(txtUnidad.text.toString().length != 2){
                mensaje("La unidad lleva la letra U y el número de la unidad, ejemplo: 'U1' ")
                return@setOnClickListener
            }

            nombre = txtNombre.text.toString()
            noControl = txtNumeroDeControl.text.toString()
            calificacion = txtCalificacion.text.toString()
            unidad = txtUnidad.text.toString()

            //Entra si pasa la verificacion
 // "CREATE TABLE CALIFICACION(NOMBRE VARCHAR(200), NUMEROCONTROL VARCHAR(8), UNIDAD VARCHAR(2), CALIFICACION VARCHAR(3))"
            try {
                var baseDatos = BaseDatos(this,"CALIFICACION",null,1)
                var insertar = baseDatos.writableDatabase
                var SQL = "INSERT INTO CALIFICACION VALUES('${nombre}','${noControl}','${unidad}','${calificacion}')"
                insertar.execSQL(SQL)
                baseDatos.close()
            }catch (e: SQLiteException){
                mensaje(e.message!!)
            }
            // Actualizar lista
            leerCalificaciones()
        }

        //Presionar item de la lista
        listaCalificaciones.setOnItemClickListener { parent, view, position, id ->
            if(listaNoControl.size==0){
                return@setOnItemClickListener
            }
            AlertaEliminar(position)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == siPermiso){

        }
        if(requestCode == siPermisoReceiver){

        }
        if(requestCode == siPermisoLectura){

        }
    }

    private fun AlertaEliminar(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("¿Deseas eliminar la calificación con los siguientes datos?")
            .setMessage(dataLista[position])
            .setPositiveButton("Eliminar"){d,i-> EliminarCalificacion(position)}
            .setNeutralButton("Cancelar"){d,i->}
            .show()
    }

    private fun EliminarCalificacion(position: Int) {
        try {
            var base = BaseDatos(this,"CALIFICACION",null,1)
            var eliminar = base.writableDatabase
            var noControlEliminar = arrayOf(listaNoControl[position])
            var respuesta =  eliminar.delete("CALIFICACION","NUMEROCONTROL=?",noControlEliminar)
            if(respuesta.toInt() == 0){
                mensaje("NO SE ELIMINÓ EL CONTACTO")
            }
        }catch (e:SQLiteException){
            mensaje(e.message!!)
        }
        leerCalificaciones()
    }

    private fun leerCalificaciones() {
        dataLista.clear()
        listaNoControl.clear()
 // "CREATE TABLE CALIFICACION(NOMBRE VARCHAR(200), NUMEROCONTROL VARCHAR(8), UNIDAD VARCHAR(2), CALIFICACION VARCHAR(3))"
        try{
            val cursor = BaseDatos(this,"CALIFICACION",null,1)
                .readableDatabase
                .rawQuery("SELECT * FROM CALIFICACION",null)
            var temporal = ""

            if(cursor.moveToFirst()){
                do{
                    temporal ="[No Control: "+cursor.getString(1)+"]"+
                              "[Nombre: "+cursor.getString(0)+"]"+
                              "[Unidad: "+cursor.getString(2)+"]"+
                              "[Calificacion: "+cursor.getString(3)+"]"

                            cursor.getString(1)
                    dataLista.add(temporal)
                    listaNoControl.add(cursor.getString(1))
                }while(cursor.moveToNext())
            }else{
                dataLista.add("Aún no has ingresado calificaciones")
            }
            var adaptador = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataLista)
            listaCalificaciones.adapter = adaptador
        }catch (err: SQLiteException){
            Toast.makeText(this,err.message, Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun mensaje(s: String) {
        Toast.makeText(this,s,Toast.LENGTH_LONG)
            .show()
    }




}
