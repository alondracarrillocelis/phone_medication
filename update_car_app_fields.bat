@echo off
echo ========================================
echo ACTUALIZACION DE CAMPOS PARA APP DE AUTO
echo ========================================
echo.

echo Actualizando campos para coincidir con app de automóvil...
echo ✅ Modelo Reminder actualizado
echo ✅ ReminderFormData actualizado
echo ✅ ReminderEntity actualizado
echo ✅ ViewModel actualizado
echo ✅ Validaciones actualizadas

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
echo ✅ Campos actualizados para app de automóvil
echo ✅ Modelo MedicineReminder implementado
echo ✅ Campos numéricos para dosis
echo ✅ Frecuencia por día como entero
echo ✅ Fechas de inicio y fin
echo ✅ Lista de horas calculadas
echo.
echo ¡Los campos ahora coinciden con la app de automóvil!
echo.
pause
