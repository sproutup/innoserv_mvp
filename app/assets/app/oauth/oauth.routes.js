angular
    .module('sproutupApp')
    .config(config);

function config($stateProvider) {

    $stateProvider
        .state('user.oauth' ,{
            url: '/oauth2callback?state&code&scope',
            template: '<div>oauth2callback</div>',
            controller: 'Oauth2Controller',
            controllerAs: 'vm',
            data: {
                title: 'Delete Me'
            }
        })
        .state('user.oauth2' ,{
            url: '/oauth/2/callback?state&code&scope',
            template: '<div>oauth2callback</div>',
            controller: 'Oauth2Controller',
            controllerAs: 'vm',
            data: {
                title: 'Delete Me'
            }
        })
        .state('user.oauth2callback' ,{
            //url: '/oauth2callback?code&scope',
            url: '/oauth',
            template: '<div>oauth2callback</div>',
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
            template: '<div>oauth1callback</div>',
            controller: 'Oauth1Controller',
            controllerAs: 'vm',
            data: {
                title: 'oauth1 callback'
            }
        });
}