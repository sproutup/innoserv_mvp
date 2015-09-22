angular
    .module('sproutupApp')
    .factory('TrialService', TrialService);

TrialService.$inject = ['$resource'];

function TrialService($resource) {
	var service = {
		myTrials: myTrials,
		userTrials: userTrials
	};

	return service;

	function myTrials() {
		return $resource('/api/trials/:id', {id:'@id'}, {update:{method:'PUT'}} );
	}

	function userTrials() {
		return $resource('/api/user/:nickname/trials', {nickname:'@nickname'}, {update:{method:'PUT'}} );
	}
}
