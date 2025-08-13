@echo off
echo ========================================
echo Actualizacion de Configuracion Firebase
echo ========================================

echo.
echo INSTRUCCIONES:
echo 1. Ve a Firebase Console: https://console.firebase.google.com/
echo 2. Selecciona tu proyecto (o crea uno nuevo)
echo 3. Ve a Project Settings (icono de engranaje)
echo 4. En la pestaña "General", busca "Your apps"
echo 5. Si no hay una app Android, haz clic en "Add app" y selecciona Android
echo 6. Usa el package name: com.example.phone_medicatios
echo 7. Descarga el archivo google-services.json
echo 8. Coloca el archivo en la carpeta app/ de este proyecto
echo.

echo ¿Ya descargaste el archivo google-services.json? (S/N)
set /p respuesta=

if /i "%respuesta%"=="S" (
    echo.
    echo Verificando archivo google-services.json...
    
    if exist "app\google-services.json" (
        echo ✅ Archivo google-services.json encontrado
        echo.
        echo Limpiando y recompilando proyecto...
        call clean_and_rebuild.bat
    ) else (
        echo ❌ Archivo google-services.json NO encontrado en app\
        echo Por favor coloca el archivo en la carpeta app\ y ejecuta este script nuevamente
    )
) else (
    echo.
    echo Por favor sigue las instrucciones y ejecuta este script nuevamente
)

echo.
pause

