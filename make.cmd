@echo off
setlocal

if "%~1"=="lint" (
  call gradlew.bat detekt
  exit /b %errorlevel%
)

echo Alvo suportado: lint
exit /b 1
