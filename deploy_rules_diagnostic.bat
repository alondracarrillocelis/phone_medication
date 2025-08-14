@echo off
echo ========================================
echo DESPLEGANDO REGLAS DE FIRESTORE
echo ========================================
echo.

echo Desplegando reglas temporales para diagnóstico...
firebase deploy --only firestore:rules

echo.
echo ========================================
echo REGLAS DESPLEGADAS
echo ========================================
echo.
echo ✅ Reglas temporales desplegadas
echo ✅ Permisos abiertos para diagnóstico
echo.
echo Ahora ejecuta la app y revisa los logs.
echo.
pause
