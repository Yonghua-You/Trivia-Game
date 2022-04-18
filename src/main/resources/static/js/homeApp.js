(function() {

	var app = angular.module("homeApp", []);

	var homeCtrl = function($scope, $http) {

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
	
		$scope.getFutureGame();
		
	};

	app.controller("HomeCtrl", ["$scope", "$http", homeCtrl]);

}());