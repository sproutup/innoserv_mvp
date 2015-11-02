angular
    .module('sproutupApp')
    .config(config);

function config($stateProvider) {

    $stateProvider
        .state('user.points' ,{
            url: '/mypoints',
            templateUrl: 'assets/app/rewards/my-points.html',
            controller: 'myPointsController',
            controllerAs: 'vm'
        });
}