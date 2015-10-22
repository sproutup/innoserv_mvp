(function() {

angular
    .module('sproutupApp')
    .controller('myPointsController', myPointsController);

myPointsController.$inject = ['$scope', 'AuthService', '$rootScope', 'RewardService', '$state'];

function myPointsController($scope, AuthService, $rootScope, RewardService, $state) {
		var vm = this;
        var rewardsLog;

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
                    renderRewardsLogCopy();
                    vm.rewardsInit = true;
                });
            } else {
                vm.rewardsInit = true;
            }
        }

        function renderRewardsLogCopy() {
            for (var r = 0; r < vm.rewardsLog.length; r++) {
                if (vm.rewardsLog[r].activity_id === 3002) {
                    vm.rewardsLog[r].title = 'Comment on a buzz post';
                } else if (vm.rewardsLog[r].activity_id === 1000) {
                    vm.rewardsLog[r].title = 'Publish content for a product trial';
                }
            }
        }
}

})();