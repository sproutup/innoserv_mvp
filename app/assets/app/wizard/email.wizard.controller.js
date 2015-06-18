(function () {
    'use strict';

    angular
        .module('sproutupApp')
        .controller('EmailSignupWizardController', EmailSignupWizardController);

    EmailSignupWizardController.$inject = ['$stateParams', '$state', '$log', 'AuthService','UserService'];

    function EmailSignupWizardController($stateParams, $state, $log, authService, userService) {
        $log.debug("signup wizard email");
        var vm = this;
        vm.user = authService.m.user;
        vm.save = save;

        activate();

        function activate() {
            if(vm.user.email === null || vm.user.email.length < 1){
                $log.debug("email missing");
            }
            else{
                $state.go("user.wizard.twitter");
            }
        }

        function save() {
            $log.debug("signup wizard -> save email");
            authService.save().then(function(data){
                $state.go("user.wizard.twitter");
            });
        }
    }
})();
