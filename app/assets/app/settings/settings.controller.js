(function () {
    'use strict';

    angular
        .module('sproutupApp')
        .controller('SettingsController', SettingsController);

    SettingsController.$inject = ['$rootScope', '$stateParams', '$state', '$log', 'AuthService'];

    function SettingsController($rootScope, $stateParams, $state, $log, authService) {
        var vm = this;

        vm.user = {};

        activate();

        function activate() {
            if(!authService.ready()){
                var unbindWatch = $rootScope.$watch(authService.ready, function (value) {
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
            if (authService.loggedIn()) {
                vm.user = angular.copy(authService.m.user);
            } else {
                $state.go('user.login');
            }
        }
    }
})();
