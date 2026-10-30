@echo off
cd /d "%~dp0"
echo Compiling Hospital Management System...
javac -cp "lib/*" -sourcepath src -d bin src\hospital\*.java src\hospital\model\*.java src\hospital\repository\*.java src\hospital\services\*.java src\hospital\ui\*.java src\hospital\utils\*.java
if %ERRORLEVEL% equ 0 (
    echo Starting application...
    start javaw -cp "bin;lib/*" hospital.App
) else (
    echo Compilation failed.
    pause
)
