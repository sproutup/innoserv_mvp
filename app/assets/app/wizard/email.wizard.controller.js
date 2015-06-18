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

        $scope.save = function() {
            $log.debug("signup wizard -> save email");
            authService.save().then(function(data){
                $state.go("user.search");
            });
        };
    }
})();
