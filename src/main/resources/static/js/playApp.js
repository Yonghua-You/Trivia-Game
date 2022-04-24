(function() {

	var app = angular.module("playApp", []);

	var playGameCtrl = function($scope, $http, $location) {
		var url = $location.absUrl();
		var startIndex = url.indexOf('game/') + 5;
		var endIndex = url.indexOf('/play');
		var startQuestion = url.indexOf('/question/');
		var orderNum = 0;
		var realUrl = url;
		if(startQuestion != -1)
		{
			orderNum = url.substring(startQuestion + 10);
			realUrl = url.substring(0, startQuestion);
		}

		$scope.gameGuid = url.substring(startIndex, endIndex);
		$scope.Math = window.Math;
		
		var question;
		var questionResult;
		var answers = [];

		
		$scope.isPlaying = true;
		
		$scope.initialize = function(order) {
			console.log($scope.gameGuid);

			if ($scope.gameGuid == 0)
				return;
			
			$scope.playing = true;
		
			$http.get("/api/game/" + $scope.gameGuid + "/question/"+ order +"?onlyValid=true")
				.then(
					function(response) {
						question = response.data;
						if(question.id === undefined)
						{
							window.location.href="/" ;
						}
						$scope.setQuestion(question.id);
					},
					function(reason) {
						$scope.error = "Could not fetch the data.";
					}
				);
		}
		
		$scope.setQuestion = function(questionId) {
			if (questionId === undefined) {

				return;
			}
			$http.get("/api/game/" + $scope.gameGuid + "/question/" + questionId + "/answers")
				.then(
					function(response) {
						$scope.currentQuestion = question;
						$scope.currentAnswers = response.data;
						//$scope.$applyAsync();
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
						//$scope.initialize(question.order);
						// $http.get("/api/game/" + $scope.gameGuid + "/question/" +  question.order + "?onlyValid=true")
						// 	.then(
						// 		function(response) {
						// 			question = response.data;
						// 			if(question == undefined)
						// 			{
						// 				window.location.href="/";
						// 				return;
						// 			}
						// 			$scope.setQuestion(question.id);
						// 			//$scope.initialize();
						// 		},
						// 		function(reason) {
						// 			$scope.error = "Could not fetch the data.";
						// 		}
						// 	);
						// window.location.href= realUrl + "/question/" + question.order;
						window.location.href="/game/" + $scope.gameGuid + "/question/" +  question.id + "/answers" + "?currentOrder=" + question.order;
					}else{
						window.location.href="/game/" + $scope.gameGuid + "/question/" +  question.id + "/answers" ;

					}

					$scope.playing = false;
				}, 
				function(reason) {
					$scope.error = "Could not fetch the data.";
				}
			);
		}
	
		$scope.initialize(orderNum);
	};

	app.controller("PlayGameCtrl", ["$scope", "$http","$location", playGameCtrl]);

}());