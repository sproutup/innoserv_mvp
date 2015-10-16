(function() {

angular
    .module('sproutupApp')
    .controller('userDetailCtrl', userDetailCtrl);

userDetailCtrl.$inject = ['$scope', '$stateParams', '$state', '$log', 'UserService', 'AnalyticsService'];

function userDetailCtrl($scope, $stateParams, $state, $log, UserService, AnalyticsService) {
    var vm = this;

    $log.debug("entered user details ctrl. nickname=" + $stateParams.nickname);
    UserService.get({nickname: $stateParams.nickname}).$promise.then(
        function(data) {
            // success
            $scope.stranger = data;
            vm.points = data.points;
            vm.trials = data.trials;
            vm.posts = data.posts;
            vm.stranger = data;

            AnalyticsService.UserReach().get({userId: data.id}).$promise.then(function(reach){
                console.log('reach: ', reach);
                vm.reach = reach.total;
            });
        },
        function(error) {
            // error handler
            $state.go("home");
        }
    );

}

})();