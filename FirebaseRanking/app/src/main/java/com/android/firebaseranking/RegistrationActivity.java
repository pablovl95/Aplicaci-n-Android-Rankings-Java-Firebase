package com.android.firebaseranking;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.android.firebaseranking.Activitys.DataBase;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private EditText editTextNombre;
    private EditText editTextApellidos;
    private EditText editTextUsuario;
    private EditText editTextCorreo;
    private EditText editTextContraseña;
    private Button btnRegistrarse;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        editTextNombre = findViewById(R.id.editTextNombre);
        editTextApellidos = findViewById(R.id.editTextApellidos);
        editTextUsuario = findViewById(R.id.editTextUsuario);
        editTextCorreo = findViewById(R.id.editTextCorreo);
        editTextContraseña = findViewById(R.id.editTextContra);
        btnRegistrarse = findViewById(R.id.btnRegistrarse);

        btnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextCorreo.getText().toString().trim();
                String password = editTextContraseña.getText().toString().trim();

                registrarUsuario(email, password);
            }
        });
    }

    private void registrarUsuario(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registro exitoso, el usuario ha sido creado en Firebase Authentication
                            String uid = mAuth.getCurrentUser().getUid();

                            // Obtener los datos del formulario
                            String nombre = editTextNombre.getText().toString().trim();
                            String apellidos = editTextApellidos.getText().toString().trim();
                            String usuario = editTextUsuario.getText().toString().trim();
                            String correo = editTextCorreo.getText().toString().trim();

                            // Crear un mapa con los datos del usuario
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("nombre", nombre);
                            userData.put("apellidos", apellidos);
                            userData.put("username", usuario);
                            userData.put("email", correo);

                            // Obtener la instancia de Firestore
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            // Guardar los datos en la colección "users" con el UID como documento
                            DataBase.PostIntentMap("users",uid,RegistrationActivity.this,AuthActivity.class, userData);
                        } else {
                            // Error en el registro
                            Toast.makeText(RegistrationActivity.this, "Error en el registro: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
