(function () {
    'use strict';

    angular
        .module('sproutupApp')
        .controller('TwitterSignupWizardController', TwitterSignupWizardController);

    TwitterSignupWizardController.$inject = ['$stateParams', '$state', '$log', 'AuthService','UserService'];

    function TwitterSignupWizardController($stateParams, $state, $log, authService, userService) {
        $log.debug("signup wizard twitter");
        var vm = this;
        vm.user = authService.m.user;
        vm.save = save;

        function save() {
            $log.debug("signup wizard -> save twitter");
            authService.save().then(function(data){
                $state.go("user.search");
            });
        }
    }
})();
