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

### Swagger UI
The API can be tested via the Swagger UI:

![Swagger UI Main View](/readme/swagger-main-view.png)

For example, to add a new game, expand the `POST` operation. Then click on the `Try it out`, add the payload below to the `Request Body` text area, and click on the `Execute`:
```json
{
  "canceled": false,
  "endDateTime": "2022-04-26T15:30:08.455Z",
  "guid": "3fa85f64-5717-4562-b3fc-2c963f69afa6",
  "id": 1,
  "questions": [
    {
      "answers": [
        {
          "createdDateTime": "2022-04-23T15:30:08.455Z",
          "id": 1,
          "order": 1,
          "playerCount": 0,
          "text": "answer 1"
        }
      ],
      "createdDateTime": "2022-04-23T15:30:08.455Z",
      "id": 1,
      "isValid": true,
      "order": 1,
      "text": "question 1"
    },
{
      "answers": [
        {
          "createdDateTime": "2022-04-23T15:30:08.455Z",
          "id": 2,
          "order": 1,
          "playerCount": 0,
          "text": "answer 2"
        }
      ],
      "createdDateTime": "2022-04-23T15:30:08.455Z",
      "id": 2,
      "isValid": true,
      "order": 2,
      "text": "question 2"
    }
  ],
  "startDateTime": "2022-04-25T15:30:08.455Z",
  "version": 0
}
```
![Swagger post create game](/readme/swagger_post_create_game.png)

If the operation is successful, you will get the following response:

![Swagger post create game](/readme/swagger_post_create_game_response.png)

### H2 Console
When running with the `h2` profile, the H2 console is available at `http://trivia-game-app-heroku.herokuapp.com/h2-console/login.jsp`.
JDBC URL is the same one from (spring.datasource.url) the spring boot appliction.properties file
Fill the login form as follows and click on Connect:
* Saved Settings: **Generic H2 (Embedded)**
* Setting Name: **Generic H2 (Embedded)**
* Driver class: **org.h2.Driver**
* JDBC URL: **jdbc:h2:mem:trivia;IGNORECASE=TRUE**
* User Name: **sa**
* Password:

![H2 Console Login](/readme/h2_login.png)
![H2 Console Main View](/readme/h2_query.png)

### HEROKU Deployment
* Create a new application "trivia-game-app-heroku"
* Choose Heroku Git and configure your local code IDE to hook with git repository at '
https://git.heroku.com/trivia-game-app-heroku.git'
* When push any new changes, Heroku will auto build and delopy your application like the attached screenshot. and the app will be available at 'http://trivia-game-app-heroku.herokuapp.com'
![heroku deployment](/readme/heroku_activity.png)
