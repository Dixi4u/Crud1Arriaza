package diego.arriaza.myapplication

import Modelo.ClaseConexion
import Modelo.dataClassMascotas
import RecyclerViewHelper.Adaptador
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //1- Mandar a llamar a todos los elementos
        val txtNombreM = findViewById<EditText>(R.id.txtNombreM)
        val txtPeso = findViewById<EditText>(R.id.txtPeso)
        val txtEdad = findViewById<EditText>(R.id.txtEdad)
        val btnAgregar = findViewById<Button>(R.id.btnAgregar)
        val rcvMascotas = findViewById<RecyclerView>(R.id.rcvMascotas)

        //Primer paso para mostrar datos
        //Asignarle layout al RecyclerView
        rcvMascotas.layoutManager = LinearLayoutManager(this)

        //////////////////TODO: Mostrar Datos///////////////////////
        //Funcion para mostrar datos
        fun obtenerDatos():List<dataClassMascotas>{
            //1- Creoun objeto de la clase conexion
            val objConexion = ClaseConexion().cadenaConexion()

            //2- Creo un statement
            val statement = objConexion?.createStatement()
            val resulSet = statement?.executeQuery("select * from tbmascotas")!!
            val mascotas = mutableListOf<dataClassMascotas>()

            //Recorro todos los registros de la base de datos
            while (resulSet.next()){
                val nombre = resulSet.getString("nombre")
                val mascota = dataClassMascotas(nombre)
                mascotas.add(mascota)
            }
            return mascotas
        }

        //Asignar el adaptador al RecyclerView
        CoroutineScope(Dispatchers.IO).launch {
            val mascotasDB = obtenerDatos()
            withContext(Dispatchers.Main){
                val adapter = Adaptador(mascotasDB)
                rcvMascotas.adapter = adapter
            }
        }

        //2- Programar el boton para agregar
        btnAgregar.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                //1- Creo un objeto de la calse conexion
                val objConexion = ClaseConexion().cadenaConexion()

                //2- Creo una variable que contenga un PrepareStatement
                val addMascota = objConexion?.prepareStatement("insert into tbMascotas values(?, ?, ?)")!!
                addMascota.setString(1, txtNombreM.text.toString())
                addMascota.setInt(2, txtPeso.text.toString().toInt())
                addMascota.setInt(3, txtEdad.text.toString().toInt())
                addMascota.executeUpdate()

                //Toast.makeText(this@MainActivity, "Mascota registrada", Toast.LENGTH_LONG).show()

                //Refresco la lista
                val nuevasMascotas = obtenerDatos()
                withContext(Dispatchers.Main){
                    (rcvMascotas.adapter as? Adaptador)?.AnctualizarLista(nuevasMascotas)
                }
            }
        }

    }
}