'use strict';

angular.module('sproutupApp').directive('upTwitterTweet', ['TwitterService',
    function(twitterService){
        return{
            templateUrl: 'assets/templates/up-twitter-tweet.html',
            restrict: "E",
            scope:{
                productId: "="
            },
            link: function(scope, element, attrs){
                twitterService.search(scope.productId).then(
                    function(data){
                        var arrayLength = data.statuses.length;
                        for (var i = 0; i < arrayLength; i++) {
                            //Do something
                            element.append("<div id='tweet"+i+"'</div>")
                            twttr.widgets.createTweet(
                                data.statuses[i].id_str,
                                document.getElementById('tweet'+i),
                                {
//                                    theme: 'dark'
                                }).then(function (el) {
                                    console.log("Embedded a tweet.")
                                });
                        }
                        data.statuses
                    },
                    function(error){

                    }
                );
            }
        }
    }
]);


angular.module('sproutupApp').directive('upTwitterTimeline', ['TwitterService',
    function(twitterService){
        return{
            templateUrl: 'assets/templates/up-twitter-timeline.html',
            restrict: "E",
            scope:{
                productId: "="
            },
            link: function(scope, element, attrs){
                twitterService.show(scope.productId).then(
                    function(data){
                        twttr.widgets.createTimeline(
                            '585242050129440768',
                            document.getElementById('timeline'),
                            {
                                width: '604',
                                height: '700',
                                userId: data.id_str,
                                related: 'twitterdev,twitterapi'
                            }).then(function (el) {
                                console.log("Embedded a timeline.")
                            });
                    },
                    function(error){

                    }
                );
            }
        }
    }
]);

// <div class="fb-post" data-href="https://www.facebook.com/belledstech/posts/360314004151782" data-width="500"><div class="fb-xfbml-parse-ignore"><blockquote cite="https://www.facebook.com/belledstech/posts/360314004151782"><p>Awesome interface for Nook to control the Q developed by Ronald!</p>Posted by <a href="https://www.facebook.com/belledstech">Belleds</a> on <a href="https://www.facebook.com/belledstech/posts/360314004151782">Tuesday, March 17, 2015</a></blockquote></div></div>

angular.module('sproutupApp').directive ('upFacebookPost', ['Facebook', 'FacebookService', '$log', '$parse',
    function(facebook, facebookService, $log, $parse) {
        return{
            template: "<div ng-repeat='post in posts | limitTo:3' class='fb-post' " +
            "data-href='https://www.facebook.com/belledstech/posts/{{post.post_id}}'>{{post.post_id}}</div>",
            restrict: 'E',
            scope: {
                product: "="
            },
            link: function(scope, element, attrs){
                scope.$watch(function() {
                    // This is for convenience, to notify if Facebook is loaded and ready to go.
                    return facebook.isReady() && scope.product != undefined;
                }, function(newVal) {
                    if(newVal){
                        $log.debug("facebook > ready ", newVal);
                        // You might want to use this to disable/show/hide buttons and else
                        scope.facebookReady = true;

                        $log.debug("facebook > productId = ", scope.product);

                        facebookService.get(scope.product).then(
                            function(data){
                                $log.debug("facebook > received post data");
                                data.data.forEach(
                                    function(post){
                                        post.post_id = post.id.split("_")[1];
                                    }
                                );
                                scope.posts = data.data;
                                facebook.parseXFBML();
                            },
                            function(error){

                            }
                        );
                    }
                    else{
                        $log.debug("facebook > not ready ", newVal);
                    }

                    //facebook.login(function(){
                    //
                    //    facebook.api('/me', function(response) {
                    //        console.log(JSON.stringify(response));
                    //    });
                    //
                    //}, {scope: 'publish_actions'});

                    //facebook.api('/me/feed', 'post', {message: 'Hello, world!'});

                });

                scope.login = function() {
                    // From now on you can use the Facebook service just as Facebook api says
                    facebook.login(function(response) {
                        // Do something with response.
                    });
                };
                //FB.login(function(){}, {scope: 'read_stream'});
                //read_stream
                //facebook.api();
                //facebook.login();

            }
        };
    }
]);

/*
    Search icon directive
    Listens for stage changes and sets the search icon accordingly.
    After search return to previous state.
 */
angular.module('sproutupApp').directive('upSearchIcon', [ '$rootScope', '$log', '$state',
    function($rootScope, $log, $state){
        return{
            restrict: 'A',
            scope: {},
            link: function(scope, element, attrs) {
                var previous_state = "home";
                var state =  $state.current.name;

                update();

                function update(){
                    if(state=="search"){
                        element.find("i").addClass('active');
                    }
                    else{
                        element.find("i").removeClass('active');
                    }
                }

                element.bind('click', function() {
                    $log.debug("search - click()" + state + " " + previous_state);
                    if(state=="search") {
                        $state.go(previous_state);
                    }
                    else{
                        $state.go("search");
                    }
                });

                scope.$on('$stateChangeSuccess',
                    function (ev, to, toParams, from, fromParams) {
                        //assign the "from" parameter to something
                        console.log('search state change ' + from.name);
                        state =  $state.current.name;
                        update();
                    }
                );
            }
        }
    }
]);

/*
  General alert message handler
  Usage:
  broadcast a message like this and it will be flashed
  $rootScope.$broadcast('alert:success', {
    message: 'insert message here'
  });
 */
angular.module('sproutupApp').directive('upAlert', ['$timeout',
    function($timeout) {
        return{
            restrict: 'EA',
            replace: true,
            template: '<div ng-show="state.show" class="col-sm-12 alert" ng-class="state.status" role="alert">{{state.message}}</div>',
            scope: {},
            link: function(scope, element, attrs){
                scope.state = {
                    message: "",
                    status: "",
                    show: false
                };

                scope.$on('alert:success', function (event, args) {
                    scope.state.message = args.message;
                    scope.state.status = "success";
                    scope.state.show = true;

                    $timeout(
                        function(){
                            scope.state.message = "";
                            scope.state.status = "";
                            scope.state.show = false;
                        },
                        2000,
                        true,
                        scope
                    );
                });
            }
        }
    }
]);

angular.module('sproutupApp').directive('upProductSuggest', ['ProductSuggestionService', '$log',
    function(productSuggestionService, $log) {
        return {
            restrict: 'E',
            templateUrl: 'assets/templates/up-product-suggest.html',
            scope: {},
            controller: function( $scope, $element, $attrs, $transclude ) {
                // Controller code goes here.
                $scope.addSuggestion = function () {
                    $log.debug("productSuggestionService > add suggestion");
                    productSuggestionService.add($scope.newSuggestion).then(
                        function(data){
                            $scope.newSuggestion.email = "";
                            $scope.newSuggestion.productName = "";
                            $scope.newSuggestion.productUrl = "";
                        }
                    );
                }
            }
        }
    }
]);

angular.module('sproutupApp').directive('upSlideable', function () {
    return {
        restrict:'AC',
        compile: function (element, attr) {
            // wrap tag
            var contents = element.html();
            element.html('<div class="slideable_content" style="margin:0 !important; padding:0 !important" >' + contents + '</div>');

            return function postLink(scope, element, attrs) {
                // default properties
                attrs.duration = (!attrs.duration) ? '0.4s' : attrs.duration;
                attrs.easing = (!attrs.easing) ? 'ease-in-out' : attrs.easing;
                element.css({
                    'overflow': 'hidden',
                    'height': '0px',
                    'transitionProperty': 'height',
                    'transitionDuration': attrs.duration,
                    'transitionTimingFunction': attrs.easing
                });
            };
        }
    };
});

angular.module('sproutupApp').directive('upSlideableToggle', ['$rootScope',
    function($rootScope) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var target, content;
                console.log("slideable toggle - init");

                attrs.expanded = false;

                element.bind('click', function() {
                    console.log("slideable toggle - click event");
                    if (!target) target = document.querySelector(attrs.upSlideableToggle);
                    if (!content) content = target.querySelector('.slideable_content');

                    if(!attrs.expanded) {
                        content.style.border = '1px solid rgba(0,0,0,0)';
                        var y = content.clientHeight;
                        content.style.border = 0;
                        target.style.height = y + 'px';
                    } else {
                        target.style.height = '0px';
                    }
                    attrs.expanded = !attrs.expanded;
                });

                $rootScope.$on('$stateChangeStart', function(event, toState, toParams, fromState, fromParams){
                    console.log("product suggest > state changed event");
                    if (!target) target = document.querySelector(attrs.upSlideableToggle);
                    if (!content) content = target.querySelector('.slideable_content');
                    if(attrs.expanded) {
                        target.style.height = '0px';
                        attrs.expanded = !attrs.expanded;
                    }
                })
            }
        };
    }
]);

angular.module('sproutupApp').directive('upLike', ['LikesService', 'AuthService', '$timeout',
    function (likesService, authService, $timeout) {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                likes: '=',
                id: '=upId',
                type: '@upType'
            },
            templateUrl: 'assets/templates/up-like.html',
            link: function (scope, element, attrs) {
                attrs.$observe('likes', function (likes) {
                    didIlikeItAlready();
                });

                scope.$watch(function () {
                        return authService.isLoggedIn();
                    },
                    function(newVal, oldVal) {
                        didIlikeItAlready();
                    }, true);

                function didIlikeItAlready() {
                    if(authService.isLoggedIn()) {
                        var userid = authService.currentUser().id;
                        if (scope.likes == undefined) {
                            return false;
                        }
                        for (var i = 0; i < scope.likes.length; i++) {
                            if (scope.likes[i].id == userid) {
                                scope.upvoted = true;
                                return true;
                            }
                        }
                    }
                    scope.upvoted = false;
                    return false;
                }

                element.on('click', function () {
                    console.log("#########################");
                    console.log("up-like > clicked id/type: " + scope.id + "/" + scope.type);
                    console.log("up-like > is-logged-in: " + authService.isLoggedIn());

                    if(!authService.isLoggedIn()){
                        scope.$emit('LoginEvent', {
                            someProp: 'Sending you an Object!' // send whatever you want
                        });
                        return;
                    }

                    if(scope.likes == undefined){
                        scope.likes = [];
                    }

                    console.log("user.id: " + authService.currentUser().id);

                    if(didIlikeItAlready()==false){
                        likesService.addLike(scope.id, scope.type, authService.currentUser().id).then(
                            function(data) {
                                console.log("liked it: " + scope.id);
                                scope.likes.push(data);
                                scope.upvoted = true;
                            }, function(reason) {
                                console.log('up-files failed: ' + reason);
                            }
                        );
                    };

                });
            }
        }
    }
]);

angular.module('sproutupApp').directive('upVideo', ['FileService', '$timeout',
    function (fileService, $timeout) {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                file: '='
            },
            templateUrl: 'assets/templates/up-video.html',
            link: function (scope, element, attrs) {
                attrs.$observe('file', function (file) {
                    if (file) {
                        console.log("up-video - file changed: ", file);
                    }
                });

                angular.element(document).ready(function () {
                    console.log('Hello World');
                    //flowplayer(function (api, root) {
                    //
                    //    api.bind("load", function () {
                    //        console.log("up-video - load");
                    //
                    //        // do something when a new video is about to be loaded
                    //
                    //    }).bind("ready", function () {
                    //        console.log("up-video - ready");
                    //
                    //        // do something when a video is loaded and ready to play
                    //
                    //    });
                    //
                    //});
                });


                $timeout(function () {
                    $timeout(function () {
                        console.log("up-video - loaded: ");
                        // This code will run after
                        // templateUrl has been loaded, cloned
                        // and transformed by directives.
                        // and properly rendered by the browser
                        var flowplr = element.find(".player");
                        var api = flowplr.flowplayer();
                    }, 0);
                }, 0);
            }
        }
    }
]);

angular.module('sproutupApp').directive('upPhoto', ['FileService',
    function (fileService) {
        return {
            restrict: 'E',
            replace: true,
            require: '^masonry',
            scope: {
                file: '='
            },
            templateUrl: 'assets/templates/up-photo.html',
            link: function (scope, element, attrs, ctrl) {
                attrs.$observe('file', function (file) {
                    if (file) {
                        console.log("up-photo - file changed: " + file.id);
                        //ctrl.reload();
                    }
                });
            }
        }
    }
]);

angular.module('sproutupApp').directive('upFiles', ['$compile', 'FileService',
    function ($compile, fileService) {
        return {
            restrict: 'E',
            replace: true,
            scope: {
            },
            templateUrl: 'assets/templates/up-files.html',
            link: function (scope, element, attrs) {
                // listen for the event in the relevant $scope
                scope.$on('fileUploadEvent', function (event, args) {
                    console.log('on fileUploadEvent - type: ' + args.data.type);
                    loadFiles();
                    //var elem = $compile( "<up-photo file='file'></up-photo>" )( scope );
                    //element.find('.masonry').prepend(elem);

                    //scope.files.unshift(args.data);
                });

                attrs.$observe('refId', function (refId) {
                    if (refId) {
                        console.log("up-files - ref-id changed: " + refId);
                        loadFiles();
                    }
                });

                function loadFiles() {
                    var promise = fileService.getAllFiles(attrs.refId, attrs.refType);

                    promise.then(function(data) {
                        console.log('up-files received data size: ' + data.length);
                        scope.files = data;
                    }, function(reason) {
                        console.log('up-files failed: ' + reason);
                    }, function(update) {
                        console.log('up-files got notification: ' + update);
                    });
                }
            }
        }
    }
]);

angular.module('sproutupApp').directive('upProfilePhotos', ['FileService',
    function (fileService) {
        return {
            restrict: 'EA',
            replace: true,
            scope: true,
            link: function (scope, element, attrs) {
                scope.fileService = fileService;
            },
            templateUrl: 'assets/templates/up-profile-photos.html'
        }
    }
]);

angular.module('sproutupApp').directive('upProfileVideos', ['FileService',
    function (fileService) {
        return {
            restrict: 'EA',
            replace: true,
            scope: true,
            link: function (scope, element, attrs) {
                scope.fileService = fileService;
            },
            templateUrl: 'assets/templates/up-profile-videos.html'
        }
    }
]);

angular.module('sproutupApp').directive('upProfileInfo', ['AuthService',
    function (authService) {
        return {
            restrict: 'E',
            scope: {

            },
            link: function (scope, element, attrs) {
                scope.user = authService.currentUser();
            },
            templateUrl: 'assets/templates/up-profile-info.html'
        }
    }
]);

angular.module('sproutupApp').directive('upProfileMenu', ['AuthService','FileService','$state','$timeout',
    function (authService, fileService, $state, $timeout) {
        return {
            restrict: 'E',
            scope: true,
            link: function (scope, element, attrs) {
                $timeout(function(){
                    scope.user = authService.currentUser();
                    scope.$state = $state;
                    scope.menu = {
                        photos: fileService.allUserPhotos().length,
                        videos: fileService.allUserVideos().length
                    };
                });
            },
            templateUrl: 'assets/templates/up-profile-menu.html'
        }
    }
]);

angular.module('sproutupApp').directive('upProfileEditButtons', ['$rootScope','FileService','$state','$log','$timeout',
    function ($rootScope, fileService, $state, $log, $timeout) {
        return {
            restrict: 'E',
            scope: true,
            link: function (scope, element, attrs) {
                scope.$state = $state;
                scope.state = "pristine";
                scope.showMessage = false;

                var hideStatusMessage = function(test){
                   scope.showMessage = false;
                };

                scope.cancel = function () {
                    $log.debug("up-profile - cancel()");
                    $rootScope.$broadcast('profile:cancel');
                };

                scope.$on('profile:saved', function (event, args) {
                    console.log('event received profile:saved ');
                    scope.state = "pristine";
                });

                scope.$on('profile:valid', function (event, args) {
                    console.log('event received profile:valid ');
                    scope.state = "dirty";
                });

                scope.$on('profile:invalid', function (event, args) {
                    console.log('event received profile:invalid ');
                    scope.state = "pristine";
                });

                scope.save = function () {
                    $log.debug("up-profile - save()");
                    $rootScope.$broadcast('profile:save');
                    scope.state = "saving";
                };

            },
            templateUrl: 'assets/templates/up-profile-edit-buttons.html'
        }
    }
]);

angular.module('sproutupApp').directive('upProfileEdit', ['$rootScope','AuthService', 'UserService', '$state','$log', '$timeout',
    function ($rootScope, authService, userService, $state, $log, $timeout) {
        return {
            restrict: 'A',
            scope: true,
            link: function (scope, element, attrs) {
                scope.$state = $state;

                var user = authService.currentUser();

                scope.$on('auth:status', function (event, args) {
                    user = authService.currentUser();
                    copyUser(updated, user);
                });

                scope.$watch(
                    function(scope){
                        return scope.basicinfoform.$dirty && scope.basicinfoform.$valid;
                    },
                    function(newValue, oldValue) {
                        $log.debug("dirty&valid: " + newValue + " " + oldValue);
                        if(newValue==true){
                            $rootScope.$broadcast('profile:valid');
                        }
                        else{
                            $rootScope.$broadcast('profile:invalid');
                        }
                    }
                );

                var copyUser = function(copy, orig){
                    copy.id = orig.id;
                    copy.email = orig.email;
                    copy.firstname = orig.firstname;
                    copy.lastname = orig.lastname;
                    copy.name = orig.name;
                    copy.nickname = orig.nickname;
                    copy.description = orig.description;
                    copy.urlFacebook = orig.urlFacebook;
                    copy.urlTwitter = orig.urlTwitter;
                    copy.urlPinterest = orig.urlPinterest;
                    copy.urlBlog = orig.urlBlog;
                };

                var updated = {
                    "id" : user.id,
                    "email" : user.email,
                    "firstname" : user.firstname,
                    "lastname" : user.lastname,
                    "name" : user.name,
                    "nickname" : user.nickname,
                    "description" : user.description,
                    "urlFacebook" : user.urlFacebook,
                    "urlTwitter" : user.urlTwitter,
                    "urlPinterest" : user.urlPinterest,
                    "urlBlog" : user.urlBlog
                };

                scope.user = updated;

                var previous_state = "profile.photos";

                scope.$on('$stateChangeSuccess', function (ev, to, toParams, from, fromParams) {
                    //assign the "from" parameter to something
                    console.log('state change ' + from.name);
                    if(from.name.length > 0){
                        previous_state = from.name;
                    }
                });

                scope.$on('profile:save', function (event, args) {
                    console.log('event received profile:save ');
                    userService.update(scope.user).then(
                        function(data){
                            $log.debug("up-profile-edit > update success");
                            user.description = data.description;
                            user.name = data.name;
                            user.nickname = data.nickname;
                            user.firstname = data.firstname;
                            user.lastname = data.lastname;
                            user.urlFacebook = data.urlFacebook;
                            user.urlTwitter = data.urlTwitter;
                            user.urlPinterest = data.urlPinterest;
                            user.urlBlog = data.urlBlog;
                            $rootScope.$broadcast('profile:saved');
                            $rootScope.$broadcast('alert:success', {
                                message: 'Your profile is saved'
                            });
                            scope.basicinfoform.$setPristine();
                        },
                        function(error){
                            $log.debug("up-profile-edit > update failed");
                        }
                    )
                });

                scope.$on('profile:cancel', function (event, args) {
                    console.log('event received profile:cancel ');
                    $state.go(previous_state);
                });
            }
        }
    }
]);

angular.module('sproutupApp').directive('upProfile', ['$filter', '$log', 'FileService',
    function ($filter, $log, fileService) {
        return {
            restrict: 'EA',
            scope: {
            },
            controller: function($scope) {
                fileService.getAllUserFiles().then(
                    function(data){
                        $log.debug("up-profile - files loaded");
                    },
                    function(error){
                    }
                );
            }
        }
    }
]);

angular.module('sproutupApp').directive('follow', ['FollowService',
    function (FollowService) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var isFollowing = false;

            function changeButtonToFollowing() {
                isFollowing = true;
                element.html('<i class="fa fa-check"></i>Following');
                element.addClass("btn-following");
                element.removeClass("btn-outline");
            }

            function changeButtonToFollow() {
                isFollowing = false;
                element.html('Follow');
                element.removeClass("btn-following");
                element.addClass("btn-outline");
            }

            attrs.$observe('refId', function(refId) {
                if (refId) {
                    console.log("refId observe: " + refId);
                    FollowService.isFollowing(refId, attrs.refType)
                        .then(
                        function(payload){
                            changeButtonToFollowing();
                        },
                        function(errorPayload){
                            changeButtonToFollow();
                        }
                    );
                }
            });

            element.on('click', function () {
                console.log("refId: "+ attrs.refId);
                if(!isFollowing){
                    FollowService.follow(attrs.refId, attrs.refType)
                        .then(
                        function(payload){
                            changeButtonToFollowing();
                        },
                        function(errorPayload){
                            if(errorPayload="forbidden"){
                                scope.login('sm');
                            }
                            changeButtonToFollow();
                        }
                    );
                }
                else{
                    FollowService.unfollow(attrs.refId, attrs.refType)
                        .then(
                        function(payload){
                            changeButtonToFollow();
                        },
                        function(errorPayload){
                            changeButtonToFollowing();
                        }
                    );
                }
            });
        }
    };
}]);

angular.module('sproutupApp').directive('upTrial', ['ProductTrialService', 'AuthService',
    function (trialService, authService) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var isFollowing = false;

                function changeButtonToFollowing() {
                    isFollowing = true;
                    element.html('<i class="fa fa-check"></i>I am in for Tryout.');
                    element.addClass("btn-following");
                    element.removeClass("btn-outline");
                    element.addClass("disabled");
                }

                function changeButtonToFollow() {
                    isFollowing = false;
                    element.html('Interested in Tryout?');
                    element.removeClass("btn-following");
                    element.addClass("btn-outline");
                }

                attrs.$observe('refId', function(refId) {
                    if (refId) {
                        console.log("refId observe: " + refId);
                        trialService.didSignUp(refId, attrs.refType)
                            .then(
                            function(payload){
                                changeButtonToFollowing();
                            },
                            function(errorPayload){
                                changeButtonToFollow();
                            }
                        );
                    }
                });

                element.on('click', function () {
                    console.log("refId: "+ attrs.refId);
                    if(authService.isLoggedIn()){
                        scope.trial('sm', {"product_id": attrs.refId, "isLoggedIn": true}).then(
                            function(){
                                console.log("trial success");
                                changeButtonToFollowing();
                            },
                            function(){
                                console.log("trial error");
                            }
                        );
                    }
                    else {
                        scope.trial('sm', {"product_id": attrs.refId, "isLoggedIn": false});
                    }
                });
            }
        };
    }
]);

angular.module('sproutupApp').directive('avatar', function () {
    return {
        restrict: 'E',
        template: '<img ng-src="{{user.avatarUrl}}">',
        link: function (scope, element, attrs) {
            attrs.$observe('class', function(value) {
                if (value) {
                    console.log("value: " + value);
                    element.addClass(value);
                }
            });

            //console.log(attrs.class);
            //element.addClass(attrs.class);
        }
    };
});

angular.module('sproutupApp').directive('commentlink', function () {
    return {
        restrict: 'E',
        templateUrl: 'assets/templates/comment-add-link.html'
    };
});

angular.module('sproutupApp').directive('subjectPresent', function ($parse) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var onPresent = $parse(attrs.subjectPresent);
            var onLogin = $parse(attrs.login);

            element.on('click', function () {
                if(scope.user.isLoggedIn){
                    console.log(attrs.subjectPresent);

                    // The event originated outside of angular,
                    // We need to call $apply
                    scope.$apply(function () {
                        onPresent(scope);
                    });
                }
                else{
                    console.log(attrs.login);
                    scope.$apply(function () {
                        onLogin(scope);
                    });
                }
            });
        }
    };
});

angular.module('sproutupApp').directive('upToptags', ['TagsService', '$timeout',
    function (tagService, $timeout) {
    return {
        templateUrl: 'assets/templates/up-toptags.html',
        scope: {
            productId: "=",
            category: "="
        },
        link: function (scope, element, attrs) {
            $timeout(function(){
                console.log("tags timeout");
                refresh();
            });

            //attrs.$observe('category', function (category) {
            //    console.log("tags observe");
            //    refresh();
            //});

            var refresh = function(){
                tagService.getPopularPostTags(scope.productId, scope.category).then(
                    function(data){
                        scope.tags = data;
                    },
                    function(error){
                    }
                );

                switch(scope.category) {
                    case 0:
                        scope.cat = "compliments";
                        break;
                    case 1:
                        scope.cat = "suggestions";
                        break;
                    case 2:
                        scope.cat = "questions";
                        break;
                    default:
                        scope.cat = "default";
                }
            }
        }
    };
}]);

/*
    Product List
 */
angular.module('sproutupApp').directive('upProductList', [ 'ProductService',
    function (productService) {
        return {
            restrict: 'A',
            transclude:true,
            scope: {
            },
            link: function (scope, element, attrs, ctrl, transclude) {
                // get the product list
                scope.query = "";
                scope.products = productService.query();

                // add the directive scope to the transcluded content
                transclude(scope, function(clone, scope) {
                    element.append(clone);
                });
            }
        };
    }]);

angular.module('sproutupApp').directive('upProductItem', [
    function () {
        return {
            templateUrl: 'assets/templates/up-product-item.html',
            //require: '^upProductList',
            scope: {
                product: "="
            },
            link: function (scope, element, attrs) {
                attrs.$observe('product', function (producty) {
                });
            }
        };
    }]);

angular.module('sproutupApp').directive('navbar', [ 'AuthService', '$rootScope',
    function (authService, $rootScope) {
    return {
        templateUrl: '/assets/templates/navbar.html',
        scope: true,
        link: function (scope, element, attrs) {
            scope.user = authService.currentUser();
            scope.isLoggedIn = authService.isLoggedIn();
            scope.$on('auth:status', function (event, args) {
                console.log('event received auth:state ' + args.data);
                scope.user = authService.currentUser();
                scope.isLoggedIn = authService.isLoggedIn();
            });
        }
    };
}]);

angular.module('sproutupApp').directive('upFbShare', [ '$location', '$window', function ($location, $window) {
    return {
        template: '<a class="post-actions--item" target="_blank" ng-href="{{url}}"><i class="fa fa-facebook-square"></i>Share on Facebook</a>',
        scope: {
            anchor: "="
        },
        link: function (scope, element, attrs) {
            attrs.$observe('anchor', function(anc) {
                scope.url = "http://www.facebook.com/share.php?u=" + encodeURIComponent($location.absUrl() + "#" + scope.anchor);
            });

            //element.on('click', function () {
            //    $window.open(encodeURIComponent(scope.url));
            //});
        }
    };
}]);



angular.module('sproutupApp').directive('upAccessLevel', ['AuthService', '$log',
    function(auth, $log) {
        return {
            restrict: 'A',
            scope: true,
            link: function(scope, element, attrs) {
                var prevDisp = element.css('display')
                    , userRole
                    , accessLevel;

                scope.user = auth.currentUser();
                scope.$watch('user', function(user) {
                    $log.debug("up-access-level - watch - user - " + user.role);
                    if(user.role)
                        userRole = user.role;
                    updateCSS();
                }, true);

                attrs.$observe('upAccessLevel', function(al) {
                    $log.debug("up-access-level - watch - accesslevel - " + al);
                    //if(al) accessLevel = $scope.$eval(al);
                    if(al) accessLevel = al;
                    updateCSS();
                });

                function updateCSS() {
                    if(userRole && accessLevel) {
                        $log.debug("up-access-level - update css");
                        if(!auth.authorize(accessLevel, userRole))
                            element.css('display', 'none');
                        else
                            element.css('display', prevDisp);
                    }
                }
            }
        };
    }
]);
