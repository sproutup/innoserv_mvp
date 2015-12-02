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
                vm.chart = {
                    total: vm.reach.total,
                    labels: [],
                    data: []
                };
                for (var key in vm.reach) {
                  if (key === 'fb') {
                    vm.chart.labels.push('Facebook');
                    vm.chart.data.push(vm.reach[key]);
                  }
                  if (key === 'ga') {
                    vm.chart.labels.push('Google Analytics');
                    vm.chart.data.push(Math.round(vm.reach[key]));
                  }
                  if (key === 'ig') {
                    vm.chart.labels.push('Instagram');
                    vm.chart.data.push(vm.reach[key]);
                  }
                  if (key === 'pi') {
                    vm.chart.labels.push('Pinterest');
                    vm.chart.data.push(vm.reach[key]);
                  }
                  if (key === 'tw') {
                    vm.chart.labels.push('Twitter');
                    vm.chart.data.push(vm.reach[key]);
                  }
                  if (key === 'yt') {
                    vm.chart.labels.push('YouTube');
                    vm.chart.data.push(vm.reach[key]);
                  }
                }
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