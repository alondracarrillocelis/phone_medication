# Mi Dosis - App de Recordatorios de Medicamentos

Una aplicación móvil desarrollada en Android con Jetpack Compose para ayudar a los usuarios a gestionar sus recordatorios de medicamentos de manera eficiente.

## 🚀 Características Principales

### ✅ Funcionalidades Implementadas

- **Dashboard Principal**: Vista general con recordatorios del día, medicamentos registrados y calendario semanal
- **Creación de Recordatorios**: Formulario de 3 pasos para configurar medicamentos
- **Gestión de Horarios**: Configuración de frecuencias (diaria, cíclica, días seleccionados)
- **Historial Completo**: Vista de todos los recordatorios creados con opción de eliminación
- **Marcado de Completado**: Funcionalidad para marcar medicamentos como tomados
- **Backend con Firebase**: Almacenamiento en tiempo real con Firestore
- **Navegación Fluida**: Sistema de navegación entre pantallas optimizado

### 🎨 Mejoras de Diseño

- **Tamaños de Letra Optimizados**: Mejor legibilidad en todos los dispositivos
- **Componentes Rediseñados**: Cards, botones y elementos visuales mejorados
- **Experiencia de Usuario**: Navegación intuitiva y feedback visual
- **Responsive Design**: Adaptable a diferentes tamaños de pantalla

## 📱 Pantallas de la Aplicación

### 1. Splash Screen
- Pantalla de bienvenida con logo de la aplicación
- Transición automática al dashboard

### 2. Dashboard
- Resumen de recordatorios del día
- Calendario semanal interactivo
- Lista de medicamentos registrados
- Botón de acceso al historial
- FAB para crear nuevo recordatorio

### 3. Formulario de Recordatorio (3 Pasos)
- **Paso 1**: Información del medicamento (nombre, dosis, tipo, unidad)
- **Paso 2**: Configuración de horarios y frecuencia
- **Paso 3**: Confirmación y guardado

### 4. Historial
- Lista completa de recordatorios
- Opción de eliminación
- Estado vacío con call-to-action

## 🛠️ Tecnologías Utilizadas

- **Android Studio**: IDE principal
- **Jetpack Compose**: UI declarativa moderna
- **Kotlin**: Lenguaje de programación
- **Firebase Firestore**: Base de datos en tiempo real
- **Material Design 3**: Sistema de diseño
- **Navigation Compose**: Navegación entre pantallas
- **ViewModel**: Gestión de estado de la UI
- **Coroutines**: Programación asíncrona

## 📊 Estructura del Proyecto

```
app/src/main/java/com/example/phone_medicatios/
├── MainActivity.kt                 # Actividad principal
├── components/
│   ├── CommonComponents.kt        # Componentes reutilizables
│   └── StatusMessages.kt          # Mensajes de estado
├── data/
│   ├── FirebaseRepository.kt      # Repositorio de datos
│   └── Models.kt                  # Modelos de datos
├── navigation/
│   └── AppNavigation.kt           # Configuración de navegación
├── screen/
│   ├── DashboardScreen.kt         # Pantalla principal
│   ├── ReminderFormScreen.kt      # Formulario paso 1
│   ├── ReminderScheduleScreen.kt  # Formulario paso 2
│   ├── ReminderProgramScreen.kt   # Formulario paso 3
│   ├── HistoryScreen.kt           # Historial de recordatorios
│   └── SplashScreen.kt            # Pantalla de bienvenida
├── theme/
│   ├── Color.kt                   # Colores de la aplicación
│   └── Theme.kt                   # Tema general
└── viewmodel/
    └── ReminderViewModel.kt       # Lógica de negocio
```

## 🔧 Configuración

### ⚠️ IMPORTANTE: Problema de Firebase

**La aplicación se queda cargando al crear recordatorios** porque el archivo `google-services.json` actual contiene datos de ejemplo, no datos reales de Firebase.

### Solución Temporal (Testing Local)

Para probar la aplicación **SIN configurar Firebase**:

1. Abre `ReminderViewModel.kt`
2. Cambia la línea:
   ```kotlin
   private val repository = LocalRepository() // Para testing local
   ```
3. Ejecuta la aplicación - funcionará con almacenamiento local

### Solución Permanente (Con Firebase)

Para usar Firebase real:

1. **Configura Firebase** siguiendo las instrucciones en `FIREBASE_SETUP.md`
2. En `ReminderViewModel.kt`, cambia a:
   ```kotlin
   private val repository = FirebaseRepository() // Para Firebase real
   ```

### Requisitos Previos
- Android Studio Arctic Fox o superior
- Android SDK 24+
- Cuenta de Firebase (opcional para testing local)

### Instalación
1. Clona el repositorio
2. Abre el proyecto en Android Studio
3. **Para testing local**: Usa `LocalRepository()` (ya configurado)
4. **Para Firebase real**: 
   - Configura Firebase siguiendo `FIREBASE_SETUP.md`
   - Cambia a `FirebaseRepository()`
5. Sincroniza el proyecto con Gradle
6. Ejecuta la aplicación

## 🎯 Funcionalidades del Backend

### Firebase Firestore
- **Colecciones**:
  - `medications`: Información de medicamentos
  - `reminders`: Recordatorios configurados
  - `reminder_schedules`: Horarios específicos

### Operaciones Soportadas
- ✅ Crear recordatorios
- ✅ Leer recordatorios del usuario
- ✅ Marcar como completado
- ✅ Eliminar recordatorios
- ✅ Cargar medicamentos registrados

## 🎨 Paleta de Colores

- **Color Principal**: `#B295C7` (Púrpura suave)
- **Fondo**: `#FFFFFF` (Blanco)
- **Texto**: `#000000` (Negro)
- **Texto Secundario**: `#808080` (Gris)

## 📱 Compatibilidad

- **Versión Mínima**: Android 7.0 (API 24)
- **Versión Objetivo**: Android 14 (API 35)
- **Dispositivos**: Teléfonos y tablets Android

## 🚀 Próximas Mejoras

- [ ] Notificaciones push
- [ ] Autenticación de usuarios
- [ ] Sincronización entre dispositivos
- [ ] Estadísticas de adherencia
- [ ] Modo offline
- [ ] Exportación de datos
- [ ] Temas personalizables

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Por favor, abre un issue o pull request para sugerir mejoras.

---

**Desarrollado con ❤️ para mejorar la salud de las personas** 