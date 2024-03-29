# Minesweeper Server API

### Design considerations

* All domain classes were designed package-private thus avoiding unnecessary exposure outside API implementation.
* Information returned from game creation and cell revealing was reduced to the minimum lest cluttering response information with unimportant data.
* Flagging cells will change it flagging status considering its current one. I.E if it wasn't flagged previously then it will have Red Flag status onwards, but if it was Red flagged then it will have Question Mark status onwards. From this last status it will transition to a non-flagged one.
* While application is running, all game information is persisted in-memory. If a game ends, it's information is no longer kept.
* Game design caters for all detailed requirements, although some features aren't yet implemented.
* On requesting cell composition of a given game by public accessors, a cloned collection is returned in order to prevent internal game information from being changed outside of its internal implementation.
* Game state mutators methods (reveal and flag) were developed with concurrency in mind.
* A document-oriented database (MongoDB) was chosen for persistence since it makes easier storing/retrieving all game information at once, and a relational model would've been cumbersome to use to model cell composition.
* Spring Data was used to quickly develop a game repository.
* Quartz job scheduling framework was chosen in order to later configure a job which checks for expired games (maximum playing time exceeded).
* Project architecture is backed by Spring Boot framework.
* API documentation is exposed via Swagger

### Requirements coverage
* Persistence was configured with an embedded MongoDB but it's not being used.
* Time tracking is limited to played time computation on revealing and flagging, see Quartz job item in previous section.
* A game will end either by terminal user intervention (such as revealing a mine, winning, etc) or by maximum playing time exceeded (set as 999 seconds). This last feature is not available yet.
* Preserve/resume old games not done.
* Ability to select the game parameters: number of rows, columns, and mines. Although it's
lower priority accommodating that feature posed no considerable delay during development.
* Ability to support multiple users/accounts not done.

### Usage instructions
All endpoint information is conveniently exposed on (deployment url)/swagger-ui.html.
* To create a game make a POST to (deployment url)/games using params "columns", "rows" and "mines". You'll get a JSON response with all created game data detailing all composing cells status.
* With generated game id you can now make a PUT to (deployment url)/games/{gameId/cells/reveal using params "column" and "row" to identify cell to be revealed. You'll get a JSON response with resulting Game Status from move (WIN, LOST, STARTED if you can keep playing)
and adjacent cells that could be revealed (and current cell as well) with corresponding number of surrounding mines info. When game is LOST cell information pertains to all mine cells.
* With generated game id you can now make a PUT to (deployment url)/games/{gameId/cells/flag using params "column" and "row" to flag a cell. Flagging status changes considering current flag status as detailed in Design considerations section.