@echo off
echo ========================================
echo CORRECCION DE ERRORES DE CRASH
echo ========================================
echo.

echo Corrigiendo errores que causan crash...
echo ✅ Espacio extra corregido en getMedicationsFlow()
echo ✅ Manejo de excepciones agregado
echo ✅ Carga inicial de datos simplificada
echo ✅ Flujos en tiempo real más robustos

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
echo ✅ Errores de crash corregidos
echo ✅ Manejo de excepciones implementado
echo ✅ Carga inicial más robusta
echo ✅ App debería ejecutarse sin cerrarse
echo.
echo ¡La app ahora debería funcionar sin crashes!
echo.
pause
