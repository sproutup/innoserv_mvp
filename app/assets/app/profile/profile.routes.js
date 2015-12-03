angular
    .module('sproutupApp')
    .config(config);

function config($stateProvider) {

    $stateProvider
        .state('user.profile' ,{
            url: '/user/:nickname',
            abstract: true,
            templateUrl: 'assets/app/profile/profile.html',
            controller: 'userDetailCtrl',
            controllerAs: 'vm'
        })
        .state('user.profile.buzz' ,{
            url: '',
            templateUrl: 'assets/app/profile/user-buzz.html',
            controller: 'BuzzController',
            controllerAs: 'vm',
            data: {
                title: 'User Profile - Buzz'
            }
        })
        .state('user.profile.trials' ,{
            url: '/trials',
            templateUrl: 'assets/app/profile/user-trials.html',
            controller: 'userTrialsController',
            controllerAs: 'vm',
            data: {
                title: 'User Profile - Product Trials'
            }
        })
        .state('user.profile.reach' ,{
            url: '/reach',
            templateUrl: 'assets/app/profile/user-reach.html',
//            controller: 'userTrialsController',
//            controllerAs: 'vm',
            data: {
                title: 'User Profile - Social Reach'
            }
        })
        .state('user.profile.points' ,{
            url: '/points',
            templateUrl: 'assets/app/profile/user-points.html',
            data: {
                title: 'User Profile - Sprout Points'
            }
        });
}