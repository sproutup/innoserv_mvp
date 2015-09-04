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
                if (vm.content[c].content) {
                    optimizeContentDisplay(vm.content[c]);
                }
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
                optimizeContentDisplay(more[a]);
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
                optimizeContentDisplay(res);
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

    function optimizeContentDisplay(contentObject) {
        displayYoutubeVideo(contentObject.content);
        displayTweet(contentObject.content);
        contentObject.body = urlify(contentObject.body);
    }

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
            console.log(url);
            return '<a href="' + url + '">' + displayedUrl + '</a>';
        });
    }

    function contains(str, substr) {
        return (str.indexOf(substr) > -1);
    }

    var youtubeRegexp = /https?:\/\/(?:[0-9A-Z-]+\.)?(?:youtu\.be\/|youtube(?:-nocookie)?\.com\S*[^\w\s-])([\w-]{11})(?=[^\w-]|$)(?![?=&+%\w.-]*(?:['"][^<>]*>|<\/a>))[?=&+%\w.-]*/ig;
    var timeRegexp = /t=(\d+)[ms]?(\d+)?s?/;

    function displayYoutubeVideo(content) {
        console.log(content);
        var match = content.url.match(/https:\/\/www.youtube.com\/watch/g);
        if (match) {
            var id = content.url.replace(youtubeRegexp, '$1');

            if (contains(id, ';')) {
                var pieces = id.split(';');

                if (contains(pieces[1], '%')) {
                    // links like this:
                    // "http://www.youtube.com/attribution_link?a=pxa6goHqzaA&amp;u=%2Fwatch%3Fv%3DdPdgx30w9sU%26feature%3Dshare"
                    // have the real query string URI encoded behind a ';'.
                    // at this point, `id is 'pxa6goHqzaA;u=%2Fwatch%3Fv%3DdPdgx30w9sU%26feature%3Dshare'
                    var uriComponent = decodeURIComponent(id.split(';')[1]);
                    id = ('http://youtube.com' + uriComponent)
                            .replace(youtubeRegexp, '$1');
                } else {
                    // https://www.youtube.com/watch?v=VbNF9X1waSc&amp;feature=youtu.be
                    // `id` looks like 'VbNF9X1waSc;feature=youtu.be' currently.
                    // strip the ';feature=youtu.be'
                    id = pieces[0];
                }
            } else if (contains(id, '#')) {
                // id might look like '93LvTKF_jW0#t=1'
                // and we want '93LvTKF_jW0'
                id = id.split('#')[0];
            }

            if (id) {
                content.youtube = "https://www.youtube.com/embed/" + id;
            }
        }
    }

    function displayTweet(content) {
        var match = content.url.match(/twitter.com\/\w+\/status\/\w+/g);
        if (match) {
            var statusIndex = match[0].indexOf('/status/');
            var twitterUsername = match[0].substring(12, statusIndex);
            var onTwitterIndex = content.title.indexOf('on Twitter');
            var twitterName = content.title.substring(0, onTwitterIndex);
            var tweetBody = content.description.substring(1, (content.description.length - 1));
            var mediaIndex = content.image.indexOf('/media/');
            content.tweet = {
                twitterUsername: twitterUsername,
                twitterName: twitterName,
                tweetBody: tweetBody
            };
            if (mediaIndex > 0) {
                content.tweet.picture = content.image;
            }
        }
    }
}

})();