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

    vm.postCount = 0;
    vm.addContent = function() {
        if (vm.postCount !== 1) {
            if (!vm.selectedProduct) {
                vm.productErrorMsg = true;
            } else if (!vm.enteredBody) {
                vm.productErrorMsg = false;
                vm.textErrorMsg = true;
            } else {
                var Post = postService.post();
                var item = new Post();
                vm.postCount = 1;
                item.body = vm.enteredBody;
                item.product_id = vm.selectedProduct;

                var postButton = document.getElementsByClassName('content-post-button');
                var spinner = new Spinner(opts).spin(postButton[0]);
                setTimeout(function() {
                    item.$save(function(res) {
                        res.htmlBody = urlify(res.body);
                        vm.content.unshift(res);
                        vm.enteredBody = '';
                        vm.selectedProduct = null;
                        vm.productErrorMsg = false;
                        vm.textErrorMsg = false;
                        vm.postCount = 0;
                        var spinnerToRemove = document.getElementsByClassName('spinner');
                        spinnerToRemove[0].remove();
                    }, function(err) {
                        console.log(err);
                        vm.postCount = 0;
                        var spinnerToRemove = document.getElementsByClassName('spinner');
                        spinnerToRemove[0].remove();
                    });
                }, 2000);

            }
        } else {
            console.log('already posted');
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

    // logic for a spinner after the save—should be moved to a directive 
    var opts = {
          lines: 8, // The number of lines to draw
          length: 16, // The length of each line
          width: 23, // The line thickness
          radius: 42, // The radius of the inner circle
          scale: 0.13, // Scales overall size of the spinner
          corners: 1, // Corner roundness (0..1)
          color: 'white', // #rgb or #rrggbb or array of colors
          opacity: 0.25, // Opacity of the lines
          rotate: 0, // The rotation offset
          direction: -1, // 1: clockwise, -1: counterclockwise
          speed: 0.8, // Rounds per second
          trail: 60, // Afterglow percentage
          fps: 20, // Frames per second when using setTimeout() as a fallback for CSS
          zIndex: 2e9, // The z-index (defaults to 2000000000)
          className: 'spinner', // The CSS class to assign to the spinner
          top: '50%', // Top position relative to parent
          left: '50%', // Left position relative to parent
          shadow: false,// Whether to render a shadow
          hwaccel: false, // Whether to use hardware acceleration
          position: 'absolute' // Element positioning
    };
}

})();