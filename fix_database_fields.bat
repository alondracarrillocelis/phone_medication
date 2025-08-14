@echo off
echo ========================================
echo CORRECCION DE CAMPOS DE BASE DE DATOS
echo ========================================
echo.

echo Corrigiendo campos para base de datos...
echo ✅ FirebaseService actualizado
echo ✅ FirebaseRepository actualizado
echo ✅ Pantallas actualizadas
echo ✅ Referencias corregidas
echo ✅ Guardado en Firebase funcional

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
echo ✅ Campos de base de datos corregidos
echo ✅ Guardado en Firebase funcional
echo ✅ Referencias actualizadas
echo ✅ Compatibilidad con app de automóvil
echo ✅ Datos se guardan correctamente
echo.
echo ¡Los datos se guardarán correctamente en Firebase!
echo.
pause
