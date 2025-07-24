@echo off
echo ========================================
echo    Limpieza Simple para SQLite
echo ========================================
echo.

echo [1/4] Limpiando cache de Gradle...
if exist .gradle rmdir /s /q .gradle
if exist build rmdir /s /q build
if exist app\build rmdir /s /q app\build
echo ✓ Cache limpiado

echo.
echo [2/4] Limpiando proyecto...
call gradlew clean
if %errorlevel% neq 0 (
    echo ✗ Error al limpiar proyecto
    pause
    exit /b 1
)
echo ✓ Proyecto limpiado

echo.
echo [3/4] Reconstruyendo proyecto...
call gradlew build
if %errorlevel% neq 0 (
    echo ✗ Error al reconstruir proyecto
    pause
    exit /b 1
)
echo ✓ Proyecto reconstruido exitosamente

echo.
echo [4/4] Verificando compilacion...
call gradlew assembleDebug
if %errorlevel% neq 0 (
    echo ✗ Error en la compilacion final
    pause
    exit /b 1
)
echo ✓ Compilacion exitosa

echo.
echo ========================================
echo    ¡Proyecto listo para usar!
echo ========================================
echo.
echo Ahora puedes:
echo - Abrir Android Studio
echo - Ejecutar la aplicacion
echo - SQLite simple funcionando sin problemas
echo.
pause 