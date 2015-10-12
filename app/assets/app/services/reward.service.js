angular
    .module('sproutupApp')
    .factory('RewardService', RewardService);

RewardService.$inject = ['$resource'];

function RewardService($resource){
	var service = {
		myRewards: myRewards
	};
	return service;

	function myRewards() {
		return $resource('/api/user/reward');
	}
}