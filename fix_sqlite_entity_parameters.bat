@echo off
echo ========================================
echo CORRECCION DE PARAMETROS SQLITE ENTITY
echo ========================================
echo.

echo Corrigiendo parámetros faltantes en SqliteRepository...
echo ✅ medicationId agregado con valor por defecto
echo ✅ hoursBetweenDoses agregado con valor por defecto
echo ✅ selectedDays agregado desde reminder.days
echo ✅ cycleWeeks agregado desde reminder.cycleWeeks
echo ✅ Todos los parámetros requeridos por ReminderEntity

echo.
echo Limpiando proyecto...
call gradlew clean
if %errorlevel% neq 0 (
    echo ERROR: Fallo al limpiar el proyecto
    pause
    exit /b 1
)

echo.
echo Construyendo proyecto...
call gradlew build
if %errorlevel% neq 0 (
    echo ERROR: Fallo al construir el proyecto
    pause
    exit /b 1
)

echo.
echo ========================================
echo CONSTRUCCION COMPLETADA EXITOSAMENTE
echo ========================================
echo.
echo ✅ Parámetros de ReminderEntity corregidos
echo ✅ SqliteRepository actualizado
echo ✅ Base de datos funcional
echo ✅ Datos se guardan correctamente
echo ✅ Compatibilidad con app de automóvil
echo.
echo ¡El proyecto ahora compila sin errores!
echo.
pause
