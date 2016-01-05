angular
    .module('sproutupApp')
    .config(config);

function config($stateProvider) {
    $stateProvider
        .state('user.hangout' ,{
            url: '/hangout/upcoming',
            templateUrl: 'assets/app/hangout/hangout.html',
            controller: '',
            controllerAs: '',
            data: {
                title: 'Hangout With Cool People - SproutUp'
            }
        })
        .state('user.pastHangout' ,{
            url: '/hangout/past',
            templateUrl: 'assets/app/hangout/past-hangout.html',
            controller: '',
            controllerAs: '',
            data: {
                title: 'Hangout With Cool People - SproutUp'
            }
        })
        .state('user.hangoutPreview' ,{
            url: '/hangout-coming-soon',
            templateUrl: 'assets/app/hangout/hangout-coming-soon.html',
            controller: '',
            controllerAs: '',
            data: {
                title: 'Hangout With Cool People - SproutUp'
            }
        });
}