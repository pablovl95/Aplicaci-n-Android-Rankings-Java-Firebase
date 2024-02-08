package com.android.firebaseranking.Activitys;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class DataBase {
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void PostIntentMap(String collection, String document, Context context, final Class<?> redirect, Map<String, Object> userData) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collection)
                .document(document)
                .set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Datos guardados exitosamente en la base de datos
                        Toast.makeText(context, "Datos guardados exitosamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, redirect);
                        context.startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al guardar los datos en la base de datos
                        Toast.makeText(context, "Error al guardar los datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void AddMapToArrayIntent(String collection, String document,String Array, Context context, final Class<?> redirect, Map<String, Object> Map){
        db.collection(collection)
                .document(document)
                .update(Array,FieldValue.arrayUnion(Map))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Datos guardados exitosamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, redirect);
                    context.startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Algo ha salido mal", Toast.LENGTH_SHORT).show();
                    // Manejar el error de forma adecuada
                });
    }



}
