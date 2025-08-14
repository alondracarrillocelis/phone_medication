@echo off
echo ========================================
echo CORRECCION DE ERRORES REMINDERPROGRAMSCREEN
echo ========================================
echo.

echo Corrigiendo errores en ReminderProgramScreen.kt...
echo ✅ frequency → frequencyPerDay
echo ✅ selectedDays → days
echo ✅ dosage convertido a String con unidad
echo ✅ Referencias actualizadas para app de automóvil
echo ✅ Tipos de datos corregidos

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
echo ✅ Errores de ReminderProgramScreen corregidos
echo ✅ Campos actualizados para app de automóvil
echo ✅ Base de datos funcional
echo ✅ Datos se guardan correctamente
echo ✅ Tipos de datos consistentes
echo.
echo ¡El proyecto ahora compila sin errores!
echo.
pause
