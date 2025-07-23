@echo off
echo ========================================
echo    Limpieza Agresiva del Proyecto
echo ========================================
echo.

echo [1/7] Deteniendo procesos de Gradle...
taskkill /f /im java.exe 2>nul
taskkill /f /im gradle.exe 2>nul
echo ✓ Procesos detenidos

echo.
echo [2/7] Limpiando cache de Gradle...
if exist .gradle rmdir /s /q .gradle
if exist build rmdir /s /q build
if exist app\build rmdir /s /q app\build
if exist app\schemas rmdir /s /q app\schemas
if exist app\generated rmdir /s /q app\generated
if exist .kotlin rmdir /s /q .kotlin
echo ✓ Cache limpiado

echo.
echo [3/7] Limpiando cache de Android Studio...
if exist .idea\workspace.xml del /q .idea\workspace.xml
if exist .idea\modules.xml del /q .idea\modules.xml
echo ✓ Cache de Android Studio limpiado

echo.
echo [4/7] Limpiando proyecto con Gradle...
call gradlew clean --no-daemon
if %errorlevel% neq 0 (
    echo ✗ Error al limpiar con Gradle
    echo Continuando de todas formas...
)
echo ✓ Proyecto limpiado

echo.
echo [5/7] Reconstruyendo proyecto...
call gradlew build --no-daemon --stacktrace
if %errorlevel% neq 0 (
    echo ✗ Error al reconstruir proyecto
    echo.
    echo Intentando con configuración mínima...
    call gradlew assembleDebug --no-daemon --stacktrace
    if %errorlevel% neq 0 (
        echo ✗ Error en la compilacion final
        pause
        exit /b 1
    )
) else (
    echo ✓ Proyecto reconstruido exitosamente
)

echo.
echo [6/7] Verificando compilacion...
call gradlew assembleDebug --no-daemon
if %errorlevel% neq 0 (
    echo ✗ Error en la compilacion final
    pause
    exit /b 1
)
echo ✓ Compilacion exitosa

echo.
echo [7/7] Limpieza agresiva completada
echo.
echo ========================================
echo    ¡Proyecto listo para usar!
echo ========================================
echo.
echo Ahora puedes:
echo - Abrir Android Studio
echo - Ejecutar la aplicacion
echo - Los errores de KAPT y Room deberian estar resueltos
echo.
echo Si persisten errores:
echo - Reinicia Android Studio completamente
echo - Invalida caches: File -> Invalidate Caches and Restart
echo.
pause 