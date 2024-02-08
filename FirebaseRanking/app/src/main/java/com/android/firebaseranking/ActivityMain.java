package com.android.firebaseranking;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

public class ActivityMain extends AppCompatActivity {
    private static final String TAG = "ActivityMain";
    private FirebaseFirestore db;
    private LinearLayout linearLayout;
    private String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String Username;
    // Declarar la variable sharedPreferences como un miembro de la clase ActivityMain
    private SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Obtener instancia de FirebaseFirestore
        db = FirebaseFirestore.getInstance();
        ImageView image = findViewById(R.id.ic_edit);
        linearLayout = findViewById(R.id.linearLayout);
        TextView titulo = findViewById(R.id.titulo);
        ImageView imageUser = findViewById(R.id.user_icon);

        imageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mostrar el menú emergente cuando se hace clic en la imagen
                showPopupMenu(v);
            }
        });
        db.collection("users")
                .document(user)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Username = document.getString("username");
                            Log.d(TAG, "Username: " + Username);
                        }
                    }
                });
        // Realizar la llamada a Firestore
        db.collection("rankings")
                .document("Doc")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Obtener los datos del documento
                            List<String>  lista =(List<String>) document.get("Players");
                            String tituloInicial = (String) document.get("Name");
                            // Obtener Mensajes
                            List<Map<String, String>> Messages = (List<Map<String, String>>) document.get("Messages");
                            Map<String, Integer> MessagesForPersons = contarMensajes(Messages);
                            crearVistasPersonajes(MessagesForPersons);

                            titulo.setText(tituloInicial);
                            ImageView buttonAdd = findViewById(R.id.button_Add);
                            buttonAdd.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(ActivityMain.this, AddActivity.class);
                                    intent.putExtra("lista", (Serializable) lista);
                                    startActivity(intent);
                                }
                            });
                            image.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // Abrir la ventana para cambiar el título
                                    openChangeTitleDialog();
                                }
                            });

                        } else {
                            Log.d(TAG, "El documento no existe");
                        }
                    } else {
                        Log.d(TAG, "Error obteniendo documento: ", task.getException());
                    }
                });

    }

    public static Map<String, Map<String, List<Set<String>>>> procesarMensajes(List<String> nombres, List<Map<String, String>> mensajes) {
        Map<String, Map<String, List<Set<String>>>> mapaMensajes = new HashMap<>();

        for (String nombre : nombres) {
            mapaMensajes.put(nombre, new HashMap<>());
        }

        for (Map<String, String> mensaje : mensajes) {
            String destinatario = mensaje.get("To");
            String creador = mensaje.get("From");
            String descripcion = mensaje.get("Content");
            //String fecha = (String) mensaje.get("Date");

            if (mapaMensajes.containsKey(destinatario)) {
                Map<String, List<Set<String>>> mensajesDestinatario = mapaMensajes.get(destinatario);
                if (mensajesDestinatario.containsKey(creador)) {
                    List<Set<String>> mensajesCreador = mensajesDestinatario.get(creador);
                    Set<String> mensajeSet = new HashSet<>();
                    //mensajeSet.add(fecha);
                    mensajeSet.add(descripcion);
                    mensajesCreador.add(mensajeSet);
                } else {
                    List<Set<String>> mensajesCreador = new ArrayList<>();
                    Set<String> mensajeSet = new HashSet<>();
                    //mensajeSet.add(fecha);
                    mensajeSet.add(descripcion);
                    mensajesCreador.add(mensajeSet);
                    mensajesDestinatario.put(creador, mensajesCreador);
                }
            }
        }

        return mapaMensajes;
    }

    public static Map<String, Integer> contarMensajes(List<Map<String, String>> Messages) {
        Map<String, Integer> con = new HashMap<>();
        for (Map<String, String> mensaje : Messages) {
            String destinatario = mensaje.get("To");
            if (con.containsKey(destinatario)) {
                con.put(destinatario, con.get(destinatario) + 1);
            } else {
                con.put(destinatario, 1);
            }
        }

        return con;
    }
    private void crearVistasPersonajes(Map<String, Integer> personajes) {
        // Ordenar el mapa por valor de forma descendente
        List<Map.Entry<String, Integer>> list = new ArrayList<>(personajes.entrySet());
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // Crear vistas para cada personaje y puntuación
        int position = 1;
        for (Map.Entry<String, Integer> entry : list) {
            String personaje = entry.getKey();
            int Messages = entry.getValue();

            LinearLayout layout = new LinearLayout(this);
            layout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            layout.setOrientation(LinearLayout.HORIZONTAL);

            TextView textViewNumero = new TextView(this);
            textViewNumero.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1));
            textViewNumero.setText(String.valueOf(position));
            textViewNumero.setGravity(Gravity.CENTER);
            textViewNumero.setPadding(2, 8, 2, 2);
            layout.addView(textViewNumero);

            TextView textViewPersonaje = new TextView(this);
            textViewPersonaje.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    2));
            textViewPersonaje.setText(personaje);
            textViewPersonaje.setGravity(Gravity.CENTER);
            textViewPersonaje.setPadding(2, 8, 2, 2);
            layout.addView(textViewPersonaje);

            TextView textViewPuntuacion = new TextView(this);
            textViewPuntuacion.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1));
            textViewPuntuacion.setText(String.valueOf(Messages));
            textViewPuntuacion.setGravity(Gravity.CENTER);
            textViewPuntuacion.setPadding(2, 8, 2, 2);
            layout.addView(textViewPuntuacion);

            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(80, 80));
            imageView.setImageResource(R.drawable.ic_message);
            imageView.setPadding(2, 8, 2, 2);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ActivityMain.this, DetailActivity.class);
                    intent.putExtra("Name", personaje);
                    intent.putExtra("Messages", Messages);
                    intent.putExtra("Username", Username);
                    startActivity(intent);
                }
            });

            layout.addView(imageView);

            linearLayout.addView(layout);
            position++;
        }

    }
    private void openChangeTitleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cambiar título");

        // Crear un EditText para que el usuario ingrese el nuevo título
        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);

        // Configurar el botón "Aceptar" del diálogo
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Obtener el nuevo título ingresado por el usuario
                String newTitle = input.getText().toString().trim();

                // Actualizar el título en la base de datos de Firebase
                updateTitleInFirebase(newTitle);
            }
        });

        // Configurar el botón "Cancelar" del diálogo
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        // Mostrar el diálogo
        builder.show();
    }
    private void updateTitleInFirebase(String newTitle) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Crear un mapa con el nuevo título
        Map<String, Object> titleData = new HashMap<>();
        titleData.put("Name", newTitle);

        // Actualizar el documento en la colección "rankers" con el nuevo título
        db.collection("rankings")
                .document("Doc")
                .update("Name", newTitle)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Post guardado correctamente");
                    recreate();

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Ocurrió un error al actualizar el título en la base de datos
                        Toast.makeText(ActivityMain.this, "Error al actualizar el título", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error al actualizar el título", e);
                    }
                });

    }
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(ActivityMain.this, view);
        popupMenu.getMenu().add("Salir de la sesión");

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                // Manejar la opción del menú
                if (menuItem.getTitle().equals("Salir de la sesión")) {
                    signOut();
                    return true;
                }
                return false;
            }
        });

        popupMenu.show();
    }

    private void signOut() {
        // Cerrar la sesión del usuario
        FirebaseAuth.getInstance().signOut();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("Email");
        editor.remove("Password");
        editor.apply();
        // Redirigir a la actividad de inicio de sesión
        Intent intent = new Intent(ActivityMain.this, AuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}