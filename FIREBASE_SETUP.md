# Configuración de Firebase para Phone Medicatios

## Pasos para configurar Firebase:

### 1. Configurar Firebase Console

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Crea un nuevo proyecto o selecciona el existente "medications-reminder1"
3. Habilita Firestore Database
4. Habilita Authentication (opcional, para futuras implementaciones)

### 2. Configurar Firestore Database

1. En la consola de Firebase, ve a "Firestore Database"
2. Crea una base de datos en modo de prueba
3. Configura las reglas de seguridad usando el archivo `firestore.rules`

### 3. Estructura de la Base de Datos

La aplicación usa las siguientes colecciones:

#### Colección: `medications`
```json
{
  "id": "auto-generated",
  "name": "Nombre del medicamento",
  "dosage": "500",
  "unit": "mg",
  "type": "Tableta",
  "description": "Descripción opcional",
  "instructions": "Instrucciones de uso",
  "userId": "ID del usuario",
  "createdAt": "Timestamp",
  "isActive": true
}
```

#### Colección: `reminders`
```json
{
  "id": "auto-generated",
  "medicationId": "ID del medicamento",
  "medicationName": "Nombre del medicamento",
  "dosage": "500",
  "unit": "mg",
  "type": "Tableta",
  "frequency": "Diariamente",
  "firstDoseTime": "8:00 a.m.",
  "doseTime": "8:00 p.m.",
  "userId": "ID del usuario",
  "createdAt": "Timestamp",
  "isActive": true,
  "totalDoses": 2,
  "completedDoses": 0
}
```

### 4. Reglas de Seguridad

Las reglas de seguridad están configuradas para:
- Permitir lectura/escritura solo a usuarios autenticados
- Restringir acceso a datos del propio usuario
- Prevenir acceso no autorizado a datos de otros usuarios

### 5. Funcionalidades Implementadas

- ✅ Consulta de medicamentos desde Firebase
- ✅ Consulta de recordatorios desde Firebase
- ✅ Consulta de horarios del día desde Firebase
- ✅ Estadísticas del usuario desde Firebase
- ✅ Agregar nuevos medicamentos a Firebase
- ✅ Agregar nuevos recordatorios a Firebase
- ✅ Actualizar datos en Firebase
- ✅ Eliminar datos de Firebase
- ✅ Indicador de carga durante operaciones
- ✅ Manejo de errores con fallback a datos locales
- ✅ Botón de refresh para actualizar datos

### 6. Próximos Pasos (Opcionales)

1. **Implementar Autenticación de Usuarios**
   - Usar Firebase Auth para login/registro
   - Reemplazar userId hardcodeado con UID real del usuario

2. **Sincronización en Tiempo Real**
   - Implementar listeners de Firestore para cambios en tiempo real
   - Notificaciones push para recordatorios

3. **Backup y Restauración**
   - Exportar/importar datos del usuario
   - Sincronización entre dispositivos

### 7. Solución de Problemas

#### Error de Gradle Cache
Si encuentras errores de cache de Gradle:
```bash
# Limpiar cache de Gradle
rmdir /s /q "C:\Users\[USERNAME]\.gradle\caches"

# Limpiar proyecto
.\gradlew clean

# Reconstruir
.\gradlew build
```

#### Error de Conexión a Firebase
- Verifica que el archivo `google-services.json` esté en la carpeta `app/`
- Verifica la conexión a internet
- Verifica las reglas de seguridad de Firestore

### 8. Testing

Para probar la funcionalidad:
1. Ejecuta la aplicación
2. Ve al Dashboard
3. Los datos se cargarán desde Firebase automáticamente
4. Usa el botón de refresh para actualizar datos
5. Crea nuevos recordatorios para verlos en Firebase

### 9. Monitoreo

- Usa Firebase Console para monitorear el uso de la base de datos
- Revisa los logs de la aplicación para debugging
- Monitorea el rendimiento de las consultas 