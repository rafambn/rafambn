# Árvores do cenário

- `arch-oak.svg`: tronco curvo e galhos abertos.
- `forked-tree.svg`: tronco bifurcado e copas em alturas diferentes.

Os dois desenhos têm geometria própria e seguem o estilo ilustrado do
[carvalho de referência](https://freesvg.org/vector-clip-art-of-wide-oak-tree).

Execute `bun assets/trees/draw-trees.mjs` para regenerar os arquivos e os símbolos
empacotados em `resources/trees/foreground.svg`. O servidor incorpora esses
símbolos no SVG final, sem buscar imagens externas.
