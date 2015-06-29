(function () {
    'use strict';

    angular
        .module('sproutupApp')
        .controller('ProfileController', ProfileController);

    ProfileController.$inject = ['$rootScope', '$stateParams', '$state', '$log', 'AuthService','UserService'];

    function ProfileController($rootScope, $stateParams, $state, $log, authService, userService) {
        $log.debug("settings.profile controller");
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
                console.log("profile: auth service ready");
                init();
            }

            //vm.basicinfoform.$setPristine();
            //vm.basicinfoform.$setUntouched();
        }

        function init() {
            vm.user = angular.copy(authService.m.user);
        }

        function save() {
            $log.debug("settings.profile controller - save()");
            authService.save(vm.user).then(function(data){
                vm.basicinfoform.$setPristine();
                vm.basicinfoform.$setUntouched();
                $log.debug("save done");
                $rootScope.$broadcast('alert:success', {
                    message: 'Saved'
                });
            });
        }
    }
})();
