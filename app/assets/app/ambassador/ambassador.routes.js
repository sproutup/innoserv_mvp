angular
    .module('sproutupApp')
    .config(config);

function config($stateProvider) {

    $stateProvider
        .state('user.ambassador' ,{
            url: '/mock-ambassador',
            templateUrl: 'assets/app/ambassador/mock-ambassador.html',
            data: {
                title: 'Become a Product Ambassador - SproutUp'
            }
        })
        .state('user.mockAmbassadorDetails' ,{
            url: '/mock-ambassador-details',
            templateUrl: 'assets/app/ambassador/mock-ambassador-details.html',
            data: {
                title: 'Ambassador Details - SproutUp'
            }
        });
}
