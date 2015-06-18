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
                var unbindWatch = $rootScope.$watch(authService.ready, function (value) {
                    if ( value === true ) {
                      unbindWatch();
                      activate();
                    }
                });
            }
            else{
                console.log("wizard: auth service ready");
                if(authService.m.isLoggedIn) {
                    vm.user = authService.m.user;
                    wizard();
                }
            }
        }

        function wizard(){
            console.log("wizard: ", vm.user);

            switch(vm.user.providerKey){
                case "twitter":
                    $log.debug("signup wizard -> twitter");
                    if(vm.user.email === null || vm.user.email.length < 1){
                        $log.debug("signup wizard -> ask for email");
                        $state.go("user.wizard.email");
                        return;
                    }
                    break;
                case "facebook":
                    if(vm.user.urlTwitter === null || vm.user.urlTwitter.length < 1){
                        $log.debug("signup wizard -> ask for twitter link");
                        $state.go("user.wizard.twitter");
                        return;
                    }
                    break;
                case "password":
                    $log.debug("signup wizard -> password");
                    if(vm.user.urlTwitter === null || vm.user.urlTwitter.length < 1){
                        $log.debug("signup wizard -> ask for twitter link");
                        $state.go("user.wizard.twitter");
                        return;
                    }
                    break;
            }
            $state.go("user.search");
        }
    }
})();
