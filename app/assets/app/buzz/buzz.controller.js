(function () {
    'use strict';

angular
    .module('sproutupApp')
    .controller('BuzzController', BuzzController);

// This controller contains logic for main buzz page as well as individual product buzz pages
// Functions loadInit and loadMore have seperate queries for when a slug is present (product buzz page) vs. when there is no slug (main buzz page)

BuzzController.$inject = ['$stateParams', '$state', 'FeedService', 'AuthService', '$rootScope', '$scope', 'MyTrialProductsService', 'PostService', '$timeout'];

function BuzzController($stateParams, $state, FeedService, AuthService, $rootScope, $scope, MyTrialProductsService, postService, $timeout) {
    var vm = this;
    vm.content = [];
    vm.myTrialProducts = [];
    vm.myTrialProducts = MyTrialProductsService.query();
    vm.loadInit = loadInit;
    vm.slug = $stateParams.slug;
    vm.addContent = addContent;
    vm.displayLinkInput = displayLinkInput;
    vm.displayLinkPhoto = displayLinkPhoto;
    vm.displayLinkVideo = displayLinkVideo;
    vm.selectTrialProduct = selectTrialProduct;
    vm.busy = true;
    var local = {};
    local.urlify = urlify;
    local.displayYoutubeVideo = displayYoutubeVideo;
    local.displayTweet = displayTweet;
    local.optimizeContentDisplay = local.optimizeContentDisplay;
    var position = 0;

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
        vm.user = angular.copy(AuthService.m.user);
        loadInit();
    }

    function loadInit(){
        if (vm.slug) {
            vm.content = FeedService.buzzProduct().query({
                slug: vm.slug,
                start: 0
            }, function() {
                for (var c = 0; c < vm.content.length; c++) {
                    vm.content[c].body = urlify(vm.content[c].body);
                    if (vm.content[c].content) {
                        optimizeContentDisplay(vm.content[c]);
                    }
                    if (vm.content[c].comments) {
                        for (var d = 0; d < vm.content[c].comments.data.length; d ++) {
                            vm.content[c].comments.data[d].body = urlify(vm.content[c].comments.data[d].body);
                        }
                    }
                }
                $timeout(function(){vm.busy = false;}, 1000);
                position += 10;
                vm.init = true;
            });
        } else {
            vm.content = FeedService.buzzAll().query(function() {
                for (var c = 0; c < vm.content.length; c++) {
                    vm.content[c].body = urlify(vm.content[c].body);
                    // display youtube videos and tweets
                    if (vm.content[c].content) {
                        displayYoutubeVideo(vm.content[c].content);
                        displayTweet(vm.content[c].content);
                    }
                    // urlify comments
                    if (vm.content[c].comments && vm.content[c].comments.data.length > 0) {
                        for (var d = 0; d < vm.content[c].comments.data.length; d++) {
                            vm.content[c].comments.data[d].body = urlify(vm.content[c].comments.data[d].body);
                        }
                    }
                }
                vm.init = true;
                $timeout(function(){vm.busy = false;}, 1000);
                position += 10;
            });
        }

    }

    vm.loadMore = function(productId) {
        vm.busy = true;
        var more = [];
        if (vm.slug) {
            more = FeedService.buzzProduct().query({
                slug: vm.slug,
                start: position
            }, function() {
                for (var a = 0; a < more.length; a++) {
                    if (more[a].content) {
                        optimizeContentDisplay(more[a]);
                    }
                    if (more[a].comments) {
                        for (var c = 0; c < more[a].comments.data.length; c++) {
                            more[a].comments.data[c].body = urlify(more[a].comments.data[c].body);
                        } 
                    }
                    more[a].body = urlify(more[a].body);
                    vm.content.push(more[a]);
                }
                $timeout(function(){vm.busy = false;}, 1000);
                position += 10;
            });
        } else {
            more = FeedService.buzzAll().query({
                start: position
            }, function() {
                for (var a = 0; a < more.length; a++) {
                    if (more[a].content) {
                        optimizeContentDisplay(more[a]);
                    }
                    more[a].body = urlify(more[a].body);
                    if (more[a].comments && more[a].comments.data.length > 0) {
                        for (var c = 0; c < more[a].comments.data.length; c++) {
                            more[a].comments.data[c].body = urlify(more[a].comments.data[c].body);
                        }
                    }
                    vm.content.push(more[a]);
                }
                $timeout(function(){vm.busy = false;}, 1000);
                position += 10;
            });
        }
    };

    vm.postCount = 0;
    function addContent() {
        if (vm.postCount !== 1) {
            if (!vm.selectedProduct) {
                vm.productErrorMsg = true;
            } else if (!vm.enteredBody) {
                vm.productErrorMsg = false;
                vm.textErrorMsg = true;
            } else {
                var Post = postService.post();
                var item = new Post();
                var postButton = document.getElementsByClassName('content-post-button');
                var spinner = new Spinner(opts).spin(postButton[0]);
                vm.postCount = 1;
                item.body = vm.enteredBody;
                item.product_id = vm.selectedProduct;
                item.$save(function(res) {
                    res.body = urlify(res.body);
                    vm.content.unshift(res);
                    vm.enteredBody = '';
                    vm.selectedProduct = null;
                    vm.productErrorMsg = false;
                    vm.textErrorMsg = false;
                    vm.postCount = 0;
                    if (res.content) {
                        displayYoutubeVideo(res.content);
                        displayTweet(res.content);
                    }
                    var spinnerToRemove = document.getElementsByClassName('spinner');
                    spinnerToRemove[0].remove();
                }, function(err) {
                    console.log(err);
                    vm.postCount = 0;
                    var spinnerToRemove = document.getElementsByClassName('spinner');
                    spinnerToRemove[0].remove();
                });
            }
        }
    }

    function optimizeContentDisplay(contentObject) {
        displayYoutubeVideo(contentObject.content);
        displayTweet(contentObject.content);
    }

    function urlify(text) {
        var urlRegex = /(https?:\/\/[^\s]+)/g;
        return text.replace(urlRegex, function(url) {
            var displayedUrl;
            if (url.length > 40) {
                displayedUrl = url.substring(0, 40);
                displayedUrl += '...';
            } else {
                displayedUrl = url;
            }
            return '<a href="' + url + '" target="blank">' + displayedUrl + '</a>';
        });
    }

    function contains(str, substr) {
        return (str.indexOf(substr) > -1);
    }

    var youtubeRegexp = /https?:\/\/(?:[0-9A-Z-]+\.)?(?:youtu\.be\/|youtube(?:-nocookie)?\.com\S*[^\w\s-])([\w-]{11})(?=[^\w-]|$)(?![?=&+%\w.-]*(?:['"][^<>]*>|<\/a>))[?=&+%\w.-]*/ig;
    var timeRegexp = /t=(\d+)[ms]?(\d+)?s?/;

    function displayYoutubeVideo(content) {
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

    function displayLinkInput() {
        vm.enteringLink = true;
        vm.enteringPhoto = vm.enteringVideo = false;
    }

    function displayLinkPhoto() {
        vm.enteringPhoto = true;
        vm.enteringVideo = vm.enteringLink = false;
    }

    function displayLinkVideo() {
        vm.enteringVideo = true;
        vm.enteringPhoto = vm.enteringLink = false;
    }

    function selectTrialProduct(p) {
        vm.selectedProduct = p.id;
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