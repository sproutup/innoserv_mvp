(function () {
    'use strict';

    angular
        .module('sproutupApp')
        .controller('AnalyticsController', AnalyticsController);

    AnalyticsController.$inject = ['$rootScope', '$state', '$log', 'AuthService','GoogleApiService', 'AnalyticsService', '$filter'];

    function AnalyticsController($rootScope, $state, $log, authService, googleApiService, analyticsService, $filter) {
        var vm = this;

        vm.user = {};
        vm.analytics = {};
        vm.ga = {};
        vm.yt = {};
        vm.googleAnalyticsAPI = false;
        vm.youtubeAnalyticsAPI = false;
        vm.requestGoogleApiToken = requestGoogleApiToken;
        vm.requestAnalyticsTokenUrl = '';
        vm.requestYoutubeTokenUrl = '';
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

            analyticsService.getAll().then(function(data){
                vm.analytics = data;
                vm.ga = $filter("filter")(data[0], {kind:"google"});
                vm.yt = $filter("filter")(data[0], {kind:"youtube"});
                authService.m.user.analytics = data;
                vm.googleAnalyticsAPI = data[0].googleAnalyticsAPI;
                vm.youtubeAnalyticsAPI = data[0].youtubeAnalyticsAPI;
            });

//            // right now we can have only one, but its prepared for more than one account
//            if(authService.m.user.analytics!==undefined){
//                vm.googleAnalyticsAPI = authService.m.user.analytics[0].googleAnalyticsAPI;
//                vm.youtubeAnalyticsAPI = authService.m.user.analytics[0].youtubeAnalyticsAPI;
//            }
        }

        function requestGoogleApiToken() {
            googleApiService.getAuthorizationParams().then(function(data) {
                vm.requestAnalyticsTokenUrl = data.ga_url;
                vm.requestYoutubeTokenUrl = data.yt_url;
            }, function(error) {
            });
        }

        function revokeAuthorization(){
            googleApiService.revokeAuthorization().then(function(data) {
                authService.m.user.analytics = data;
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
