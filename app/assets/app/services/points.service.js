angular
    .module('sproutupApp')
    .factory('PointsService', PointsService);

PointsService.$inject = ['$resource'];

function PointsService($resource) {
	var service = {
		addPoints: addPoints,
		getSummary: getSummary
	};

	return service;

	function addPoints() {
		return $resource('/api/reward/event/:activity_id', {activity_id:'@activity_id'});
	}

	function getSummary() {
		return $resource('/api/user/:id/reward/summary', {id: '@id'});
	}
}