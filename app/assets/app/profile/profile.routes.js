angular
    .module('sproutupApp')
    .config(config);

function config($stateProvider) {

    $stateProvider
        .state('user.profile' ,{
            url: '/user/:nickname',
            templateUrl: 'assets/app/profile/profile.html',
            controller: 'userDetailCtrl',
            data: {
                title: 'User Profile'
            }
        });
}