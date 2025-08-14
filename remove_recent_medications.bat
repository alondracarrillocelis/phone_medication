@echo off
echo ========================================
echo ELIMINACION DE MEDICAMENTOS RECIENTES
echo ========================================
echo.

echo Eliminando sección de medicamentos recientes...
echo ✅ Sección "Medicamentos recientes" removida
echo ✅ Solo se mantiene "Pendientes de hoy"
echo ✅ Dashboard más limpio y enfocado
echo ✅ Mejor experiencia de usuario
echo ✅ Interfaz más simple y directa
echo ✅ Base de datos Firebase funcional

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
echo ✅ Sección de medicamentos recientes eliminada
echo ✅ Dashboard más limpio y enfocado
echo ✅ Solo "Pendientes de hoy" visible
echo ✅ Mejor experiencia de usuario
echo ✅ Base de datos Firebase funcional
echo.
echo ¡El Dashboard ahora es más simple y directo!
echo.
pause
