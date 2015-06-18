(function () {
    'use strict';

    angular
        .module('sproutupApp')
        .controller('UserLoginController', UserLoginController);

    UserLoginController.$inject = ['$q', '$rootScope', '$stateParams', '$state', '$log', 'AuthService', '$location', '$window'];

    function UserLoginController($q, $rootScope, $stateParams, $state, $log, authService, $location, $window) {
        $log.debug("entered user login");
        var vm = this;

        vm.login = login;
        vm.social = social;
        vm.form = {};
        vm.ready = authService.ready;

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
                if(authService.m.isLoggedIn) {
                    wizard();
                }
            }
        }

        function login(){
            console.log("## login attempt", new Date());

            // reset error message
            vm.form.error = "";

            var promise = authService.login(vm.form);

            promise.then(
                function(data){
                    wizard();
                },
                function(error){
                    $log.info('Login failed: ' + new Date());
                    vm.form.error = true;
                }
            );
        }

        function social(provider) {
            $log.debug("provider: " + provider);

            var currentPath = $location.path();
            $log.debug("path: " + currentPath);

            var dataObject = {
                originalUrl : currentPath
            };

            var promise = authService.provider(provider, dataObject);

            promise.then(
                function(data){
                    $log.debug(data);
                    $window.location.href = data;
                    wizard();
                },
                function(error){
                    $log.info('Login failed: ' + new Date());
                }
            );
        }

        function wizard(){
            $state.go("user.wizard.start");
        }
    }
})();
