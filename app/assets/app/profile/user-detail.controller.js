(function() {

angular
    .module('sproutupApp')
    .controller('userDetailCtrl', userDetailCtrl);

userDetailCtrl.$inject = ['$scope', '$stateParams', '$state', '$log', 'UserService'];

function userDetailCtrl($scope, $stateParams, $state, $log, UserService) {
    var vm = this;

    $log.debug("entered user details ctrl. nickname=" + $stateParams.nickname);
    UserService.get({nickname: $stateParams.nickname}).$promise.then(
        function(data) {
            // success
            $scope.stranger = data;
            vm.points = data.points;
            vm.stranger = data;
        },
        function(error) {
            // error handler
            $state.go("home");
        }
    );
}

})();