(function () {
    'use strict';

    angular
        .module('sproutupApp')
        .controller('TrialController', TrialController);

    TrialController.$inject = ['$q', '$rootScope', '$stateParams', '$state', '$log', 'AuthService', '$location', '$window'];

    function TrialController($q, $rootScope, $stateParams, $state, $log, authService, $location, $window) {
        $log.debug("entered trial request");
        var vm = this;

        vm.submit = submit;
        vm.cancel = cancel;
        vm.finish = finish;
        vm.form = {};
        vm.request = {};
        vm.trial = {};
        vm.user = {};
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
                    reset();
                }
            }
        }

        function reset() {
            vm.trial.address = authService.m.user.address;
            vm.trial.phone = authService.m.user.phone;
        }

        function cancel(){
            console.log("## cancel", new Date());
            $state.go("user.search");
        }

        function submit(){
            console.log("## submit", vm.request);

            // reset error message
//            vm.form.error = "";

//            var promise = authService.login(vm.form);
//
//            promise.then(
//                function(data){
//                    wizard();
//                },
//                function(error){
//                    $log.info('Login failed: ' + new Date());
//                    vm.form.error = true;
//                }
//            );
            $state.go("user.trial.confirmation");
        }

        function finish() {
            $state.go("user.search");
        }
    }
})();
