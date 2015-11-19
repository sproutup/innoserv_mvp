(function() {

angular
    .module('sproutupApp')
    .controller('userDetailCtrl', userDetailCtrl);

userDetailCtrl.$inject = ['$scope', '$stateParams', '$state', '$log', 'UserService', 'AnalyticsService', 'PointsService'];

function userDetailCtrl($scope, $stateParams, $state, $log, UserService, AnalyticsService, PointsService) {
    var vm = this;
    vm.getPoints = getPoints;
    
    UserService.get({nickname: $stateParams.nickname}).$promise.then(
        function(data) {
            // success
            $scope.stranger = data;
            vm.points = data.points;
            vm.trials = data.trials;
            vm.posts = data.posts;
            vm.stranger = data;

            AnalyticsService.userReach().get({userId: data.id}).$promise.then(function(reach){
                vm.reach = reach;
            });
        },
        function(error) {
            // error handler
            $state.go("home");
        }
    );

    function getPoints() {
        PointsService.getSummary().query({
            id: vm.stranger.id
        }, function(res) {
            vm.summary = res;
        });
    }

}

})();