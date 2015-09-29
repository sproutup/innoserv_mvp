angular
    .module('sproutupApp')
    .factory('TrialService', TrialService);

TrialService.$inject = ['$resource'];

function TrialService($resource) {
	var service = {
		myTrials: myTrials,
		userTrials: userTrials,
		productTrials: productTrials
	};

	return service;

	function myTrials() {
		return $resource('/api/trials/:id', {id:'@id'}, {update:{method:'PUT'}} );
	}

	function userTrials() {
		return $resource('/api/user/:nickname/trials', {nickname:'@nickname'}, {update:{method:'PUT'}} );
	}

	function productTrials() {
		return $resource('/api/trials/product/:slug', {slug:'@slug'});
	}
}