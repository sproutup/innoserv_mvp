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
        })
        .state('user.pastHangout' ,{
            url: '/past-hangout',
            templateUrl: 'assets/app/hangout/past-hangout.html',
            controller: '',
            controllerAs: '',
            data: {
                title: 'Hangout With Cool People - SproutUp'
            }
        });
}