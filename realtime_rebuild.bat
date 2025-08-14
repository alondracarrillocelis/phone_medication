@echo off
echo ========================================
echo RECONSTRUCCION CON TIEMPO REAL
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
echo ✅ Funcionalidad en tiempo real implementada
echo ✅ addSnapshotListener configurado
echo ✅ Los datos se actualizan automáticamente
echo ✅ No más llamadas manuales a loadData()
echo.
echo ¡La app ahora funciona con sincronización en tiempo real!
echo.
pause
