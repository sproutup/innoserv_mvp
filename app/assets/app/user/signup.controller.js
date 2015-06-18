(function () {
    'use strict';

    angular
        .module('sproutupApp')
        .controller('UserSignupController', UserSignupController);

    UserSignupController.$inject = ['$q', '$rootScope', '$stateParams', '$state', '$log', 'AuthService', '$location', '$window'];

    function UserSignupController($q, $rootScope, $stateParams, $state, $log, authService, $location, $window) {
        $log.debug("entered user signup");
        var vm = this;

        vm.signup = signup;
        vm.social = social;
        vm.form = {name:"", email:""};
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

        function signup(){
            console.log("## signup");

            // reset error message
            vm.form.error = "";

            var promise = authService.signup(vm.form);

            promise.then(
                function(data){
                    // go to wizard after signup
                    wizard();
                },
                function(error){
                    $log.info('Signup failed: ' + error + " " + new Date());
                    if(error.status=="USER_EXISTS"){
                        vm.form.error = "Oops, email's been taken. If it's you, please log in.";
                    }
                    else{
                        vm.form.error = "Signup failed";
                    }
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
