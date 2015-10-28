(function () {
    'use strict';

    angular
        .module('sproutupApp')
        .controller('Oauth1Controller', Oauth1Controller);

    Oauth1Controller.$inject = ['$rootScope', '$stateParams', '$state', '$log', 'AuthService', 'OAuthService'];

    function Oauth1Controller($rootScope, $stateParams, $state, $log, authService, oauth) {
        var vm = this;

        vm.user = {};

        activate();

        function activate() {
            if(!authService.ready()){
                var unbindWatch = $rootScope.$watch(authService.loggedIn, function (value) {
                    if ( value === true ) {
                      unbindWatch();
                      activate();
                    }
                });
            }
            else{
                init();
            }
        }

        function init() {
            console.log("[oauth1] init ", $stateParams);
            vm.user = angular.copy(authService.m.user);
            //requestGoogleApiToken();
            //url: '/oauth2callback?oauth_token&oauth_verifier',
            //console.log("found code: ", $location.search('code'));
            if($stateParams.oauth_token!==undefined){
                console.log("[oauth1] found token: ", $stateParams.oauth_token);
                oauth.saveCallback($stateParams.oauth_token, $stateParams.oauth_verifier).then(function(data) {
                    // update the users analytics data
                    $rootScope.$broadcast('alert:success', {
                        message: 'Authorization granted'
                    });
                    console.log('[oath1] success');
                    //$state.go("user.settings.analytics");
                    authService.redirect('user.home', '');
                }, function(error) {
                    $rootScope.$broadcast('alert:error', {
                        message: 'Authorization failed'
                    });
                    console.log('[oath1] error');
                    //$state.go("user.settings.analytics");
                    //authService.redirect('user.home');
                });
            }
            else{
                console.log("[oauth1] missing token...");
                //$state.go("user.settings.analytics");
            }
        }
    }
})();
