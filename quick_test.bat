@echo off
echo ========================================
echo    Prueba Rápida con TestRepository
echo ========================================
echo.

echo [1/3] Limpiando cache...
if exist app\build rmdir /s /q app\build
echo ✓ Cache limpiado

echo.
echo [2/3] Compilando...
call gradlew assembleDebug --no-daemon
if %errorlevel% neq 0 (
    echo ✗ Error en la compilacion
    pause
    exit /b 1
)
echo ✓ Compilacion exitosa

echo.
echo [3/3] Instalando en dispositivo...
call gradlew installDebug --no-daemon
if %errorlevel% neq 0 (
    echo ✗ Error al instalar
    pause
    exit /b 1
)
echo ✓ Aplicacion instalada

echo.
echo ========================================
echo    ¡Prueba completada!
echo ========================================
echo.
echo Ahora ejecuta la aplicacion y revisa:
echo - Si se abre sin cerrarse
echo - Los logs en Android Studio (Logcat)
echo - Si aparece la pantalla de error
echo.
echo Si funciona, el problema estaba en SQLite
echo Si no funciona, el problema es otro
echo.
pause 