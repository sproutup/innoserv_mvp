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
            $log.debug("settings.social controller - save()");
            update();
            authService.save().then(function(data){
                $rootScope.$broadcast('alert:success', {
                    message: 'Saved'
                });
                $log.debug("save done");
            });
        }
    }
})();
