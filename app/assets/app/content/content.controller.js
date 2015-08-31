(function () {
    'use strict';

angular
    .module('sproutupApp')
    .controller('ContentController', ContentController);

ContentController.$inject = ['$stateParams', '$state', 'FeedService', 'AuthService', '$rootScope', '$scope', 'MyTrialProductsService', 'PostService'];

function ContentController($stateParams, $state, FeedService, AuthService, $rootScope, $scope, MyTrialProductsService, postService) {
    var vm = this;
    vm.content = [];
    var local = {};
    local.urlify = urlify;

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

    vm.content = FeedService.buzzAll().query(function() {
        for (var c = 0; c < vm.content.length; c++) {
            vm.content[c].htmlBody = urlify(vm.content[c].body);
        }
    });

    vm.busy = false;
    var position = 10;

    vm.loadMore = function() {
        vm.busy = true;
        var more = [];
        more = FeedService.buzzAll().query({
            start: position
        }, function() {
            for (var a = 0; a < more.length; a++) {
                more[a].htmlBody = urlify(more[a].body);
                vm.content.push(more[a]);
                if ((a + 1) === more.length) {
                    vm.busy = false;
                }

            }
            position += 10;
        });
    };

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
            var Post = postService.post();
            var item = new Post();
            item.body = vm.enteredBody;
            item.product_id = vm.selectedProduct;
            item.$save(function(res) {
                res.htmlBody = urlify(res.body);
                vm.content.unshift(res);
                vm.enteredBody = '';
                vm.selectedProduct = null;
                vm.productErrorMsg = false;
                vm.textErrorMsg = false;
            }, function(err) {
                console.log(err);
            });
        }
    };

    function urlify(text) {
        var urlRegex = /(https?:\/\/[^\s]+)/g;
        return text.replace(urlRegex, function(url) {
            var displayedUrl;
            if (url.length > 50) {
                displayedUrl = url.substring(0, 50);
                displayedUrl += '...';
            } else {
                displayedUrl = url;
            }
            return '<a href="' + url + '">' + displayedUrl + '</a>';
        });
    }
}

})();