(function() {

angular
    .module('sproutupApp')
    .controller('myPointsController', myPointsController);

myPointsController.$inject = ['$scope', 'AuthService', '$rootScope', 'RewardService', '$state'];

function myPointsController($scope, AuthService, $rootScope, RewardService, $state) {
		var vm = this;

        activate();

        function activate() {
            if(!AuthService.ready()){
                var unbindWatch = $rootScope.$watch(AuthService.ready, function (value) {
                    if ( value === true ) {
                      unbindWatch();
                      activate();
                    }
                });
            }
            else{
                if(AuthService.m.isLoggedIn) {
                    init();
                } else {
                    $state.go('user.perk');
                }
            }
        }

        function init() {
            vm.user = AuthService.m.user;
            if (vm.user) {
                var MyRewards = RewardService.myRewards();
                vm.rewardsLog = MyRewards.query(function(res) {
                    vm.rewardsInit = true;
                });
            } else {
                vm.rewardsInit = true;
            }
        }
}

})();