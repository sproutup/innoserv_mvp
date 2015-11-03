angular
    .module('sproutupApp')
    .config(config);

function config($stateProvider) {

    $stateProvider
        .state('user.ambassador' ,{
            url: '/ambassador',
            templateUrl: 'assets/app/ambassador/ambassador.html',
            data: {
                title: 'Become a Product Ambassador - SproutUp'
            }
        });
}
