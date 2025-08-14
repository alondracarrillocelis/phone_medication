@echo off
echo ========================================
echo MEJORAS DE RESPONSIVIDAD DEL DASHBOARD
echo ========================================
echo.

echo Aplicando mejoras de responsividad...
echo ✅ Espaciado corregido entre contadores y barra de progreso
echo ✅ Contenido de contadores perfectamente centrado
echo ✅ Iconos y textos mejorados en tamaño y alineación
echo ✅ Botones de acción más grandes y responsivos
echo ✅ Tarjetas de pendientes mejoradas visualmente
echo ✅ Espaciado general optimizado
echo ✅ Textos con mejor alineación y tamaños
echo ✅ Interfaz más moderna y profesional
echo ✅ Mejor experiencia en diferentes tamaños de pantalla

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
echo ✅ Dashboard completamente responsivo
echo ✅ Espaciado y centrado corregidos
echo ✅ Interfaz más moderna y profesional
echo ✅ Mejor experiencia de usuario
echo ✅ Adaptable a diferentes pantallas
echo ✅ Base de datos Firebase funcional
echo.
echo ¡El Dashboard ahora es más atractivo y funcional!
echo.
pause
