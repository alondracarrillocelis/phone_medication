# Configuración de Índices de Firestore

## Problema Actual
La aplicación está mostrando un error de Firebase que indica que se necesitan índices compuestos para las consultas que se están realizando.

## Solución

### Opción 1: Usar Firebase CLI (Recomendado)

1. **Instalar Firebase CLI** (si no lo tienes):
   ```bash
   npm install -g firebase-tools
   ```

2. **Iniciar sesión en Firebase**:
   ```bash
   firebase login
   ```

3. **Inicializar Firebase en el proyecto**:
   ```bash
   firebase init firestore
   ```

4. **Aplicar los índices**:
   ```bash
   firebase deploy --only firestore:indexes
   ```

### Opción 2: Configuración Manual en Firebase Console

1. **Ir a Firebase Console**: https://console.firebase.google.com
2. **Seleccionar tu proyecto**: `appmedicamentos-c9a8d`
3. **Ir a Firestore Database**
4. **Ir a la pestaña "Índices"**
5. **Crear los siguientes índices compuestos**:

#### Índice para la colección `medications`:
- **Campos**: 
  - `userId` (Ascending)
  - `isActive` (Ascending) 
  - `createdAt` (Descending)

#### Índice para la colección `reminders`:
- **Campos**:
  - `userId` (Ascending)
  - `isActive` (Ascending)
  - `createdAt` (Descending)

#### Índice para la colección `reminder_schedules`:
- **Campos**:
  - `reminderId` (Ascending)
  - `time` (Ascending)

#### Índice adicional para `reminder_schedules`:
- **Campos**:
  - `userId` (Ascending)
  - `isCompleted` (Ascending)

### Opción 3: Usar el enlace directo del error

El error en la aplicación proporciona un enlace directo para crear el índice específico. Puedes hacer clic en ese enlace para crear automáticamente el índice necesario.

## Verificación

Después de crear los índices:

1. **Esperar 1-2 minutos** para que los índices se construyan
2. **Reiniciar la aplicación** en Android Studio
3. **Verificar que el error ya no aparece** en la pantalla de "Historial de Recordatorios"

## Notas Importantes

- Los índices pueden tardar varios minutos en construirse, especialmente si ya tienes datos en las colecciones
- El estado de construcción de los índices se puede ver en la consola de Firebase
- Una vez creados, los índices mejorarán significativamente el rendimiento de las consultas

## Estructura de Datos

Las consultas que requieren estos índices son:

```kotlin
// En getMedications()
.whereEqualTo("userId", userId)
.whereEqualTo("isActive", true)
.orderBy("createdAt", Query.Direction.DESCENDING)

// En getReminders()
.whereEqualTo("userId", userId)
.whereEqualTo("isActive", true)
.orderBy("createdAt", Query.Direction.DESCENDING)
```

Estas consultas filtran por usuario y estado activo, y luego ordenan por fecha de creación, lo cual requiere índices compuestos en Firestore. 