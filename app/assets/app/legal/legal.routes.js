angular
    .module('sproutupApp')
    .config(config);

function config($stateProvider) {

    $stateProvider
        .state('user.terms' ,{
            url: '/terms',
            templateUrl: 'views/terms',
            data: {
                title: 'Terms of Service - SproutUp'
            }
        })
        .state('user.privacy' ,{
            url: '/privacy',
            templateUrl: 'views/privacy',
            data: {
                title: 'Privacy Policy - SproutUp'
            }
        })
        .state('user.trial-terms' ,{
            url: '/terms/trial',
            templateUrl: 'assets/app/legal/trial-terms.html',
            data: {
                title: 'Trial Terms of service - SproutUp'
            }
        });
}