package com.example.phone_medicatios

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class MedicApp : Application() {
    
    companion object {
        private const val TAG = "MedicApp"
    }
    
    override fun onCreate() {
        super.onCreate()
        
        try {
            // Inicializar Firebase
            FirebaseApp.initializeApp(this)
            
            // Configurar Firestore para modo debug
            val db = FirebaseFirestore.getInstance()
            db.firestoreSettings = com.google.firebase.firestore.FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
            
            Log.d(TAG, "Firebase inicializado correctamente")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error al inicializar Firebase: ${e.message}", e)
        }
    }
}
