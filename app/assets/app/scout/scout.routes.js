angular
    .module('sproutupApp')
    .config(config);

function config($stateProvider) {

    $stateProvider
        .state('user.scout' ,{
            url: '/scout',
            templateUrl: 'assets/app/scout/scout.html',
            controller: 'BuzzController',
            controllerAs: 'vm',
            data: {
                title: 'Scout Cool Products - SproutUp'
            }
        });
}