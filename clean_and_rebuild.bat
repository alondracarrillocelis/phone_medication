@echo off
echo ========================================
echo    Limpieza y Reconstruccion del Proyecto
echo ========================================
echo.

echo [1/5] Limpiando cache de Gradle y Room...
if exist .gradle rmdir /s /q .gradle
if exist build rmdir /s /q build
if exist app\build rmdir /s /q app\build
if exist app\schemas rmdir /s /q app\schemas
if exist app\generated rmdir /s /q app\generated
echo ✓ Cache limpiado

echo.
echo [2/5] Limpiando proyecto con Gradle...
call gradlew clean
if %errorlevel% neq 0 (
    echo ✗ Error al limpiar con Gradle
    pause
    exit /b 1
)
echo ✓ Proyecto limpiado

echo.
echo [3/5] Reconstruyendo proyecto...
call gradlew build
if %errorlevel% neq 0 (
    echo ✗ Error al reconstruir proyecto
    echo.
    echo Intentando con --stacktrace...
    call gradlew build --stacktrace
    pause
    exit /b 1
)
echo ✓ Proyecto reconstruido exitosamente

echo.
echo [4/5] Verificando compilacion...
call gradlew assembleDebug
if %errorlevel% neq 0 (
    echo ✗ Error en la compilacion final
    pause
    exit /b 1
)
echo ✓ Compilacion exitosa

echo.
echo [5/5] Limpieza completada
echo.
echo ========================================
echo    ¡Proyecto listo para usar!
echo ========================================
echo.
echo Ahora puedes:
echo - Abrir Android Studio
echo - Ejecutar la aplicacion
echo - Los errores de KAPT deberian estar resueltos
echo.
pause 