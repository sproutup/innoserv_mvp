angular
    .module('sproutupApp')
    .config(config);

function config($stateProvider) {

    $stateProvider
        .state('user.content' ,{
            url: '/content',
            templateUrl: 'assets/app/content/content.html',
            data: {
                title: 'Content - SproutUp'
            }
        });
}