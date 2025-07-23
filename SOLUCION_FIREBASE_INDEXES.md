# 🔧 Solución para Error de Firebase Indexes

## 🚨 Problema Actual

La aplicación está mostrando un error de Firebase que dice:
```
FAILED_PRECONDITION: The query requires an index. You can create it here:
```

Este error ocurre porque las consultas de Firestore que estás realizando requieren índices compuestos.

## ✅ Solución Rápida

### Opción 1: Usar el Script Automático (Recomendado)

1. **Abrir PowerShell como Administrador**
2. **Navegar al directorio del proyecto**:
   ```powershell
   cd "C:\Users\diego\Documents\OneDrive\Escritorio\app_medicamento\phone_medication"
   ```
3. **Ejecutar el script**:
   ```powershell
   .\setup_firebase.ps1
   ```

### Opción 2: Configuración Manual

#### Paso 1: Instalar Node.js
- Descargar desde: https://nodejs.org/
- Instalar la versión LTS

#### Paso 2: Instalar Firebase CLI
```bash
npm install -g firebase-tools
```

#### Paso 3: Iniciar sesión en Firebase
```bash
firebase login
```

#### Paso 4: Aplicar índices
```bash
firebase deploy --only firestore:indexes --project appmedicamentos-c9a8d
```

### Opción 3: Usar Firebase Console

1. **Ir a**: https://console.firebase.google.com/project/appmedicamentos-c9a8d/firestore/indexes
2. **Crear los siguientes índices**:

#### Para colección `medications`:
- **Campos**: `userId` (Ascending), `isActive` (Ascending), `createdAt` (Descending)

#### Para colección `reminders`:
- **Campos**: `userId` (Ascending), `isActive` (Ascending), `createdAt` (Descending)

#### Para colección `reminder_schedules`:
- **Campos**: `reminderId` (Ascending), `time` (Ascending)
- **Campos**: `userId` (Ascending), `isCompleted` (Ascending)

## ⏱️ Tiempo de Construcción

- Los índices pueden tardar **2-5 minutos** en construirse
- Puedes verificar el estado en la consola de Firebase
- Una vez listos, el error desaparecerá automáticamente

## 🔍 Verificación

1. **Esperar** a que los índices estén listos (estado "Enabled")
2. **Reiniciar** la aplicación en Android Studio
3. **Verificar** que el error ya no aparece en "Historial de Recordatorios"

## 📁 Archivos Creados

- `firestore.indexes.json` - Configuración de índices
- `firestore.rules` - Reglas de seguridad
- `firebase.json` - Configuración de Firebase CLI
- `setup_firebase.ps1` - Script de configuración automática
- `FIREBASE_INDEXES_SETUP.md` - Documentación detallada

## 🐛 Si el Problema Persiste

1. **Verificar logs** en Android Studio (Logcat)
2. **Comprobar** que el proyecto Firebase es correcto
3. **Revisar** que las reglas de Firestore permiten acceso
4. **Contactar** si necesitas ayuda adicional

## 📞 Soporte

Si tienes problemas con la configuración:
1. Revisa los logs de Firebase Console
2. Verifica que Node.js esté instalado correctamente
3. Asegúrate de tener permisos de administrador en Windows

---

**Nota**: Una vez configurados los índices, la aplicación funcionará correctamente y las consultas serán más eficientes. 