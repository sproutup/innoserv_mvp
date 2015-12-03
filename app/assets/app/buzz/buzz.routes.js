angular
    .module('sproutupApp')
    .config(config);

function config($stateProvider) {

    $stateProvider
        .state('user.buzz' ,{
            url: '/buzz',
            templateUrl: 'assets/app/content/content.html',
            controller: 'BuzzController',
            controllerAs: 'vm',
            data: {
                title: 'Buzz - SproutUp'
            }
        })
        .state('user.singleBuzz' ,{
            url: '/buzz/:id',
            templateUrl: 'assets/app/buzz/single-buzz.html',
            controller: 'BuzzController',
            controllerAs: 'vm',
            data: {
                title: 'Buzz - SproutUp'
            }
        });
}