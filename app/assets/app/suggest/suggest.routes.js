angular
    .module('sproutupApp')
    .config(config);

function config($stateProvider) {

    $stateProvider
        .state('user.suggest' ,{
            url: '/suggest',
            templateUrl: 'assets/app/suggest/mock.suggest.html',
            controller: 'BuzzController',
            controllerAs: 'vm',
            data: {
                title: 'Suggest Cool Products - SproutUp'
            }
        });
}