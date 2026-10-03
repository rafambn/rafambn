# Servidor do portfólio

Portfólio de Rafael Mendonça servido por Kotlin/Ktor com JDK 21. O porto é SVG
vetorial, sem imagens rasterizadas, JavaScript de interface ou chamadas de API
pelo navegador. A página e as imagens usam os mesmos desenhos e dados.

## Executar e empacotar

```sh
cp .env.example .env
set -a
. ./.env
set +a
./kotlin run
```

O endereço padrão é `http://localhost:3000`. Para desenvolver sem registrar
visitas, abra `http://localhost:3000/preview`. Os SVGs como imagens estão em
`http://localhost:3000/preview/images`.

```sh
./kotlin build
./kotlin test
./kotlin package
java -jar build/tasks/_rafambn_executableJarJvm/rafambn-jvm-executable.jar
```

O wrapper incluído resolve o toolchain. O pacote executável inclui dependências,
configuração e fonte. A execução do JAR precisa apenas de JDK 21 e das variáveis
de ambiente abaixo. Não precisa de Bun, Node ou navegador no servidor.

## Configuração

| Variável | Padrão |
| --- | --- |
| `DATABASE_PATH` | `data/profile.mv.db` |
| `LOG_FILE_PATH` | `data/profile.log` |
| `PROFILE_BANNER_HOST` | `0.0.0.0` |
| `PROFILE_BANNER_PORT` | `3000` |

A lista de projetos fica em `src/PinnedRepos.kt`. O Ktor lê
`resources/application.yaml`. O log é JSON no stdout e no arquivo configurado.
O endereço usado no README é `https://profile.rafambn.com`.

## Rotas e registro de acessos

| Rota GET | Conteúdo | Incremento |
| --- | --- | --- |
| `/` | Site responsivo com SVG incorporado | Uma visita do perfil |
| `/preview` | Mesmo site para revisão | Nenhum |
| `/api/stats` | JSON de contadores e estrelas | Nenhum |
| `/preview/harbor.svg` | Porto completo | Nenhum |
| `/preview/images` | Composição das imagens independentes | Nenhum |
| `/github/profile.svg` | Porto completo; compatibilidade com a URL anterior | Uma visita do perfil |
| `/github/header.svg` | Nome, frase e contadores do perfil | Uma visita do perfil |
| `/github/projects/{repository}.svg` | Faixa do porto referente ao projeto | Uma visualização desse repositório |
| `/github/footer.svg` | Rodapé | Nenhum |
| `/preview/header.svg` | Cabeçalho para revisão | Nenhum |
| `/preview/projects/{repository}.svg` | Faixa do projeto para revisão | Nenhum |
| `/preview/footer.svg` | Rodapé para revisão | Nenhum |
| `/badge/{owner}/{repository}.svg` | Badge compacta de views | Uma visualização desse repositório |

Os nomes dos repositórios e o proprietário das badges aceitam maiúsculas ou
minúsculas. Apenas `rafambn` e os seis projetos fixados são permitidos. Um projeto
desconhecido retorna `404`; um layout inválido retorna `400`. Essas respostas não
registram acessos. As respostas válidas incluem
`Cache-Control: no-store, max-age=0, must-revalidate` e
`X-Content-Type-Options: nosniff`.

O JSON mantém `owner`, `profile` e a lista `repositories`. Cada repositório tem
`name`, `stars` e `views`. Tanto `profile` quanto `views` incluem `today`, `week`,
`month` e `total`. Uma estrela desconhecida é `null` no JSON e `—` no desenho;
zero é exibido como zero.

### O que os contadores medem

As contagens medem requisições recebidas pela aplicação, não pessoas únicas,
cliques nos links ou métricas privadas do GitHub. Robôs e atualizações repetidas
podem entrar no total. O proxy de imagens do GitHub e outros caches podem reduzir
o número de requisições que chegam ao servidor, apesar dos cabeçalhos de cache.

Uma abertura do site incrementa o perfil uma vez, mesmo que o HTML contenha as
duas versões responsivas do SVG. No README, somente o cabeçalho incrementa o
perfil. As seis faixas de projetos incrementam apenas seus respectivos
repositórios. O rodapé não incrementa nada. Assim, carregar oito recortes não
conta oito visitas do perfil. Não combine o SVG completo com o cabeçalho na mesma
composição: ambos são entradas públicas independentes que registram o perfil.

As views de repositórios acumulam requisições às faixas públicas e às badges
compatíveis. O site lê esses totais; clicar em um projeto abre o GitHub diretamente
e não passa por um contador de cliques. Pré-visualizações e consultas de
estatísticas não alteram nenhum valor. Imagens públicas mostram a contagem já
incluindo a própria requisição.

Os períodos são dia atual em UTC, últimos sete dias incluindo hoje, últimos
trinta dias incluindo hoje, e total histórico. Números a partir de 10 mil usam
`k`, `M` e unidades maiores para caber no desenho. As descrições acessíveis do
SVG e o JSON preservam os valores exatos.

## Layouts e recortes SVG

| Layout | Dimensões do porto completo | Grade de projetos |
| --- | --- | --- |
| `desktop` | 1200 × 1064 | Três linhas, duas colunas |
| `mobile` | 360 × 1384 | Seis linhas, uma coluna |
| `readme` | 840 × 1740 | Seis linhas, uma coluna |

O site muda para `mobile` em larguras de até 900 CSS px. Em 320 px, os nomes dos
projetos ficam com aproximadamente 26 px e os números com 21 px. O navio permanece
à esquerda. A página permite rolagem vertical sem rolagem horizontal. Contêineres
inteiros são links, na ordem de leitura, com indicação de hover e foco de teclado.
O movimento discreto da água respeita `prefers-reduced-motion`.

Os SVGs completos aceitam `?layout=desktop`, `mobile` ou `readme`, com `desktop`
como padrão. Cabeçalho, faixas e rodapé aceitam apenas `readme` ou `mobile`, com
`readme` como padrão. Todos os recortes de uma composição devem usar o mesmo
layout. O parâmetro não muda quais contadores são registrados.

`HarborLayout.kt` define dimensões e a fronteira entre mar e cais. Cada recorte
usa o `viewBox` correspondente às coordenadas do porto completo. O navio, os
padrões e as linhas do cais conservam a mesma origem, inclusive quando atravessam
uma fronteira entre imagens. Não há um navio diferente em cada faixa. Os SVGs
completos ficam abaixo de 128 KiB, limite verificado nos testes.

`HarborDrawing.kt` compõe o cais e os contêineres; `HarborShip.kt` desenha o
navio. `HarborDetails.kt` reúne padrões, símbolos e fontes. `HarborSvg.kt`
compõe cabeçalho, porto e rodapé. `HarborPage.kt` monta o site e a prévia de
imagens. Barlow Condensed SemiBold é incorporada como WOFF2 em cada SVG.
O cabeçalho também incorpora Source Serif 4 Bold para o nome e Barlow Regular
para a frase. As imagens funcionam isoladamente, sem carregar fontes externas.
Os arquivos e as licenças OFL ficam em `resources/fonts/`.

O desenho segue o esboço original fornecido: água azul com curvas que se repetem
sem cortes, casco vermelho e convés verde, cabine à frente, telhados marfim,
guindastes laranja, faixas amarelas, ferrovia e rua. Os detalhes usam paths,
gradientes de material e sombras SVG. No mobile, a faixa de rua e ferrovia fica
estreita e os armazéns e árvores laterais são omitidos para preservar a leitura.

## README do GitHub

O README usa oito imagens: cabeçalho, seis projetos e rodapé. Cada imagem de
projeto é envolvida em seu próprio `<a>`. Os links não dependem de âncoras dentro
do SVG, que não são interativas quando ele é exibido como imagem.

O GitHub remove estilos inline, classes e scripts durante a sanitização do
Markdown. Portanto, uma grade CSS que troca duas colunas por uma não pode ser
implementada no README. Uma tabela manteria suas colunas em telas estreitas e
introduziria bordas/espaçamento do GitHub. A solução escolhida é uma coluna de
seis projetos também no desktop do README; o site mantém exatamente as duas
grades solicitadas. [Pipeline oficial de Markdown](https://github.com/github/markup).

Cada `<picture>` troca as proporções do porto com
`<source media="(max-width: 760px)">`. Isso adapta as imagens sem tentar reorganizar
elementos externos ao SVG. Os `<div>` e `align="top"` evitam o espaço da linha de
base entre as faixas. `width="100%"` mantém a largura comum. O suporte a
`<picture>` é descrito na [documentação do GitHub](https://docs.github.com/en/get-started/writing-on-github/getting-started-with-writing-and-formatting-on-github/quickstart-for-writing-on-github).

O markup foi passado pelo endpoint `POST /markdown` do GitHub: os oito links,
`picture`, fontes mobile, larguras e alinhamentos foram preservados. A saída
sanitizada também foi inspecionada no navegador com os estilos do GitHub, em
desktop e 320 px, usando URLs locais de prévia. Todos os oito recortes encostam
sem gaps de layout; a rasterização em escalas fracionárias pode deixar uma linha
de antialiasing de um pixel nas junções. Essa revisão não publica um README ou
altera o perfil remoto.

## Persistência e estrelas

O `ViewStore` preserva os mapas H2 MVStore `view_totals` e `view_daily`, com as
chaves `profile:rafambn` e `repo:rafambn/<nome-em-minúsculas>`. A reconstrução visual
não exige migração nem recriação do banco. O fechamento normal confirma as
alterações pendentes; o auto-commit ocorre a cada cinco segundos.

O `GitHubStars` consulta os seis repositórios em uma tarefa da aplicação. O cache
tem validade de quinze minutos e fica em memória. Falhas HTTP ou JSON inválido
mantêm os valores conhecidos. Respostas `403` e `429` interrompem a rodada e
adiam novas consultas de acordo com `Retry-After`, `X-RateLimit-Reset` e backoff.
Ao reiniciar o servidor, as estrelas ficam desconhecidas até a primeira resposta
válida. Nenhuma consulta de página ou SVG dispara uma chamada extra ao GitHub.

## Publicar no Termux

1. Execute build, testes e empacotamento localmente.
2. Envie o JAR para um nome temporário no servidor e confira seu checksum.
3. Pare o serviço `profile-server` com `sv -w 30 down "$PREFIX/var/service/profile-server"`.
4. Faça backup do JAR anterior e do arquivo definido por `DATABASE_PATH`.
5. Substitua apenas o JAR. Preserve `.env`, o banco de visitas e o script do serviço.
6. Inicie com `sv -w 30 up "$PREFIX/var/service/profile-server"`.
7. Confira `/preview`, `/preview/images` e `/api/stats` localmente e pelo endereço público.

O diretório atualmente utilizado é `~/apps/profile-server` e o serviço executa
`java -jar profile-server.jar`. O túnel Cloudflare será configurado pelo proprietário.
Em uma instalação nova, configure o diretório de trabalho, as variáveis de
ambiente e JDK 21 no serviço antes de iniciá-lo. Cada processo precisa de seu
próprio arquivo de banco; nunca abra o mesmo MVStore em duas instâncias.

Publique o servidor com as novas rotas antes de atualizar o README remoto. Um
`git push` isolado não substitui o JAR que já está rodando no Termux. Se o deploy
falhar, pare o serviço, restaure o JAR anterior e inicie novamente. O banco não
precisa ser revertido para voltar à implementação anterior.
