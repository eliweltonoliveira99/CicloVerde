# CicloVerde

O CicloVerde é um projeto acadêmico feito para a disciplina UPX2, na Facens. A ideia é ajudar pequenos comércios a organizar alimentos disponíveis para doação. O sistema foi desenvolvido como uma aplicação desktop em Java Swing, com dados armazenados em MySQL.

## O que o sistema faz

- Mostra as empresas cadastradas e permite consultar o perfil e os alimentos de cada uma.
- Tem uma área de demonstração para cadastrar empresas e alimentos e acompanhar os lotes.
- Vincula cada alimento à empresa selecionada no cadastro.
- Permite reservar um lote inteiro e registra a reserva no banco.
- Valida os campos, a quantidade e a data de validade antes de salvar.
- Mostra uma mensagem de erro quando não consegue acessar o banco de dados.

A área administrativa não tem autenticação nesta versão. Também ainda não há cadastro de ONGs, identificação de quem fez a reserva, confirmação da retirada nem notificações. Por isso, uma reserva no sistema não significa que o alimento foi coletado.

## O que é necessário

- JDK 17 ou mais recente.
- MySQL em execução.
- Driver JDBC `lib/mysql-connector-java.jar`, incluído no repositório.
- Banco `cicloverde`. Para criar uma instalação nova, use o script SQL do projeto.

## Configurar o banco

Na primeira instalação, abra `sql/banco_de_dados.sql` no MySQL Workbench e execute o script para criar o banco e as tabelas.

Copie `db.properties.example` para `db.properties` e preencha os dados locais de acesso. Esse arquivo contém credenciais e não deve ser enviado ao repositório.

```properties
db.url=jdbc:mysql://localhost:3306/cicloverde
db.user=root
db.password=sua_senha_aqui
```

Também é possível configurar `DB_URL`, `DB_USER` e `DB_PASS` como variáveis de ambiente. Quando `db.properties` existe, os valores dele têm prioridade.

## Compilar e executar no PowerShell

```powershell
Copy-Item db.properties.example db.properties
```

Depois de preencher o arquivo com seu usuário e sua senha do MySQL, execute:

```powershell
./executar.ps1 -PrepararBanco
```

Nas próximas vezes, use `./executar.ps1`. Para compilar e executar manualmente:

```powershell
$fontesCicloVerde = Get-ChildItem -LiteralPath src -Filter '*.java' -Recurse | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -cp 'lib/*' -d bin $fontesCicloVerde
java -cp 'bin;lib/*' dao.PrepararBanco
java -cp 'bin;lib/*' view.HomeUI
```

O comando `PrepararBanco` atualiza a estrutura necessária e mantém as empresas e os alimentos que já estavam cadastrados. Também dá para usar `./executar.ps1 -PrepararBanco` na primeira execução e, depois, `./executar.ps1` para compilar e abrir o programa.

## Usar o sistema

1. Na tela inicial, abra a área do administrador para cadastrar empresas e alimentos.
2. Selecione uma empresa para ver os lotes dela ou escolha **Ver todas as empresas** para voltar à lista completa.
3. Para incluir dados, use **Cadastrar empresa** ou **Cadastrar alimento**.
4. Na consulta pública, abra o perfil de uma empresa para ver os alimentos disponíveis e reservar um lote.
5. Depois da confirmação, o lote deixa de aparecer como disponível. A reserva pode ser consultada na área administrativa ou no histórico do perfil.

## Organização do código

- `src/model`: dados das empresas e dos alimentos e validações.
- `src/dao`: conexão com o banco, consultas e gravações.
- `src/view`: telas do programa e interação com o usuário.
- `sql`: script para criar a estrutura do banco em uma instalação nova.
- `lib`: driver necessário para acessar o MySQL.
- `db.properties.example`: modelo da configuração local.
- `executar.ps1`: compila e abre o programa.

A pasta `bin/` é gerada na compilação e não precisa ser enviada ao Git. O arquivo `db.properties` fica apenas no computador de quem vai executar o projeto.
