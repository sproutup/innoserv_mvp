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
                title: 'Content - SproutUp'
            }
        })
        .state('user.singleBuzz' ,{
            url: '/buzz/:id',
            templateUrl: 'assets/app/buzz/singleBuzz.html',
            controller: 'BuzzController',
            controllerAs: 'vm',
            data: {
                title: 'Content - SproutUp'
            }
        });
}