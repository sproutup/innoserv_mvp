(function () {
    'use strict';

    angular
        .module('sproutupApp')
        .controller('Oauth2Controller', Oauth2Controller);

    Oauth2Controller.$inject = ['$rootScope', '$stateParams', '$state', '$log', 'AuthService', 'OAuthService'];

    function Oauth2Controller($rootScope, $stateParams, $state, $log, authService, oauth) {
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
            console.log("[oauth2] init ", $stateParams);
            vm.user = angular.copy(authService.m.user);
            //requestGoogleApiToken();
            //url: '/oauth2callback?oauth_token&oauth_verifier',
            //console.log("found code: ", $location.search('code'));
            if($stateParams.code!==undefined){
                console.log("[oauth2] found state: ", $stateParams.state);
                console.log("[oauth2] found code: ", $stateParams.code);
                oauth.saveCallback($stateParams.state, $stateParams.code).then(function(data) {
                    // update the users analytics data
                    $rootScope.$broadcast('alert:success', {
                        message: 'Woohoo, we are connected!'
                    });
                    console.log('[oath2] success');
                    //$state.go("user.settings.analytics");
                    authService.redirect('user.home', '');
                }, function(error) {
                    $rootScope.$broadcast('alert:error', {
                        message: 'Oops, connection failed. Try again?'
                    });
                    console.log('[oath2] error');
                    //$state.go("user.settings.analytics");
                    //authService.redirect('user.home');
                });
            }
            else{
                console.log("[oauth2] missing token...");
                //$state.go("user.settings.analytics");
            }
        }
    }
})();
