(function () {
    'use strict';

    angular
        .module('sproutupApp')
        .controller('SettingsController', SettingsController);

    SettingsController.$inject = ['$rootScope', '$stateParams', '$state', '$log', 'AuthService','GoogleApiService'];

    function SettingsController($rootScope, $stateParams, $state, $log, authService, googleApiService) {
        var vm = this;

        vm.user = {};
        vm.requestGoogleApiToken = requestGoogleApiToken;
        vm.exchangeAuthorizationCodeForToken = exchangeAuthorizationCodeForToken;
        vm.requestAnalyticsTokenUrl = '';

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
            vm.user = angular.copy(authService.m.user);
            requestGoogleApiToken();

//            console.log("found code: ", $stateParams.code);
//            console.log("found scope: ", $stateParams.scope);

            //console.log("found code: ", $location.search('code'));
            if($stateParams.code!==undefined){
                console.log("found code: ", $stateParams.code);
                //exchangeAuthorizationCodeForToken($stateParams.code);
            }

        }

        function requestGoogleApiToken() {
            vm.requestAnalyticsTokenUrl = googleApiService.requestToken();
        }

        function exchangeAuthorizationCodeForToken(code) {
            vm.exchangeAuthorizationCodeForToken = googleApiService.exchangeAuthorizationCodeForToken;
        }
    }
})();
