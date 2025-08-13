# Configuración de Firebase - Acceso al Proyecto

## 🚨 Problema identificado
El proyecto `medications-reminder1` fue creado por otra persona y no tienes acceso desde tu cuenta de Firebase.

## 🔧 Soluciones disponibles

### Opción 1: Solicitar acceso al proyecto existente (Recomendada)

**Pasos para la persona que creó el proyecto:**

1. **Ir a Firebase Console**: https://console.firebase.google.com/
2. **Seleccionar el proyecto**: `medications-reminder1`
3. **Ir a Project Settings** (ícono de engranaje en la esquina superior izquierda)
4. **Ir a la pestaña "Users and permissions"**
5. **Hacer clic en "Add user"**
6. **Agregar tu email** con permisos de **Editor** o **Owner**
7. **Enviar la invitación**

**Pasos para ti (una vez que recibas la invitación):**

1. **Revisar tu email** y aceptar la invitación
2. **Ir a Firebase Console** y verificar que puedes ver el proyecto
3. **Descargar el archivo `google-services.json`**:
   - Project Settings → General
   - En "Your apps", buscar la app Android
   - Hacer clic en "Download google-services.json"
4. **Reemplazar el archivo** en tu proyecto: `phone_medication/app/google-services.json`

### Opción 2: Crear tu propio proyecto de Firebase

**Si no puedes obtener acceso al proyecto del equipo:**

1. **Ir a Firebase Console**: https://console.firebase.google.com/
2. **Hacer clic en "Create a project"**
3. **Nombra tu proyecto** (ej: `medications-reminder-diego`)
4. **Seguir los pasos de configuración**:
   - Deshabilitar Google Analytics (opcional)
   - Hacer clic en "Create project"
5. **Habilitar Firestore Database**:
   - En el menú lateral, hacer clic en "Firestore Database"
   - Hacer clic en "Create database"
   - Seleccionar "Start in test mode" (para desarrollo)
   - Elegir la ubicación más cercana
6. **Agregar tu aplicación Android**:
   - Project Settings → General
   - En "Your apps", hacer clic en "Add app"
   - Seleccionar Android
   - Package name: `com.example.phone_medicatios`
   - App nickname: `Medicamentos App`
   - Hacer clic en "Register app"
7. **Descargar `google-services.json`** y colocarlo en `phone_medication/app/`

## 📋 Pasos para configurar el proyecto

### Paso 1: Actualizar configuración
```bash
update_firebase_config.bat
```

### Paso 2: Desplegar reglas de Firestore
```bash
deploy_firestore_rules.bat
```

### Paso 3: Compilar y probar
```bash
clean_and_rebuild.bat
```

## 🔍 Verificación

### 1. Verificar en Firebase Console
- Ir a **Firestore Database**
- Deberías ver las colecciones creadas automáticamente cuando guardes datos

### 2. Verificar logs en Android Studio
En Logcat, buscar estos mensajes:
```
MedicApp: Firebase inicializado correctamente
FirebaseService: Intentando agregar recordatorio: [nombre]
FirebaseService: Reminder added with ID: [id]
```

### 3. Verificar archivo google-services.json
El archivo debe contener:
- `project_id`: ID de tu proyecto
- `package_name`: `com.example.phone_medicatios`
- `api_key`: Clave de API válida

## ⚠️ Notas importantes

1. **Si usas el proyecto del equipo**: Asegúrate de coordinar con tu equipo para no sobrescribir datos
2. **Si creas tu propio proyecto**: Tendrás tu propia base de datos separada
3. **Reglas de Firestore**: Las reglas actuales permiten acceso total (solo para desarrollo)
4. **Backup**: Siempre haz backup de datos importantes antes de hacer cambios

## 🆘 Si tienes problemas

### Error: "Project not found"
- Verifica que el `project_id` en `google-services.json` sea correcto
- Asegúrate de tener acceso al proyecto en Firebase Console

### Error: "Permission denied"
- Verifica que tengas permisos de Editor o Owner en el proyecto
- Contacta al administrador del proyecto

### Error: "Network error"
- Verifica tu conexión a internet
- Asegúrate de que no haya firewall bloqueando las conexiones

## 📞 Contacto para soporte

Si necesitas ayuda adicional:
1. Verifica los logs en Logcat para errores específicos
2. Revisa la documentación de Firebase
3. Contacta al administrador del proyecto del equipo

