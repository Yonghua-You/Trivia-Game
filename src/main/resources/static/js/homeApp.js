(function() {

	var app = angular.module("homeApp", []);

	var homeCtrl = function($scope, $rootScope, $http, $interval) {

		$scope.getFutureGame = function() {

			$http.get("/api/game/futuregame")
				.then(
					function(response) {
						$scope.game = response.data;
						$rootScope.gameGuid = $scope.game.guid;
					},
					function(reason) {
						console.log(reason.data);
					}
				);
		}

		$scope.callAtInterval = function() {
			console.log("Interval occurred");
			$scope.getFutureGame();
		}
	
		$interval($scope.callAtInterval, 5000);
		
	};



	app.controller("HomeCtrl", ["$scope", "$rootScope", "$http", "$interval" , homeCtrl]);

}());