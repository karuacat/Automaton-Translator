# -----------------------------
# Automata Translator - Build & Release
# Compile all .java files, create a versioned JAR, and place it in release/
# -----------------------------

# 1️⃣ Demander la version
$version = Read-Host "Enter version (ex: v1.0)"
if (-not $version) {
    Write-Host "No version entered. Using v1.0"
    $version = "v1.0"
}

# 2️⃣ Vérifie ou crée le dossier bin
if (!(Test-Path bin)) {
    Write-Host "Creating bin directory..."
    mkdir bin
}

# 3️⃣ Compile tous les fichiers Java
Write-Host "Compiling Java files..."
Get-ChildItem -Recurse -Filter *.java | ForEach-Object { 
    javac -d bin $_.FullName
}

# 4️⃣ Vérifie ou crée le dossier release
if (!(Test-Path release)) {
    Write-Host "Creating release directory..."
    mkdir release
}

# 5️⃣ Crée le JAR exécutable versionné
$jarName = "AutomataTranslator-$version.jar"
$mainClass = "Main"

Write-Host "Creating JAR $jarName..."
jar cfe "release\$jarName" $mainClass -C bin .

# 6️⃣ Test du JAR
Write-Host "Testing the JAR..."
java -jar "release\$jarName"

Write-Host "✅ Build and release complete. JAR is in release\$jarName"
