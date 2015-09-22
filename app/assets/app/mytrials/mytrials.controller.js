(function () {
    'use strict';

    angular
        .module('sproutupApp')
        .controller('MyTrialController', MyTrialController);

    MyTrialController.$inject = ['$q', '$rootScope', '$stateParams', '$state', '$log', 'AuthService', '$location', '$window', 'ContentService', 'OpenGraphService'];

    function MyTrialController($q, $rootScope, $stateParams, $state, $log, authService, $location, $window, ContentService, OpenGraphService) {
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
                    return (item.status >= 0 && item.status < 4);
                });
                vm.past = vm.user.trials.filter(function (item) {
                    return item.status === 4;
                });
                vm.cancelled = vm.user.trials.filter(function (item) {
                    return item.status < 0;
                });
            }
        }

        function cancel(){
            console.log("## cancel", new Date());
            $state.go("user.product.detail.buzz", { slug: $stateParams.slug });
        }

        function addContent(trial){
            console.log('yo');
            console.log("## addContent", trial);

            // var Comment = CommentService.comment('models.content', $scope.vm.id);
            // var newComment = new Comment();

            var Content = ContentService.content();
            var item = new Content();
            angular.extend(item, trial.form);
            item.product_trial_id = trial.id;

            var og = new OpenGraphService();
            angular.extend(og, {url: trial.form.url});
            og.$save(function(openGraph) {
                item.open_graph_id = openGraph.id;
                saveContent(item);
            }, function(err) {
                saveContent(item);
            });

            function saveContent(item) {
                item.$save(function(content){
                    console.log("saved content");
                    $rootScope.$broadcast('alert:success', {
                        message: 'Saved'
                    });
                    trial.isContentFormOpen = false;
                    trial.content.push(content);
                    console.log("## content: ", trial.content);
                    // reset input form
                    trial.form.url = "";
                });
            }


            //$state.go("user.trial.confirmation");
        }

        function findIndex(key, array){
            for (var i=0; i < array.length; i++) {
                if (array[i].id === key) {
                    return i;
                }
            }
            return -1;
        }

        function deleteContent(content, trial){
            console.log("## deleteContent", content.id);
            var item = new ContentService.content();
            item.$delete({id: content.id},
                function(data){
                    if (typeof content.openGraph !== 'undefined') {
                        var og = new OpenGraphService();
                        og.$delete({id: content.openGraph.id}, function(data) {
                            console.log("deleted openGraph for content");
                        });
                    }
                    console.log("delete success");
                    var index = findIndex(content.id, trial.content);
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
            $state.go("user.product.detail.buzz", { slug: $stateParams.slug });
        }
    }
})();
