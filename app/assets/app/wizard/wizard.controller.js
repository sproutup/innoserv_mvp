(function () {
    'use strict';

    angular
        .module('sproutupApp')
        .controller('SignupWizardController', SignupWizardController);

    SignupWizardController.$inject = ['$rootScope', '$stateParams', '$state', '$log', 'AuthService'];

    function SignupWizardController($rootScope, $stateParams, $state, $log, authService) {
        $log.debug("signup wizard");

        var vm = this;
        vm.ready = authService.ready;

        activate();

        function activate() {
            if(!authService.ready()){
                console.log("wizard: waiting for auth service");
                var unbindWatch = $rootScope.$watch(authService.loggedIn, function (value) {
                    if ( value === true ) {
                      unbindWatch();
                      activate();
                    }
                });
            }
            else{
                console.log("wizard: auth service ready");
                vm.user = authService.m.user;
                wizard();
            }
        }

        function wizard(){
            console.log("wizard: ", vm.user);
            $state.go("user.wizard.email");
        }
    }
})();
