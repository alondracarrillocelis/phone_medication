@echo off
echo Limpiando proyecto Android...
echo.

echo Eliminando directorios de build...
if exist "app\build" rmdir /s /q "app\build"
if exist "build" rmdir /s /q "build"
if exist ".gradle" rmdir /s /q ".gradle"

echo Limpiando cache de Gradle...
gradlew clean

echo Limpiando cache de Android Studio...
if exist "%USERPROFILE%\.android\build-cache" rmdir /s /q "%USERPROFILE%\.android\build-cache"
if exist "%USERPROFILE%\.gradle\caches" rmdir /s /q "%USERPROFILE%\.gradle\caches"

echo.
echo Limpieza completada. Ahora puedes intentar compilar nuevamente.
echo.
pause