(function () {
    'use strict';

    angular
        .module('sproutupApp')
        .controller('TrialController', TrialController);

    TrialController.$inject = ['$q', '$rootScope', '$stateParams', '$scope', '$state', '$log', 'AuthService', '$location', '$window', 'TrialService', 'ProductService', '$cookieStore', 'OAuthService', 'CampaignService'];

    function TrialController($q, $rootScope, $stateParams, $scope, $state, $log, authService, $location, $window, TrialService, ProductService, $cookieStore, oauth, CampaignService) {
        $log.debug("entered trial request");
        $log.debug("slug = " + $stateParams.slug);

        var vm = this;
        vm.test = 'yo';

        vm.submit = submit;
        vm.cancel = cancel;
        vm.finish = finish;
        vm.form = {};
        vm.request = {};
        vm.trial = {};
        vm.user = {};
        vm.product = {};
        vm.ready = authService.ready;
        // Check for whether user can place another trial, ng-show (temporary fix, need rejection.html)
        vm.trialSuccess = false;
        vm.connected = connected;
        vm.findOne = findOne;
        $scope.oauth = oauth;
        vm.networks = [];


        $scope.$watch('oauth.socialMediaChecked', function (value) {
            if (value === true) {
                // we double check for a full network during trial request in analytics controller as well
                if (vm.networks.length === 6) {
                    $state.go('user.trial.request', $stateParams);
                }
                vm.socialMediaChecked = true;
            }
        });

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
            vm.user = authService.m.user;
            oauth.listNetwork(vm.user.id).then(function(data){
                if (data.length < 1) {
                    $cookieStore.put('disconnectedUser', true);
                }
                oauth.networks = data;
                vm.networks = data;
                oauth.socialMediaChecked = true;
            });

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
            var MyTrial = TrialService.myTrials();
            var item = new MyTrial(vm.request);
            item.$save(function(data) {
                // Currently checking on data.id existence. Can't return empty data
                if (typeof data.id !== 'undefined') {
                    vm.trialSuccess = true;
                    authService.addTrial(data);
                }
                $state.go("user.trial.confirmation");
            }, function(err) {
                // handle err here
            });
        }

        function connected() {
            $cookieStore.remove('disconnectedUser');
            console.log('in connected');
            $state.go('user.trial.request', $stateParams);
        }

        function finish() {
            console.log('in finish');
            $state.go("user.product.detail.buzz", { slug: $stateParams.slug });
        }

        function findOne() {
          CampaignService.campaign().get({
            campaignId: $stateParams.campaignId
          }).$promise.then(
            function(data) {
              vm.campaign = data;
            },
            function(error) {
              console.log(error);
            }
          );
        }
    }
})();
