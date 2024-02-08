package com.android.firebaseranking;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.view.Gravity;

import android.view.View;
import android.widget.TextView;

import java.util.Map;
import java.util.List;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivity";
    private TextView nombreTextView;
    private FirebaseFirestore db;
    private TextView puntuacionTextView;
    private TextView mensajesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        db = FirebaseFirestore.getInstance();
        // Obtener los valores pasados a través del Intent
        String Name = getIntent().getStringExtra("Name");
        String Username = getIntent().getStringExtra("Username");
        int Mess = getIntent().getIntExtra("Messages", 0);

        // Obtener las referencias a los TextViews del layout
        nombreTextView = findViewById(R.id.nombreTextView);
        puntuacionTextView = findViewById(R.id.puntuacionTextView);
        LinearLayout contenedorMensajes = findViewById(R.id.contenedorMensajes);
        // Establecer el nombre y los puntos como título
        nombreTextView.setText(Name);
        puntuacionTextView.setText(String.valueOf(Mess));

        db.collection("rankings")
                .document("Doc")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            List<Map<String, String>> Messages = (List<Map<String, String>>) document.get("Messages");

                            // Dentro del bucle for
                            for (Map<String, String> message : Messages) {
                                boolean isCurrentUser = message.get("From").equals(Username);
                                if (message.get("To").equals(Name)) {
                                    // Crear un nuevo LinearLayout para este mensaje
                                    LinearLayout mensajeLayout = new LinearLayout(this);
                                    mensajeLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                    mensajeLayout.setOrientation(LinearLayout.VERTICAL);

                                    // Calcula el 70% del ancho de la pantalla
                                    int screenWidth = getResources().getDisplayMetrics().widthPixels;
                                    int maxMessageWidth = (int) (screenWidth * 0.7);

                                    LinearLayout.LayoutParams mensajeLayoutParams = new LinearLayout.LayoutParams(maxMessageWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    mensajeLayoutParams.setMargins(0, 8, 0, 8);
                                    mensajeLayout.setLayoutParams(mensajeLayoutParams);

                                    // Inflar el layout mensaje para este mensaje
                                    View mensajeView = getLayoutInflater().inflate(R.layout.layout_mensaje, null);
                                    TextView textoPerfil = mensajeView.findViewById(R.id.textoPerfil);
                                    textoPerfil.setText(message.get("From"));
                                    if (Username.equals(message.get("From"))) {
                                        mensajeLayoutParams.gravity = Gravity.END;
                                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, 0);
                                        textoPerfil.setLayoutParams(layoutParams);
                                    }

                                    TextView textoMensaje = mensajeView.findViewById(R.id.textoMensaje);
                                    textoMensaje.setText(message.get("Content"));

                                    // Agregar el layout del mensaje al contenedor del mensaje
                                    mensajeLayout.addView(mensajeView);

                                    // Agregar el contenedor del mensaje al contenedor principal de mensajes
                                    contenedorMensajes.addView(mensajeLayout);
                                }
                            }


                        } else {
                            Log.d(TAG, "El documento no existe");
                        }
                    } else {
                        Log.d(TAG, "Error obteniendo documento: ", task.getException());
                    }
                });
    }
}
