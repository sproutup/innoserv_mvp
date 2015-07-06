(function () {
    'use strict';

    angular
        .module('sproutupApp')
        .controller('SignupWizardController', SignupWizardController);

    SignupWizardController.$inject = ['$rootScope', '$stateParams', '$state', '$log', 'AuthService'];

    function SignupWizardController($rootScope, $stateParams, $state, $log, authService) {
        $log.debug("signup wizard");

        var vm = this;
        vm.save = save;
        vm.user = authService.m.user;
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
                console.log("wizard: auth service ready - ", authService.m.user.email );
                init();
                wizard();
            }
        }

        function init() {
            vm.user = angular.copy(authService.m.user);
        }


        function wizard(){
            console.log("wizard starting");
            email();
        }

        function save() {
            $log.debug("signup wizard -> save twitter");
            authService.save(vm.user).then(function(data){
                // end of wizard
                wizard();
            });
        }

        function email(){
            if(vm.user.email === null || vm.user.email.length < 1){
                $log.debug("email missing");
                $state.go("user.wizard.email");
            }
            else{
                $log.debug("email ok");
                twitter();
            }
        }

        function twitter(){
            if(vm.user.urlTwitter === null || vm.user.urlTwitter.length < 1){
                $log.debug("twitter url missing");
                $state.go("user.wizard.twitter");
            }
            else{
                $log.debug("twitter ok");
                // end of wizard
                finish();
            }
        }

        function finish(){
            $log.debug("wizard done");
            authService.redirect("user.search");
        }
    }
})();
