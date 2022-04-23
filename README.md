# Trivia-Game
HQ trivia game spring boot project
# Requirements
- The application should run in completely automatic mode - i.e no admin intervention should be required to start the game (once a minimum number of players have joined) or advance the game to the next round.
- The application should allow running multiple games simultaneously.
- The application should allow a reasonably large number of players to participate in each game (think hundreds).
- The application should display statistics about player choices at the end of each round (how many players have chosen each answer).
# Up & Running
### Maven
```bash
$ git clone https://github.com/Yonghua-You/Trivia-Game.git
$ cd Trivia-Game
$ mvn spring-boot:run -Dspring-boot.run.profiles=h2
```
The Swagger UI is available at `http://localhost:8080/swagger-ui/index.html`.

### Executable JAR
```bash
$ git clone https://github.com/Yonghua-You/Trivia-Game.git
$ cd Trivia-Game
$ mvn package -DskipTests
$ java -jar -Dspring.profiles.active=h2 target/Trivia-Game-<version>.jar
```
The Swagger UI is available at `http://localhost:8080/swagger-ui/index.html`.

## Tests
### Maven
* Run only unit tests:
```bash
$ mvn clean test
```
* Run only integration tests:
```bash
$ mvn clean failsafe:integration-test
```
* Run unit and integration tests:
```bash
$ mvn clean verify

