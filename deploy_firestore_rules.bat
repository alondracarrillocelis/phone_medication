@echo off
echo Desplegando reglas de Firestore...

REM Verificar si Firebase CLI está instalado
firebase --version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Firebase CLI no está instalado.
    echo Por favor instala Firebase CLI con: npm install -g firebase-tools
    pause
    exit /b 1
)

REM Inicializar Firebase si no está inicializado
if not exist "firebase.json" (
    echo Inicializando Firebase...
    firebase init firestore --project=medications-reminder1
)

REM Desplegar reglas
echo Desplegando reglas de Firestore...
firebase deploy --only firestore:rules --project=medications-reminder1

if %errorlevel% equ 0 (
    echo Reglas de Firestore desplegadas exitosamente!
) else (
    echo Error al desplegar las reglas de Firestore.
)

pause
