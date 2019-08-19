# Minesweeper Server API

### Desing considerations

* All domain classes were designed package-private thus avoiding unnecessary exposure outside API implementation. 
* A game will end either by terminal user intervention (such as revealing a mine, winning, etc) or by maximum playing time exceeded (set as 999 seconds).
* While application is running, all game information is persisted in-memory. If a game ends, it's information is no longer kept.
