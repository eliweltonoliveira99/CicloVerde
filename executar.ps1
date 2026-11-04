param([switch]$PrepararBanco)
$ErrorActionPreference = 'Stop'
Push-Location -LiteralPath $PSScriptRoot
try {
    $fontesCicloVerde = Get-ChildItem -LiteralPath 'src' -Filter '*.java' -Recurse | ForEach-Object { $_.FullName }
    & javac -encoding UTF-8 -cp 'lib/*' -d bin $fontesCicloVerde
    if ($LASTEXITCODE -ne 0) { throw 'Não foi possível compilar o projeto.' }
    if ($PrepararBanco) {
        & java -cp 'bin;lib/*' dao.PrepararBanco
        if ($LASTEXITCODE -ne 0) { throw 'Não foi possível preparar o banco.' }
    }
    & java -cp 'bin;lib/*' view.HomeUI
    if ($LASTEXITCODE -ne 0) { throw 'O CicloVerde foi encerrado com erro.' }
} finally { Pop-Location }
