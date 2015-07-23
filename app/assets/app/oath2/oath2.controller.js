(function () {
    'use strict';

    angular
        .module('sproutupApp')
        .controller('Oauth2Controller', Oauth2Controller);

    Oauth2Controller.$inject = ['$rootScope', '$stateParams', '$state', '$log', 'AuthService','GoogleApiService'];

    function Oauth2Controller($rootScope, $stateParams, $state, $log, authService, googleApiService) {
        var vm = this;

        vm.user = {};

        activate();

        function activate() {
            console.log("Oauth2Controller");
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
            console.log("Oauth2Controller");
            vm.user = angular.copy(authService.m.user);
            //requestGoogleApiToken();

            //console.log("found code: ", $location.search('code'));
            if($stateParams.code!==undefined){
                console.log("found code: ", $stateParams.code);
                googleApiService.exchangeAuthorizationCodeForToken($stateParams.code, $stateParams.scope).then(function(data) {
                    // update the users analytics data
                    authService.m.user.analytics = data;
                    $rootScope.$broadcast('alert:success', {
                        message: 'Authorization granted'
                    });
                    $state.go("user.settings.analytics");
                }, function(error) {
                    $rootScope.$broadcast('alert:error', {
                        message: 'Authorization failed'
                    });
                    $state.go("user.settings.analytics");
                });
            }
            else{
                $state.go("user.settings.analytics");
            }
        }
    }
})();
