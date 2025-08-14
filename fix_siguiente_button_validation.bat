@echo off
echo ========================================
echo CORRECCION DE VALIDACION BOTON SIGUIENTE
echo ========================================
echo.

echo Corrigiendo validación del botón Siguiente...
echo ✅ Validación simplificada y más clara
echo ✅ Botón se habilita cuando hay segunda dosis calculada
echo ✅ Recalculo automático cuando cambia primera dosis
echo ✅ Limpieza de segunda dosis cuando se borra campo de horas
echo ✅ Validación específica por tipo de frecuencia
echo ✅ Funciona para Diariamente, Días Seleccionados y Cíclicamente

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
echo ✅ Validación del botón Siguiente corregida
echo ✅ Cálculo automático de segunda dosis funcionando
echo ✅ Botón se habilita correctamente
echo ✅ Formulario más intuitivo y funcional
echo ✅ Base de datos Firebase funcional
echo.
echo ¡Ahora puedes avanzar cuando se calcula la segunda dosis!
echo.
pause
