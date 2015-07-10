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
        }

        function requestGoogleApiToken() {
            vm.requestAnalyticsTokenUrl = googleApiService.requestToken();
        }
    }
})();
