# 🔥 Configuración de Firebase - Mi Dosis

## ⚠️ IMPORTANTE: Configuración Requerida

La aplicación **NO FUNCIONARÁ** sin una configuración correcta de Firebase. El archivo `google-services.json` actual contiene datos de ejemplo.

## 📋 Pasos para Configurar Firebase

### 1. Crear Proyecto en Firebase Console

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Haz clic en **"Crear un proyecto"**
3. Dale un nombre como **"mi-dosis-app"**
4. Puedes desactivar Google Analytics si no lo necesitas
5. Haz clic en **"Crear proyecto"**

### 2. Agregar Aplicación Android

1. En la página principal del proyecto, haz clic en el ícono de **Android**
2. Ingresa el **package name**: `com.example.phone_medicatios`
3. **Nombre de la app**: "Mi Dosis"
4. Haz clic en **"Registrar app"**
5. **Descarga** el archivo `google-services.json`
6. **Reemplaza** el archivo en `app/google-services.json` con el que descargaste

### 3. Habilitar Firestore Database

1. En el menú lateral, ve a **"Firestore Database"**
2. Haz clic en **"Crear base de datos"**
3. Selecciona **"Comenzar en modo de prueba"** (para desarrollo)
4. Elige una **ubicación** cercana a ti
5. Haz clic en **"Siguiente"**

### 4. Configurar Reglas de Seguridad (Opcional)

En Firestore Database > Reglas, puedes usar estas reglas básicas para desarrollo:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if true;
    }
  }
}
```

**⚠️ NOTA**: Estas reglas permiten acceso total. Para producción, deberías implementar autenticación.

## 🔍 Verificar la Configuración

### 1. Verificar el archivo google-services.json

El archivo debe contener datos reales, no los de ejemplo. Debe verse algo así:

```json
{
  "project_info": {
    "project_number": "123456789012",
    "project_id": "tu-proyecto-real",
    "storage_bucket": "tu-proyecto-real.appspot.com"
  },
  "client": [
    {
      "client_info": {
        "mobilesdk_app_id": "1:123456789012:android:abcdef1234567890",
        "android_client_info": {
          "package_name": "com.example.phone_medicatios"
        }
      },
      "oauth_client": [],
      "api_key": [
        {
          "current_key": "AIzaSyTuApiKeyRealAqui"
        }
      ],
      "services": {
        "appinvite_service": {
          "other_platform_oauth_client": []
        }
      }
    }
  ],
  "configuration_version": "1"
}
```

### 2. Verificar en Android Studio

1. Abre el proyecto en Android Studio
2. Ve a **Build > Clean Project**
3. Ve a **Build > Rebuild Project**
4. Verifica que no hay errores de compilación

### 3. Verificar Logs

Cuando ejecutes la aplicación, deberías ver en los logs:

```
D/FirebaseRepository: Inicializando FirebaseRepository
D/FirebaseRepository: Firebase instance: com.google.firebase.firestore.FirebaseFirestore@...
D/ReminderViewModel: Inicializando ViewModel con userId: user_...
```

## 🚨 Problemas Comunes

### Error: "Failed to get document because the client is offline"
- **Solución**: Verifica tu conexión a internet
- **Solución**: Asegúrate de que Firestore esté habilitado

### Error: "Permission denied"
- **Solución**: Verifica las reglas de Firestore
- **Solución**: Asegúrate de que el proyecto esté configurado correctamente

### Error: "Project not found"
- **Solución**: Verifica que el `google-services.json` sea del proyecto correcto
- **Solución**: Asegúrate de que el package name coincida

### La app se queda cargando
- **Solución**: Verifica los logs en Android Studio (Logcat)
- **Solución**: Asegúrate de que Firebase esté configurado correctamente

## 📱 Probar la Aplicación

1. Ejecuta la aplicación
2. Ve al dashboard
3. Haz clic en el botón "+" para crear un recordatorio
4. Completa el formulario
5. Haz clic en "Guardar"
6. Verifica que aparece el mensaje de éxito
7. Ve al historial para ver el recordatorio creado

## 🔧 Debugging

### Ver Logs en Android Studio

1. Abre **Logcat** en Android Studio
2. Filtra por **"ReminderViewModel"** o **"FirebaseRepository"**
3. Ejecuta la aplicación y crea un recordatorio
4. Observa los logs para identificar problemas

### Logs Esperados al Crear un Recordatorio

```
D/ReminderViewModel: Guardando recordatorio: Paracetamol
D/FirebaseRepository: Guardando recordatorio: Paracetamol
D/FirebaseRepository: Recordatorio guardado con ID: abc123...
D/ReminderViewModel: Recordatorio guardado con ID: abc123...
D/ReminderViewModel: Creando 2 horarios...
D/FirebaseRepository: Guardando 2 horarios
D/FirebaseRepository: Horarios guardados exitosamente
D/ReminderViewModel: Horarios guardados exitosamente
```

## 📞 Soporte

Si tienes problemas:

1. Verifica que sigues todos los pasos de configuración
2. Revisa los logs en Android Studio
3. Asegúrate de que el archivo `google-services.json` sea real
4. Verifica que Firestore esté habilitado en Firebase Console

---

**¡Una vez configurado Firebase correctamente, la aplicación funcionará perfectamente! 🎉** 