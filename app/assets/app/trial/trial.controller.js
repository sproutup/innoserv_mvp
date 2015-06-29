(function () {
    'use strict';

    angular
        .module('sproutupApp')
        .controller('TrialController', TrialController);

    TrialController.$inject = ['$q', '$rootScope', '$stateParams', '$state', '$log', 'AuthService', '$location', '$window', 'TrialService', 'ProductService'];

    function TrialController($q, $rootScope, $stateParams, $state, $log, authService, $location, $window, TrialService, ProductService) {
        $log.debug("entered trial request");
        $log.debug("slug = " + $stateParams.slug);

        var vm = this;

        vm.submit = submit;
        vm.cancel = cancel;
        vm.finish = finish;
        vm.form = {};
        vm.request = {};
        vm.trial = {};
        vm.user = {};
        vm.product = {};
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
                    init();
                }
            }
        }

        function init() {
            vm.request.address = authService.m.user.address;
            vm.request.phone = authService.m.user.phone;
            vm.request.product_slug = $stateParams.slug;
            ProductService.get({slug: $stateParams.slug}).$promise.then(
                function(data) {
                    // success
                    vm.product = data;
                },
                function(error) {
                    // error handler
                }
            );
        }

        function cancel(){
            console.log("## cancel", new Date());
            $state.go("user.product.detail.about", { slug: $stateParams.slug });
        }

        function submit(){
            console.log("## submit", vm.request);

            var newTrial = new TrialService();
            angular.extend(newTrial, vm.request);
            newTrial.$save(function(data){
                authService.addTrial(data);
                $state.go("user.trial.confirmation");
            },
            function(err){
                //todo add error handling
            });


        }

        function finish() {
            $state.go("user.product.detail.about", { slug: $stateParams.slug });
        }
    }
})();
