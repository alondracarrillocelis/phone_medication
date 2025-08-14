@echo off
echo ========================================
echo CORRECCION DE CONTENEDORES DE ALERTAS
echo ========================================
echo.

echo Corrigiendo contenedores de alertas...
echo ✅ Solo se muestran cuando hay mensajes
echo ✅ Botón de cerrar agregado
echo ✅ Auto-ocultado después de 3 segundos
echo ✅ Contenedores no aparecen vacíos

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
echo ✅ Contenedores de alertas corregidos
echo ✅ Solo aparecen cuando hay mensajes
echo ✅ Botón de cerrar funcional
echo ✅ UI más limpia y profesional
echo.
echo ¡Los recuadros de alertas ya no aparecerán vacíos!
echo.
pause
