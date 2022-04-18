(function() {

	var app = angular.module("playApp", []);

	var playGameCtrl = function($scope, $http, $location) {
		var url = $location.absUrl();
		var startIndex = url.indexOf('game/') + 5;
		var endIndex = url.indexOf('/play');
		$scope.gameGuid = url.substring(startIndex, endIndex);
		$scope.Math = window.Math;
		
		var question;
		var questionResult;
		var answers = [];

		
		$scope.isPlaying = true;
		
		$scope.initialize = function() {
			console.log($scope.gameGuid);

			if ($scope.gameGuid == 0)
				return;
			
			$scope.playing = true;
		
			$http.get("/api/game/" + $scope.gameGuid + "/question/0?onlyValid=true")
				.then(
					function(response) {
						question = response.data;
						$scope.setQuestion(question.id);
					},
					function(reason) {
						$scope.error = "Could not fetch the data.";
					}
				);
		}
		
		$scope.setQuestion = function(questionId) {

			$http.get("/api/game/" + $scope.gameGuid + "/question/" + questionId + "/answers")
				.then(
					function(response) {
						$scope.currentQuestion = question;
						$scope.currentAnswers = response.data;
					}, 
					function(reason) {
						$scope.error = "Could not fetch the data.";
					}
				);
		}
		
		$scope.answerQuestion = function(selection) {
			if (selection === undefined) {
				alert("Please, choose an answer");
				return;
			}
						
			answers.push({
				question: $scope.currentQuestion.id,
				selectedAnswer: selection
			});
			$scope.submitAnswer();

		}
		
		$scope.submitAnswer = function() {
			$http.post("/api/game/" + $scope.gameGuid + "/question/" + question.id + "/submitAnswer",
					JSON.stringify(answers))
			.then(
				function(response) {
					questionResult = response.data;
					if(questionResult.correctQuestions == 1){
						$http.get("/api/game/" + $scope.guid + "/question/" +  question.id + "?onlyValid=true")
							.then(
								function(response) {
									question = response.data;
									$scope.setQuestion(question.id);
								},
								function(reason) {
									$scope.error = "Could not fetch the data.";
								}
							);
					}else{
						window.location.href="/";

					}

					$scope.playing = false;
				}, 
				function(reason) {
					$scope.error = "Could not fetch the data.";
				}
			);
		}
	
		$scope.initialize();	
	};

	app.controller("PlayGameCtrl", ["$scope", "$http","$location", playGameCtrl]);

}());