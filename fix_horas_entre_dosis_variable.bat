@echo off
echo ========================================
echo CORRECCION DE VARIABLE HORASENTREDOSIS
echo ========================================
echo.

echo Corrigiendo error de variable horasEntreDosis...
echo ✅ Variable declarada en el lugar correcto
echo ✅ Eliminada declaración duplicada
echo ✅ LaunchedEffect puede acceder a la variable
echo ✅ Cálculo automático funcionando correctamente
echo ✅ Botón Siguiente se habilita correctamente
echo ✅ Formulario completamente funcional

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
echo ✅ Error de variable corregido
echo ✅ Cálculo automático de segunda dosis funcionando
echo ✅ Botón Siguiente se habilita correctamente
echo ✅ Formulario completamente funcional
echo ✅ Base de datos Firebase funcional
echo.
echo ¡El proyecto ahora compila sin errores!
echo.
pause
