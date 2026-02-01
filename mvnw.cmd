@echo off
REM Simple wrapper: calls mvn if available, otherwise prints install guidance.
where mvn >nul 2>&1
if %ERRORLEVEL%==0 (
  mvn %*
) else (
  echo Maven no encontrado en PATH.
  echo Instala Maven: choco install maven  (o descarga desde https://maven.apache.org/download.cgi)
  exit /b 1
)

