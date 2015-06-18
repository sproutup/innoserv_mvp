(function () {
    'use strict';

    angular
        .module('sproutupApp')
        .controller('UserLoginController', UserLoginController);

    UserLoginController.$inject = ['$q', '$rootScope', '$stateParams', '$state', '$log', 'AuthService'];

    function UserLoginController($q, $rootScope, $stateParams, $state, $log, authService) {
        $log.debug("entered user login");
        var vm = this;

        vm.login = login;
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

        function wizard(){
            $state.go("user.wizard.start");
        }
    }
})();
