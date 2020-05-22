package mx.edu.ittepic.ladm_u4_practica3_irvingdiaz

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Build
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class SmsReceiver : BroadcastReceiver(){
    var baseRemota = FirebaseFirestore.getInstance()


    override fun onReceive(context: Context, intent: Intent) {


        val extras = intent.extras
        val contexto = context
        if(extras != null){
            var sms = extras.get("pdus") as Array<Any>
            for(indice in sms.indices){
                var formato = extras.getString("format")

                var smsMensaje = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    SmsMessage.createFromPdu(sms[indice] as ByteArray,formato)
                }else{
                    SmsMessage.createFromPdu(sms[indice] as ByteArray)
                }
                //CALIFICACION 184009649 U1

                var celularOrigen = smsMensaje.originatingAddress
                var contenidoSMS = smsMensaje.messageBody.toString()
                var cadena = contenidoSMS.split(" ")
                var mensajeEnvio = ""
                Toast.makeText(context,"Recibiste mensaje de: "+celularOrigen,Toast.LENGTH_LONG)
                    .show()
                //VALIDAR SMS
                if(cadena.size !=3){
                    //ENVIAR MENSAJE DE SINTAXIS INCORRECTA

                }else{
                    if(!(cadena[0].equals("CALIFICACION"))){
                        //ENVIAR MENSAJE SINTAXIS INCORRECTA - NO SE ESCRIBIO BIEN CALIFICACION
                        SmsManager.getDefault().sendTextMessage(
                            celularOrigen,null,
                            "Sintaxis incorrecta: Escribe CALIFICACION (Sin acento!) [No. Control] [Unidad] separado por espacios Ejemplo: 'CALIFICACION 16400900 U1'",null,null)
                    }else{
                        if(cadena[1].toString().length != 8){
                            //ENVIAR MENSAJE SINTAXIS INCORRECTA - NUMERO DE CONTROL MAL PERO CALIFICACION BIEN
                            SmsManager.getDefault().sendTextMessage(
                                celularOrigen,null,
                                "Sintaxis incorrecta: El numero de control tiene 8 digitos Ejemplo: 'CALIFICACION 16400900 U1'",null,null)

                        }else{
                            if(cadena[2].toString().length != 2){
                                //ENVIAR MENSAJE -- NO ESCRIBIO BIEN LA UNIDAD
                                SmsManager.getDefault().sendTextMessage(
                                    celularOrigen,null,
                                    "Sintaxis incorrecta: La unidad se escribe con una U seguida un solo numero de unidad Ejemplo: 'CALIFICACION 16400900 U1'",null,null)

                            }else{
                                //
                                try {

                                    val cursor = BaseDatos(context,"CALIFICACION",null,1)
                                        .readableDatabase
                                        .rawQuery("SELECT * FROM CALIFICACION WHERE NUMEROCONTROL = '${cadena[1]}' AND UNIDAD = '${cadena[2]}'",null)
                                    if(cursor.moveToNext()){

                                            mensajeEnvio = "Hola! "+cursor.getString(0)+", tu calif. unidad "+cursor.getString(2)+
                                                    " es: "+cursor.getString(3)

                                        SmsManager.getDefault().sendTextMessage(
                                            celularOrigen,null,
                                            ""+mensajeEnvio,null,null)

                                    }else{
                                        SmsManager.getDefault().sendTextMessage(
                                            celularOrigen,null,
                                            "No se encontraron calificaciones con los siguientes datos: No. Control: ${cadena[1]}, Unidad: ${cadena[2]}",null,null)

                                    }
                                }catch (e: SQLiteException){
                                    SmsManager.getDefault().sendTextMessage(
                                        celularOrigen,null,
                                        e.message,null,null)

                                }

                                //


                            }
                        }
                    }
                }
            }
        }
    }

}