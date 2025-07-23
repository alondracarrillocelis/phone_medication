# Script para configurar Firebase y aplicar índices
# Ejecutar este script en PowerShell como administrador

Write-Host "=== Configuración de Firebase para App Medicamentos ===" -ForegroundColor Green
Write-Host ""

# Verificar si Node.js está instalado
Write-Host "Verificando Node.js..." -ForegroundColor Yellow
try {
    $nodeVersion = node --version
    Write-Host "Node.js encontrado: $nodeVersion" -ForegroundColor Green
} catch {
    Write-Host "ERROR: Node.js no está instalado." -ForegroundColor Red
    Write-Host "Por favor, instala Node.js desde: https://nodejs.org/" -ForegroundColor Yellow
    Write-Host "Después de instalar Node.js, ejecuta este script nuevamente." -ForegroundColor Yellow
    exit 1
}

# Verificar si Firebase CLI está instalado
Write-Host "Verificando Firebase CLI..." -ForegroundColor Yellow
try {
    $firebaseVersion = firebase --version
    Write-Host "Firebase CLI encontrado: $firebaseVersion" -ForegroundColor Green
} catch {
    Write-Host "Instalando Firebase CLI..." -ForegroundColor Yellow
    npm install -g firebase-tools
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: No se pudo instalar Firebase CLI." -ForegroundColor Red
        exit 1
    }
}

# Iniciar sesión en Firebase
Write-Host "Iniciando sesión en Firebase..." -ForegroundColor Yellow
firebase login
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: No se pudo iniciar sesión en Firebase." -ForegroundColor Red
    exit 1
}

# Inicializar Firebase en el proyecto
Write-Host "Inicializando Firebase en el proyecto..." -ForegroundColor Yellow
firebase init firestore --project appmedicamentos-c9a8d --yes
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: No se pudo inicializar Firebase." -ForegroundColor Red
    exit 1
}

# Aplicar índices
Write-Host "Aplicando índices de Firestore..." -ForegroundColor Yellow
firebase deploy --only firestore:indexes --project appmedicamentos-c9a8d
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: No se pudieron aplicar los índices." -ForegroundColor Red
    exit 1
}

# Aplicar reglas
Write-Host "Aplicando reglas de Firestore..." -ForegroundColor Yellow
firebase deploy --only firestore:rules --project appmedicamentos-c9a8d
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: No se pudieron aplicar las reglas." -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "=== Configuración completada exitosamente ===" -ForegroundColor Green
Write-Host "Los índices pueden tardar varios minutos en construirse." -ForegroundColor Yellow
Write-Host "Puedes verificar el estado en: https://console.firebase.google.com/project/appmedicamentos-c9a8d/firestore/indexes" -ForegroundColor Cyan
Write-Host ""
Write-Host "Después de que los índices estén listos, reinicia la aplicación en Android Studio." -ForegroundColor Yellow 