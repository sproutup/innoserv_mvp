(function () {
    'use strict';

    angular
        .module('sproutupApp')
        .controller('SignupWizardController', SignupWizardController);

    SignupWizardController.$inject = ['$rootScope', '$stateParams', '$state', '$log', 'AuthService', 'AnalyticsService', 'OAuthService', '$scope', '$cookieStore'];

    function SignupWizardController($rootScope, $stateParams, $state, $log, authService, analyticsService, oauth, $scope, $cookieStore) {
        $log.debug("signup wizard");

        var vm = this;
        vm.save = save;
        vm.user = authService.m.user;
        vm.ready = authService.ready;
        vm.finish = finish;
        $scope.oauth = oauth;
        
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
            }
        }

        function init() {
            vm.user = angular.copy(authService.m.user);
            oauth.listNetwork(vm.user.id).then(function(data){
                if (data.length < 1) {
                    $cookieStore.put('disconnectedUser', true);
                }
                vm.networks = data;
                vm.disconnectedUser = $cookieStore.get('disconnectedUser');
                vm.socialMediaChecked = true;
                wizard();
            });
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
                socialConnection();
            }
        }

        function socialConnection(){
            if (vm.disconnectedUser) {
                console.log("social connection missing");
                $state.go("user.wizard.socialConnection");
            } else {
                console.log("social connection ok");
                // end of wizard
                finish();
            }
        }

        function finish(){
            $cookieStore.remove('disconnectedUser');
            $log.debug("wizard done");
            authService.redirect("user.search");
        }
    }
})();
