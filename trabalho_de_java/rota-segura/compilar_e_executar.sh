#!/bin/bash
# Script para compilar e executar o Sistema Rota Segura
set -e
cd "$(dirname "$0")"

echo ">>> Compilando..."
mkdir -p out
javac -encoding UTF-8 -d out -sourcepath src/main/java $(find src/main/java -name "*.java")
echo ">>> Compilação concluída."
echo ""
echo ">>> Iniciando aplicação..."
java -cp out com.rotasegura.main.Main
