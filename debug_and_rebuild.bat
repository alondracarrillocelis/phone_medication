@echo off
echo ========================================
echo DEBUG Y RECONSTRUCCION DE LA APP
echo ========================================
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
echo Ahora puedes:
echo 1. Ejecutar la app en tu dispositivo
echo 2. Usar los botones "Debug" y "Test Firebase" en el dashboard
echo 3. Revisar los logs en Android Studio
echo.
echo Presiona cualquier tecla para continuar...
pause
