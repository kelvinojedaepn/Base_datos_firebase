package com.example.crudfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EstudianteListActivity : AppCompatActivity() {

    private val contenidoIntentExplicito = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            if (result.data != null) {
                val data = result.data
                Log.i("Intente-epn", "${data?.getStringExtra("nombreModificado")}")
            }
        }
    }

    private lateinit var carRecyclerView: RecyclerView
    private lateinit var estudianteArrayList: ArrayList<Estudiante>
    private lateinit var adapterCar: AdapterEstudiante

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estudiante_list)

        var id = intent.getStringExtra("colegioId")

        carRecyclerView = findViewById(R.id.carListRecyclerView)
        carRecyclerView.layoutManager = LinearLayoutManager(this)
        carRecyclerView.setHasFixedSize(true)
        estudianteArrayList = arrayListOf<Estudiante>()
        if (id != null) {
            consultarDocumentosColegio(id!!)
        }
        adapterCar = AdapterEstudiante(estudianteArrayList, contenidoIntentExplicito, id!!)
        carRecyclerView.adapter = adapterCar

        val btnNuevoEstudiante = findViewById<Button>(R.id.btn_nuevo_estudiante)
        btnNuevoEstudiante.setOnClickListener {
            abrirActividadConParametros(EditEstudianteInfoActivity::class.java, it, id!!)
        }
    }

    private fun consultarDocumentosColegio(id: String) {

        val db = Firebase.firestore
        val colegioRef = db.collection("colegio").document(id)
        val estudianteCollectionRef = colegioRef.collection("estudiante")
        limpiarArreglo()
        estudianteCollectionRef.get().addOnSuccessListener { querySnaps ->
            for (document in querySnaps.documents) {
                val estudiante = Estudiante(
                    document.get("id") as String?,
                    document.get("nombre") as String?,
                    (document.get("estatura") as Number?)?.toDouble(),
                    document.get("fechaNacimiento") as String?,
                    document.get("bloque") as String?
                )
                    this.estudianteArrayList.add(estudiante)

            }
            adapterCar.estudianteList = estudianteArrayList
            adapterCar.notifyDataSetChanged()
        }

    }

    private fun limpiarArreglo() {
        this.estudianteArrayList.clear()
    }

    private fun abrirActividadConParametros(clase: Class<*>, it: View?, idColegio: String) {
        val intent = Intent(it!!.context, clase)
        intent.putExtra("colegioId", idColegio)
        contenidoIntentExplicito.launch(intent)
    }
}