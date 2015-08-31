(function () {
    'use strict';

angular
    .module('sproutupApp')
    .controller('BuzzController', BuzzController);

BuzzController.$inject = ['$stateParams', '$state', 'FeedService', 'AuthService', '$rootScope', '$scope', 'MyTrialProductsService', 'PostService'];

function BuzzController($stateParams, $state, FeedService, AuthService, $rootScope, $scope, MyTrialProductsService, postService) {
    var vm = this;
    vm.content = [];
    vm.loadInit = loadInit;
    vm.slug = $stateParams.slug;

    activate();

    function activate() {
        if(!AuthService.ready()){
            var unbindWatch = $rootScope.$watch(AuthService.loggedIn, function (value) {
                unbindWatch();
                activate();
            });
        }
        else {
            init();
        }
    }

    function init() {
        loadInit();
    }

    function loadInit(){
        console.log("init load");
        vm.content = FeedService.buzzProduct().query({
            slug: vm.slug,
            start: 0
        }, function() {
            for (var c = 0; c < vm.content.length; c++) {
                vm.content[c].htmlBody = urlify(vm.content[c].body);
            }
        });
        vm.busy = false;
        var position = 10;
    }

    vm.loadMore = function(productId) {
        vm.busy = true;
        console.log('more');
        var more = [];
        more = FeedService.buzzProduct().query({
            slug: vm.slug,
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