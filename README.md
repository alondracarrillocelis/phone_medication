# 💊 Mi Dosis - App de Recordatorios de Medicamentos

Una aplicación móvil moderna para Android que te ayuda a recordar tomar tus medicamentos a tiempo. Desarrollada con las últimas tecnologías de Android.

## 🚀 Características Principales

- **📱 Interfaz moderna** con Material Design 3
- **⏰ Recordatorios inteligentes** con horarios personalizables
- **📊 Dashboard con estadísticas** en tiempo real
- **📋 Historial completo** de medicamentos tomados
- **🎯 Frecuencias flexibles** (diaria, días seleccionados, cíclica)
- **🔔 Notificaciones** para no olvidar ninguna dosis
- **💾 Almacenamiento local** con SQLite (Room Database)
- **🔄 Funcionamiento offline** completo

## 🛠️ Tecnologías Utilizadas

- **Android Studio**: IDE principal
- **Jetpack Compose**: UI declarativa moderna
- **Kotlin**: Lenguaje de programación
- **SQLite (Room Database)**: Base de datos local
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
│   ├── SqliteRepository.kt        # Repositorio de datos SQLite
│   ├── SimpleRepository.kt        # Repositorio alternativo
│   ├── TestRepository.kt          # Repositorio para testing
│   ├── Models.kt                  # Modelos de datos
│   ├── entities/                  # Entidades de Room Database
│   ├── dao/                       # Data Access Objects
│   └── database/                  # Configuración de base de datos
├── navigation/
│   └── AppNavigation.kt           # Configuración de navegación
├── screen/
│   ├── DashboardScreen.kt         # Pantalla principal
│   ├── ReminderFormScreen.kt      # Formulario paso 1
│   ├── ReminderScheduleScreen.kt  # Formulario paso 2
│   ├── ReminderProgramScreen.kt   # Formulario paso 3
│   ├── MedicationsScreen.kt       # Lista de medicamentos
│   ├── RemindersScreen.kt         # Lista de recordatorios
│   ├── HistoryScreen.kt           # Historial de recordatorios
│   └── SplashScreen.kt            # Pantalla de bienvenida
├── theme/
│   ├── Color.kt                   # Colores de la aplicación
│   └── Theme.kt                   # Tema general
└── viewmodel/
    └── ReminderViewModel.kt       # Lógica de negocio
```

## 🔧 Configuración

### ✅ **Ventajas de SQLite (Room Database)**

- **Sin dependencias externas** - Funciona completamente offline
- **Más rápido** - Acceso local a datos
- **Sin configuración** - No necesita servicios externos
- **Menos complejidad** - No hay problemas de índices
- **Privacidad** - Datos almacenados localmente
- **Sin costos** - No hay límites de uso

### Requisitos Previos
- Android Studio Arctic Fox o superior
- Android SDK 24+

### Instalación
1. Clona el repositorio
2. Abre el proyecto en Android Studio
3. Sincroniza el proyecto con Gradle
4. Ejecuta la aplicación

## 🎯 Funcionalidades del Backend

### SQLite (Room Database)
- **Entidades**:
  - `MedicationEntity`: Información de medicamentos
  - `ReminderEntity`: Recordatorios configurados
  - `ReminderScheduleEntity`: Horarios específicos

### Operaciones Soportadas
- ✅ Crear recordatorios
- ✅ Leer recordatorios del usuario
- ✅ Marcar como completado
- ✅ Eliminar recordatorios
- ✅ Cargar medicamentos registrados
- ✅ Estadísticas en tiempo real

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
- [ ] Exportación de datos
- [ ] Temas personalizables
- [ ] Backup automático

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 🤝 Contribuir

Las contribuciones son bienvenidas. Por favor, abre un issue o un pull request.

---

**¡Disfruta usando Mi Dosis para mantener un mejor control de tus medicamentos! 💊✨** 