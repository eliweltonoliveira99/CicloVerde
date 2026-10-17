# 🌱 CicloVerde - Sistema de Gestão de Alimentos Excedentes

O **CicloVerde** é uma aplicação desktop desenvolvida em Java (Swing) para conectar comércios locais a pontos de doação e instituições, facilitando o gerenciamento e a destinação correta de alimentos excedentes para combate ao desperdício.

---

## 🚀 Tecnologias Utilizadas

- **Java JDK 17+** (Compatível com versões recentes do Java)
- **Java Swing** para interface gráfica
- **MySQL Database**
- **LGoodDatePicker** (seletor de datas amigável)
- **MySQL Connector/J**

---

## 🗄️ Configuração do Banco de Dados

1. Certifique-se de que o **MySQL** esteja em execução.
2. Execute o script contido em `sql/banco_de_dados.sql` no seu MySQL Workbench ou terminal:
   ```sql
   CREATE DATABASE IF NOT EXISTS cicloverde;
   USE cicloverde;
   -- (demais tabelas do script)
   ```

---

## 🔐 Configuração de Credenciais

Para manter a segurança do projeto, as senhas do banco **não são versionadas no Git**.

1. Localize o arquivo `db.properties.example`.
2. Crie uma cópia chamada `db.properties`:
   - Copie `db.properties.example` para `db.properties`
3. Ajuste o usuário e a senha conforme o seu ambiente local:
   ```properties
   db.url=jdbc:mysql://localhost:3306/cicloverde
   db.user=root
   db.password=sua_senha_aqui
   ```

> **Nota:** Também é possível configurar via variáveis de ambiente (`DB_URL`, `DB_USER`, `DB_PASS`).

---

## ▶️ Como Executar

A classe principal da aplicação é:
`view.HomeUI`

### Via Linha de Comando (PowerShell / Terminal):
```bash
# Compilar:
javac -cp "lib/*;src" -d bin src/dao/*.java src/model/*.java src/view/*.java

# Executar:
java -cp "bin;lib/*" view.HomeUI
```

### Via Eclipse / IntelliJ / VS Code:
1. Abra a pasta `CicloVerde` como projeto Java.
2. Adicione os JARs da pasta `lib/` no **Build Path** / **Project Libraries**.
3. Execute o arquivo `src/view/HomeUI.java`.
