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
                reset();
            }

            //vm.basicinfoform.$setPristine();
            //vm.basicinfoform.$setUntouched();
        }

        function reset() {
            vm.user = angular.copy(authService.m.user);
        }

        function update() {
            authService.m.user = angular.extend(vm.user);
        }


        function save() {
            $log.debug("settings.profile controller - save()");
            update();
            authService.save().then(function(data){
                $log.debug("save done");
                $rootScope.$broadcast('alert:success', {
                    message: 'Saved'
                });
            });
        }
    }
})();
