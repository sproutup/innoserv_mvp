angular
    .module('sproutupApp')
    .config(config);

function config($stateProvider) {

    $stateProvider
        .state('oauth' ,{
            url: '/oauth2callback?state&code&scope',
            templateUrl: 'assets/app/oauth/oauth-callback.html',
            controller: 'Oauth2Controller',
            controllerAs: 'vm',
            data: {
                title: 'Delete Me'
            }
        })
        .state('oauth2' ,{
            url: '/oauth/2/callback?state&code&scope',
            templateUrl: 'assets/app/oauth/oauth-callback.html',
            controller: 'Oauth2Controller',
            controllerAs: 'vm',
            data: {
                title: 'Delete Me'
            }
        })
        .state('oauth2callback' ,{
            //url: '/oauth2callback?code&scope',
            url: '/oauth',
            templateUrl: 'assets/app/oauth/oauth-callback.html',
            //templateUrl: 'assets/app/home/index.html',
            controller: 'Oauth2Controller',
            controllerAs: 'vm',
            data: {
                title: 'oauth2 callback'
            }
        })
        .state('oauth1callback' ,{
            //url: '/oauth2callback?oauth_token&oauth_verifier',
            url: '/oauth/1/callback?oauth_token&oauth_verifier',
            templateUrl: 'assets/app/oauth/oauth-callback.html',
            controller: 'Oauth1Controller',
            controllerAs: 'vm',
            data: {
                title: 'oauth1 callback'
            }
        });
}