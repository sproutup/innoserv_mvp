angular
    .module('sproutupApp')
    .config(config);

function config($stateProvider) {

    $stateProvider
        .state('user.ambassador' ,{
            url: '/ambassador',
            templateUrl: 'assets/app/ambassador/ambassador.html',
            controller: 'BuzzController',
            controllerAs: 'vm',
            data: {
                title: 'Become a Product Ambassador - SproutUp'
            }
        });
}
