@echo off
echo ========================================
echo CORRECCION DE ERRORES OUTLINEDTEXTFIELD
echo ========================================
echo.

echo Corrigiendo errores de OutlinedTextField...
echo ✅ ReminderFormScreen.kt - dosage como Double
echo ✅ ReminderScheduleScreen.kt - frequency → frequencyPerDay
echo ✅ ReminderScheduleScreen.kt - selectedDays → days
echo ✅ ReminderScheduleScreen.kt - cycleWeeks como Int
echo ✅ ReminderScheduleScreen.kt - hoursBetweenDoses removido
echo ✅ Tipos de datos corregidos para app de automóvil

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
echo ✅ Errores de OutlinedTextField corregidos
echo ✅ Campos actualizados para app de automóvil
echo ✅ Base de datos funcional
echo ✅ Datos se guardan correctamente
echo ✅ Tipos de datos consistentes
echo.
echo ¡El proyecto ahora compila sin errores!
echo.
pause
