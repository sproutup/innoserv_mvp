angular
    .module('sproutupApp')
    .config(config);

function config($stateProvider) {

    $stateProvider
        .state('user.content' ,{
            url: '/content',
            templateUrl: 'assets/app/content/content.html',
            controller: 'ContentController',
            controllerAs: 'vm',
            data: {
                title: 'Content - SproutUp'
            }
        });
}