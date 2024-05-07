package Modelo

import java.lang.Exception
import java.sql.Connection
import java.sql.DriverManager

class ClaseConexion {

    fun cadenaConexion():Connection?{

        try {
            val ip = "jdbc:oracle:thin:@10.10.0.54:1521:xe"
            val usuario = "system"
            val contrasena = "desarrollo"

            val connection = DriverManager.getConnection(ip, usuario, contrasena)
            return connection
        }

        catch (e: Exception){
            println("Este es el error: $e")
            return null
        }
    }
}