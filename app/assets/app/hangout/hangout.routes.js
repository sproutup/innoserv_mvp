angular
    .module('sproutupApp')
    .config(config);

function config($stateProvider) {
    $stateProvider
        .state('user.hangout' ,{
            url: '/hangout',
            templateUrl: 'assets/app/hangout/hangout.html',
            controller: '',
            controllerAs: '',
            data: {
                title: 'Hangout With Cool People - SproutUp'
            }
        });
}