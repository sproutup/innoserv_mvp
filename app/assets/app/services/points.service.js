angular
    .module('sproutupApp')
    .factory('PointsService', PointsService);

PointsService.$inject = ['$resource'];

function PointsService($resource) {
	var service = {
		addPoints: addPoints
	};

	return service;

	function addPoints() {
		return $resource('/api/reward/event/:activity_id', {activity_id:'@activity_id'});
	}
}