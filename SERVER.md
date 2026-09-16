# Operação do servidor

Este projeto expõe um banner SVG para o perfil do GitHub e badges de visualizações para os
repositórios fixados.

## Rodar

Requisitos: JDK 21 ou superior. O wrapper do Kotlin Toolchain já está no repositório.

```sh
cp .env.example .env
set -a
. ./.env
set +a
./kotlin run
```

O servidor usa `http://localhost:3000` por padrão. Para validar ou empacotar:

```sh
./kotlin test
./kotlin build
./kotlin package
```

O JAR executável fica em `build/`.

## Rotas

```text
GET /github/profile.svg
GET /github/profile.svg?layout=mobile
GET /badge/{owner}/{repository}.svg
```

Exemplos:

```sh
curl -i http://localhost:3000/github/profile.svg
curl -i 'http://localhost:3000/github/profile.svg?layout=mobile'
curl -i http://localhost:3000/badge/rafambn/KMaP.svg
```

O badge retorna `404` para outro usuário ou para um repositório que não esteja em `pinnedRepos`.

Todas as badges mostram apenas `views` e o contador, em fonte monoespaçada, com altura
de 32 px. O contador começa sempre em x=90, alinhado à esquerda, com 8 px por caractere
e 14 px de padding após o número. A largura acompanha o contador, sem largura mínima.
Cada biblioteca tem uma paleta e um padrão de fundo
temático: mapa, janela, compressão, chave, linhas de escrita ou conexões de rede.
Personalize `background` e `textColor` com seis dígitos hexadecimais, sem `#`.
Cores inválidas usam o padrão. Ao personalizar, escolha cores com bom contraste.

```text
/badge/rafambn/KMaP.svg
/badge/rafambn/FrameBar.svg
/badge/rafambn/KFlate.svg
/badge/rafambn/KeyManager.svg
/badge/rafambn/Scribe.svg
/badge/rafambn/wg-kotlin.svg
/badge/rafambn/KMaP.svg?background=111827&textColor=ffffff
```

## Configuração

- `DATABASE_PATH` usa `data/profile.mv.db` por padrão.
- `LOG_FILE_PATH` usa `data/profile.log` por padrão.
- `PROFILE_BANNER_HOST` e `PROFILE_BANNER_PORT` usam `0.0.0.0:3000` por padrão.

Os repositórios permitidos ficam em `src/PinnedRepos.kt`. O `AppScribe` escreve cada evento JSON no
stdout e no arquivo configurado.

## Persistência

O `ViewStore` mantém dois mapas H2 MVStore, um para totais e outro para contagens diárias. O
auto-commit do H2 grava alterações a cada cinco segundos. `close()` confirma alterações pendentes
durante o encerramento normal.

O contador mede requisições. Robôs, proxies e acessos repetidos entram no total. Para backup, pare o
processo e copie o arquivo indicado por `DATABASE_PATH`. Não execute duas instâncias usando o mesmo
arquivo.

## GitHub

O perfil fica em cache por 15 minutos. A API REST pública fornece descrição, linguagem e estrelas
de cada repositório. Se uma chamada falhar, o banner usa os dados padrão daquele repositório.

## Produção

Publique a porta atrás de um proxy HTTPS. As respostas SVG usam `Cache-Control: no-store`, mas
proxies externos e o cache de imagens do GitHub ainda podem reduzir a quantidade de requisições que
chegam ao contador.

O servidor não inclui autenticação nem rate limiting. Configure essas proteções no proxy quando
necessário.
