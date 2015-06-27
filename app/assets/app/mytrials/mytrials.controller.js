(function () {
    'use strict';

    angular
        .module('sproutupApp')
        .controller('MyTrialController', MyTrialController);

    MyTrialController.$inject = ['$q', '$rootScope', '$stateParams', '$state', '$log', 'AuthService', '$location', '$window', 'TrialService', 'ContentService'];

    function MyTrialController($q, $rootScope, $stateParams, $state, $log, authService, $location, $window, TrialService, ContentService) {
        $log.debug("entered my trials");

        var vm = this;

//        vm.submit = submit;
        vm.cancel = cancel;
        vm.finish = finish;
        vm.addContent = addContent;
        vm.deleteContent = deleteContent;
        vm.form = {};
        vm.trial = {};
        vm.user = authService.m.user;
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
            if(vm.user.trials === undefined){
                vm.current = {};
            }
            else{
                vm.current = vm.user.trials.filter(function (item) {
                    return item.active === true;
                });
                vm.past = vm.user.trials.filter(function (item) {
                    return item.active === false;
                });
            }
        }

        function cancel(){
            console.log("## cancel", new Date());
            $state.go("user.product.detail.about", { slug: $stateParams.slug });
        }

        function addContent(trial){
            console.log("## addContent", trial);

            var item = new ContentService();
            angular.extend(item, trial.form);
            item.product_trial_id = trial.id;
            item.$save(function(content){
                console.log("saved content");
                $rootScope.$broadcast('alert:success', {
                    message: 'Saved'
                });
                trial.isContentFormOpen = false;
                trial.content.push(content);
                console.log("## content: ", trial.content);
            });


            //$state.go("user.trial.confirmation");
        }

        function deleteContent(id, trial){
            console.log("## deleteContent", id);
            var item = new ContentService();
            item.$delete({id: id},
                function(data){
                    console.log("delete success");
                    var index = trial.content.findIndex(function(element, index, array) {
                        return element.id == id;
                    });
                    // remove item from list
                    trial.content.splice(index, 1);
                    $rootScope.$broadcast('alert:success', {
                        message: 'Deleted'
                    });
                },
                function(err){
                    console.log("delete error");
                }
            );
        }

        function finish() {
            $state.go("user.product.detail.about", { slug: $stateParams.slug });
        }
    }
})();
