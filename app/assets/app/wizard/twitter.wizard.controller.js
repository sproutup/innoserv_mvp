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

        activate();

        function activate() {
            if(vm.user.urlTwitter === null || vm.user.urlTwitter.length < 1){
                $log.debug("twitter url missing");
            }
            else{
                // end of wizard
                $state.go("user.search");
            }
        }

        function save() {
            $log.debug("signup wizard -> save twitter");
            authService.save().then(function(data){
                // end of wizard
                $state.go("user.search");
            });
        }
    }
})();
