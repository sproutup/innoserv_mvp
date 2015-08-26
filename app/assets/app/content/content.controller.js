(function () {
    'use strict';

angular
    .module('sproutupApp')
    .controller('ContentController', ContentController);

ContentController.$inject = ['$stateParams', '$state', 'FeedService', 'AuthService', '$rootScope', '$scope', 'MyTrialProductsService', 'ContentService'];

function ContentController($stateParams, $state, FeedService, AuthService, $rootScope, $scope, MyTrialProductsService, ContentService) {
    var vm = this;
    vm.content = [];

    activate();

    function activate() {
        if(!AuthService.ready()){
            var unbindWatch = $rootScope.$watch(AuthService.loggedIn, function (value) {
                if ( value === true ) {
                  unbindWatch();
                  activate();
                }
            });
        }
        else {
            init();
        }
    }

    function init() {
        vm.user = angular.copy(AuthService.m.user);
    }

    vm.content = FeedService.query();
    console.log(vm.content);
    vm.busy = false;
    var position = 11;

    vm.loadMore = function() {
        vm.busy = true;
        console.log('more');
        var more = [];
        more = FeedService.query({
            start: position
        }, function() {
            for (var a = 0; a < more.length; a++) {
                vm.content.push(more[a]);
                if ((a + 1) === more.length) {
                    vm.busy = false;
                }
            }
            position += 11;
        });
    };


    // var productService = new MyTrialProductsService();

    // productService.$query(function(res) {
    //     console.log(res);
    // });

    vm.myTrialProducts = [];
    vm.myTrialProducts = MyTrialProductsService.query();

    vm.displayLinkInput = function() {
        vm.enteringLink = true;
        vm.enteringPhoto = vm.enteringVideo = false;
    };

    vm.displayLinkPhoto = function() {
        vm.enteringPhoto = true;
        vm.enteringVideo = vm.enteringLink = false;
    };

    vm.displayLinkVideo = function() {
        vm.enteringVideo = true;
        vm.enteringPhoto = vm.enteringLink = false;
    };

    vm.selectTrialProduct = function(p) {
        vm.selectedProduct = p.id;
    };

    vm.addContent = function() {
        if (!vm.selectedProduct) {
            vm.productErrorMsg = true;
        } else if (!vm.enteredBody) {
            vm.productErrorMsg = false;
            vm.textErrorMsg = true;
        } else {
            var Content = new ContentService.content();
            var item = new Content();
            item.product_id = vm.selectedProduct;
            item.url = vm.enteredBody;
            item.$save(function(res) {
                console.log(res);
                $rootScope.$broadcast('alert:success', {
                    message: 'Saved'
                });
                vm.enteredBody = '';
                vm.selectedProduct = null;
                vm.productErrorMsg = false;
                vm.textErrorMsg = false;
            }, function(err) {
                $rootScope.$broadcast('alert:error', {
                    message: 'Fail'
                });
            });
        }
    };

}

})();