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
        .state('user.mockAmbassadorDetailsBuzz' ,{
            url: '/mock-ambassador-details-buzz',
            templateUrl: 'assets/app/ambassador/mock-ambassador-details-buzz.html',
            data: {
                title: 'Ambassador Details - SproutUp'
            }
        })
        .state('user.mockAmbassadorDetailsContent' ,{
            url: '/mock-ambassador-details-content',
            templateUrl: 'assets/app/ambassador/mock-ambassador-details-content.html',
            data: {
                title: 'Ambassador Details - SproutUp'
            }
        })
        .state('user.mockAmbassadorDetailsCreativity' ,{
            url: '/mock-ambassador-details-creativity',
            templateUrl: 'assets/app/ambassador/mock-ambassador-details-creativity.html',
            data: {
                title: 'Ambassador Details - SproutUp'
            }
        });
}
