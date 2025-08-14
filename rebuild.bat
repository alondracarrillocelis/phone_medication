@echo off
echo ========================================
echo RECONSTRUCCION DE LA APP
echo ========================================
echo.

echo Limpiando proyecto...
call gradlew clean

echo.
echo Construyendo proyecto...
call gradlew build

echo.
echo ========================================
echo CONSTRUCCION COMPLETADA
echo ========================================
echo.
echo La app está lista para usar.
echo Los datos se cargarán automáticamente.
echo.
pause
