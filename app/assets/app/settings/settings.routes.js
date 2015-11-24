angular
    .module('sproutupApp')
    .config(config);

function config($stateProvider) {

    $stateProvider
        .state('user.settings' ,{
            url: '/settings',
            abstract: true,
            controller: 'SettingsController',
            controllerAs: 'vm',
            templateUrl: 'assets/app/settings/menu.html',
            data: {
                title: 'Settings'
            }
        })
        .state('user.settings.profile' ,{
            url: '/profile',
            templateUrl: 'assets/app/settings/profile.html',
            controller: 'ProfileController',
            controllerAs: 'vm',
            data: {
                title: 'Profile'
            }
        })
        .state('user.settings.social' ,{
            url: '/social',
            templateUrl: 'assets/app/settings/social.html',
            controller: 'AnalyticsController',
            controllerAs: 'vm',
            data: {
                title: 'Social Profile'
            }
        })
        // .state('user.settings.analytics' ,{
        //     url: '/analytics',
        //     templateUrl: 'assets/app/settings/analytics.html',
        //     controller: 'AnalyticsController',
        //     controllerAs: 'vm',
        //     data: {
        //         title: 'Analytics Settings'
        //     }
        // })
        .state('user.settings.trials' ,{
            url: '/trials',
            templateUrl: 'assets/app/settings/trials.html',
            data: {
                title: 'Trials'
            }
        });
}