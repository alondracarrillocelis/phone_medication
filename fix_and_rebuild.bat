@echo off
echo ========================================
echo CORRECCION Y RECONSTRUCCION DE LA APP
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
echo ✅ Error de RemindersScreen.kt corregido
echo ✅ Función ensureDataLoaded() reemplazada por loadData()
echo ✅ La app está lista para usar
echo.
echo Los datos se cargarán automáticamente.
echo.
pause
