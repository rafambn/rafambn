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
GET /
GET /github/profile.svg?layout=mobile
GET /badge/{owner}/{repository}.svg
GET /preview/launch-base.svg
GET /preview/launch-base.svg?layout=mobile
```

Exemplos:

```sh
curl -i http://localhost:3000/github/profile.svg
curl -i 'http://localhost:3000/github/profile.svg?layout=mobile'
curl -i http://localhost:3000/badge/rafambn/KMaP.svg
```

O badge retorna `404` para outro usuário ou para um repositório que não esteja em `pinnedRepos`.

A página inicial mostra o cenário de lançamento em desenvolvimento, inteiramente em SVG.
A composição web mede 1600 × 670, com fundo transparente, plataforma de concreto,
escadas amarelas, canteiro e três árvores opacas de cada lado. As menores ficam
em primeiro plano. As luzes piscam em ciclos de 1,6 segundo e respeitam
`prefers-reduced-motion`. A adaptação mobile está pausada; o parâmetro
`?layout=mobile` mantém apenas uma prévia de largura reduzida.

A página inicial e a prévia não incrementam contadores. `/github/profile.svg`
mostra o mesmo cenário e mantém a contagem de visitas do perfil.
Os dados de repositórios, nome e bio ainda não fazem parte desse cenário.

As árvores são geradas por `bun assets/trees/draw-trees.mjs`, que atualiza
os SVGs individuais e `resources/trees/foreground.svg`. A referência visual
preservada está em `output/imagegen/rocket-layouts/01-amanhecer.png`.

Todas as badges mostram apenas `views` e o contador, em fonte monoespaçada, com altura
de 32 px. O contador começa sempre em x=90, alinhado à esquerda, com 8 px por caractere
e 14 px de padding após o número. A largura acompanha o contador, sem largura mínima.
Cada biblioteca tem uma paleta e um padrão de fundo
temático: mapa, linha do tempo, compressão, chave, linhas de escrita ou conexões de rede.
O KMaP usa um mapa de praia, áreas verdes, ruas e rio desenhado inteiramente em SVG,
sem imagens rasterizadas. Cores claras e vias com larguras distintas dão o aspecto cartográfico;
o texto escuro tem contorno branco para manter a leitura sobre o mapa.
O FrameBar usa um fundo grafite liso em SVG, com marcações discretas na base e cursor
de reprodução dourado. O texto claro fica sobre uma área livre de detalhes.
O KFlate usa faixas curvas que passam de espaçadas a compactadas na base da badge,
sobre fundo azul-escuro `#0c1b2b`, com os tons verde e ciano do logo.
O KeyManager usa um cadeado deitado em SVG: a haste prateada contorna `views` e o corpo
azul fica atrás do contador. O azul `#0061a4` vem do ícone do aplicativo.
O Scribe usa papel claro, um traço de tinta e uma pena vetorial em verde-petróleo
`#0d9488` e ocre `#a16207`, as cores do logo.
O wg-kotlin usa linhas de rede amarelas `#facc15` conectadas ao símbolo da biblioteca, com o laranja
`#fb923c` e o grafite `#2d3748` do logo publicado em rafambn.com, inteiramente em SVG.
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

## Produção

Publique a porta atrás de um proxy HTTPS. As respostas SVG usam `Cache-Control: no-store`, mas
proxies externos e o cache de imagens do GitHub ainda podem reduzir a quantidade de requisições que
chegam ao contador.

O servidor não inclui autenticação nem rate limiting. Configure essas proteções no proxy quando
necessário.
