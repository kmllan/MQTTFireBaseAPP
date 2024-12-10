package com.kevin.mqttfirebaseapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
//librerias de widgets
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
//libreria de firebase
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityFormulario extends AppCompatActivity {

    private EditText txtCodigoJugador, txtNombreJugador, txtNumeroCamiseta;
    private Spinner spPosicion;
    private ListView lista;
    private FirebaseFirestore db;
    private String[] posiciones = {"Portero", "Defensa", "Mediocampista", "Delantero"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_formulario);

        CargarListaFirestore();

        db = FirebaseFirestore.getInstance();


        txtCodigoJugador = findViewById(R.id.txtCodigoJugador);
        txtNombreJugador = findViewById(R.id.txtNombreJugador);
        txtNumeroCamiseta = findViewById(R.id.txtNumeroCamiseta);
        spPosicion = findViewById(R.id.spPosicion);
        lista = findViewById(R.id.Lista);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, posiciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPosicion.setAdapter(adapter);
    }

    //metodo enviar datos
    public void enviarDatosFirestore(View view) {
        String codigoJugador = txtCodigoJugador.getText().toString().trim();
        String nombre = txtNombreJugador.getText().toString().trim();
        String numeroCamiseta = txtNumeroCamiseta.getText().toString().trim();
        String posicion = spPosicion.getSelectedItem().toString();

        if (codigoJugador.isEmpty() || nombre.isEmpty() || numeroCamiseta.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> jugador = new HashMap<>();
        jugador.put("codigo", codigoJugador);
        jugador.put("nombre", nombre);
        jugador.put("posicion", posicion);
        jugador.put("numeroCamiseta", numeroCamiseta);

        db.collection("jugadores")
                .document(codigoJugador) // Usamos el código como documento único
                .set(jugador)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Jugador guardado correctamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    //boton para cargar lista
    public void CargarLista(View view){
        CargarListaFirestore();
    }

    //cargar lista
    public void CargarListaFirestore() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("jugadores")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            List<String> listaJugadores = new ArrayList<>();


                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String linea = "Código: " + document.getString("codigo") + " - " +
                                        "Nombre: " + document.getString("nombre") + " - " +
                                        "Posición: " + document.getString("posicion") + " - " +
                                        "Camiseta: " + document.getString("numeroCamiseta");

                                listaJugadores.add(linea);
                            }
                            ArrayAdapter<String> adaptador = new ArrayAdapter<>(ActivityFormulario.this,
                                    android.R.layout.simple_list_item_1, listaJugadores);
                            lista.setAdapter(adaptador);
                        } else {
                            // por si ocurre un error al obtener los datos
                            Log.e("Tag", "Error al obtener datos de Firestore", task.getException());
                        }
                    }
                });
    }
}