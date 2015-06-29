(function () {
    'use strict';

    angular
        .module('sproutupApp')
        .controller('SocialController', SocialController);

    SocialController.$inject = ['$scope', '$rootScope', '$stateParams', '$state', '$log', 'AuthService','UserService'];

    function SocialController($scope, $rootScope, $stateParams, $state, $log, authService, userService) {
        $log.debug("settings.social controller");
        var vm = this;
        vm.user = {};
        vm.save = save;

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
                console.log("social: auth service ready");
                reset();
            }
        }

        function reset() {
            vm.user = angular.copy(authService.m.user);
        }

        function save() {
            $log.debug("settings.social controller - save()");
            authService.save(vm.user).then(function(data){
                vm.basicinfoform.$setPristine();
                vm.basicinfoform.$setUntouched();

                $rootScope.$broadcast('alert:success', {
                    message: 'Saved'
                });
                $log.debug("save done");
            });
        }
    }
})();
