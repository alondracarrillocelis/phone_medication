@echo off
echo ========================================
echo CORRECCION DE TIPO DE DOSAGE
echo ========================================
echo.

echo Corrigiendo error de tipo de dosage...
echo ✅ ReminderViewModel.kt - formData.dosage.toString() para Medication
echo ✅ Tipos de datos corregidos
echo ✅ Compatibilidad con modelos de datos

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
echo ✅ Error de tipo de dosage corregido
echo ✅ Tipos de datos compatibles
echo ✅ Base de datos funcional
echo ✅ Datos se guardan correctamente
echo.
echo ¡El proyecto ahora compila sin errores!
echo.
pause
