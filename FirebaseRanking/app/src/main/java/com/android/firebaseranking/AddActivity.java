package com.android.firebaseranking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.android.firebaseranking.Activitys.DataBase;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddActivity extends AppCompatActivity {
    private static final String TAG = "AddActivity";
    private FirebaseFirestore db;
    private Spinner personajesSpinner;
    private EditText descripcionEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        db = FirebaseFirestore.getInstance();
        personajesSpinner = findViewById(R.id.personajesSpinner);
        descripcionEditText = findViewById(R.id.descripcionEditText);

        // Obtener el usuario autenticado actualmente
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();
        List<String> lista = (List<String>) intent.getSerializableExtra("lista");
        Log.d(TAG, "Post guardado correctamente"+lista);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lista);
        // Establece el layout del Spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Asigna el ArrayAdapter al Spinner
        personajesSpinner.setAdapter(adapter);
        // Configurar el botón de guardar
        Button guardarButton = findViewById(R.id.guardarButton);
        guardarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    String selectedPersonaje = personajesSpinner.getSelectedItem().toString();
                    String descripcion = descripcionEditText.getText().toString();
                    Date fechaActual = new Date();
                    // Realizar el post en Firestore
                    db.collection("users").document(userId).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    String userName = documentSnapshot.getString("username");

                                    // Crear un mapa con los datos a guardar
                                    Map<String, Object> postData = new HashMap<>();
                                    postData.put("From", userName);
                                    postData.put("To", selectedPersonaje);
                                    postData.put("Content", descripcion);
                                    postData.put("Date", fechaActual);

                                    // Realizar el post en Firestore
                                    DataBase.AddMapToArrayIntent("rankings","Doc","Messages",AddActivity.this,ActivityMain.class, postData);
                                } else {
                                    Log.e(TAG, "El documento de usuario no existe");
                                    // Manejar el caso en el que el documento de usuario no exista
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error al obtener el documento de usuario", e);
                                // Manejar el error de forma adecuada
                            });
                } else {
                    // El usuario no está autenticado, tomar alguna acción si es necesario
                }
            }
        });
    }
}


