'use strict';

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

angular.module('sproutupApp').directive('upSlideableToggle',
    function() {
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
            }
        };
    });

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
                        console.log("up-video - file changed: " + file.id);
                    }
                });
                $timeout(function () {
                    $timeout(function () {
                        console.log("up-video - loaded: " + attrs.file.id);
                        // This code will run after
                        // templateUrl has been loaded, cloned
                        // and transformed by directives.
                        // and properly rendered by the browser
                        var flowplr = element.find(".player");
                        flowplr.flowplayer();
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

angular.module('sproutupApp').directive('upProfileMenu', ['AuthService','FileService','$state',
    function (authService, fileService, $state) {
        return {
            restrict: 'E',
            scope: true,
            link: function (scope, element, attrs) {
                scope.user = authService.currentUser();
                scope.$state = $state;
                scope.menu = {
                    photos: fileService.allUserPhotos().length,
                    videos: fileService.allUserVideos().length
                };
            },
            templateUrl: 'assets/templates/up-profile-menu.html'
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
                var files = $scope.files = [];
                var photos = $scope.photos = [];
                var videos = $scope.videos = [];

                fileService.getAllUserFiles().then(
                    function(data){
                        $log.debug("up-profile - files loaded");
                        files = data;
                        photos = $filter("filter")(files, {type:"image"});
                        //videos = $filter("filter")(files, {type:"video"});
                        $log.debug("up-profile - photos "); // + photos.length +" videos " + videos.length);
                    },
                    function(error){
                    }
                );

                this.photos = photos;

                $scope.select = function(pane) {
                    pane.selected = true;
                };

                this.addPane = function(pane) {
                };
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

angular.module('sproutupApp').directive('upToptags', ['TagsService',
    function (tagService) {
    return {
        templateUrl: 'assets/templates/up-toptags.html',
        scope: {
            productId: "=",
            category: "="
        },
        link: function (scope, element, attrs) {
            attrs.$observe('category', function (category) {
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
            });
        }
    };
}]);

angular.module('sproutupApp').directive('upProductList', [ 'ProductService',
    function (productService) {
        return {
            templateUrl: 'assets/templates/up-product-list.html',
            scope: {
            },
            link: function (scope, element, attrs) {
                scope.products = productService.query();
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

angular.module('sproutupApp').directive('navbar', function () {
    return {
        templateUrl: '/assets/templates/navbar.html',

        controller: function($scope, $log, $http, AuthService) {
            AuthService.isLoggedIn();
        }
    };
});

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
