(function () {
    'use strict';

    angular
        .module('sproutupApp')
        .controller('SettingsController', SettingsController);

    SettingsController.$inject = ['$rootScope', '$stateParams', '$state', '$log', 'AuthService','UserService'];

    function SettingsController($rootScope, $stateParams, $state, $log, authService, userService) {
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
                reset();
            }
        }

        function reset() {
            vm.user = angular.copy(authService.m.user);
        }
    }
})();