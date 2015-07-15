(function () {
    'use strict';

    angular
        .module('sproutupApp')
        .controller('AnalyticsController', AnalyticsController);

    AnalyticsController.$inject = ['$rootScope', '$state', '$log', 'AuthService','GoogleApiService'];

    function AnalyticsController($rootScope, $state, $log, authService, googleApiService) {
        var vm = this;

        vm.user = {};
        vm.googleAnalyticsAPI = false;
        vm.youtubeAnalyticsAPI = false;
        vm.requestGoogleApiToken = requestGoogleApiToken;
        vm.requestAnalyticsTokenUrl = '';
        vm.revokeAuthorization = revokeAuthorization;

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

            // right now we can have only one, but its prepared for more than one account
            if(authService.m.user.analytics!==undefined){
                vm.googleAnalyticsAPI = authService.m.user.analytics[0].googleAnalyticsAPI;
                vm.youtubeAnalyticsAPI = authService.m.user.analytics[0].youtubeAnalyticsAPI;
            }

//            //console.log("found code: ", $location.search('code'));
//            if($stateParams.code!==undefined){
//                console.log("found code: ", $stateParams.code);
//                googleApiService.exchangeAuthorizationCodeForToken($stateParams.code, $stateParams.scope);
//                authService.m.user.analytics = [{googleAnalyticsAPI: true, youtubeAnalyticsAPI: true}];
//                vm.googleAnalyticsAPI = authService.m.user.analytics[0].googleAnalyticsAPI;
//                vm.youtubeAnalyticsAPI = authService.m.user.analytics[0].youtubeAnalyticsAPI;
//                $rootScope.$broadcast('alert:success', {
//                    message: 'Authorization granted'
//                });
//            }
        }

        function requestGoogleApiToken() {
            //vm.requestAnalyticsTokenUrl = googleApiService.requestToken();

            googleApiService.getAuthorizationParams().then(function(data) {
                vm.requestAnalyticsTokenUrl = data.url;
                console.log("url: ", data.url);
            }, function(error) {
            });
        }

        function revokeAuthorization(){
            googleApiService.revokeAuthorization().then(function(data) {
                authService.m.user.analytics = [{googleAnalyticsAPI: false, youtubeAnalyticsAPI: false}];
                vm.googleAnalyticsAPI = authService.m.user.analytics[0].googleAnalyticsAPI;
                vm.youtubeAnalyticsAPI = authService.m.user.analytics[0].youtubeAnalyticsAPI;
                $rootScope.$broadcast('alert:success', {
                    message: 'Authorization revoked'
                });
            }, function(error) {
                $rootScope.$broadcast('alert:error', {
                    message: 'Authorization revoke failed'
                });
            });
        }
    }
})();
