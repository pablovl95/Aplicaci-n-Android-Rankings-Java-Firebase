package com.android.firebaseranking;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import android.content.SharedPreferences;

public class AuthActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button buttonRegister;

    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (sharedPreferences.contains("Email") && sharedPreferences.contains("Password")) {
            String email = sharedPreferences.getString("Email", "");
            String password = sharedPreferences.getString("Password", "");
            loginUser(email, password);
        }
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                loginUser(email, password);

            }
        });
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Guarda Usuarios
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("Email", email);
                            editor.putString("Password", password);
                            editor.apply();
                            // Inicio de sesión exitoso
                            Toast.makeText(AuthActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference userRef = db.collection("users").document(uid);

                            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists() && document.contains("username")) {

                                            // El usuario ya tiene un nombre de usuario guardado
                                            Intent intent = new Intent(AuthActivity.this, ActivityMain.class);
                                            startActivity(intent);
                                        } else {
                                            // El usuario no tiene un nombre de usuario guardado, solicitarlo
                                            showUsernameDialog(uid);
                                        }
                                    } else {
                                        // Error al obtener los datos del usuario
                                        Toast.makeText(AuthActivity.this, "Error al obtener los datos del usuario", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            // Error en el inicio de sesión
                            Toast.makeText(AuthActivity.this, "Algo ha salido mal. Por favor, inténtalo nuevamente", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showUsernameDialog(String uid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nombre de usuario");
        builder.setMessage("Por favor, ingresa un nombre de usuario:");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String username = input.getText().toString().trim();

                if (!username.isEmpty()) {
                    Map<String, Object> user = new HashMap<>();
                    user.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    user.put("username", username);

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("users").document(uid).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(AuthActivity.this, "Datos de usuario guardados correctamente", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AuthActivity.this, ActivityMain.class);
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AuthActivity.this, "Error al guardar los datos de usuario", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(AuthActivity.this, "Por favor, ingresa un nombre de usuario válido", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

}
