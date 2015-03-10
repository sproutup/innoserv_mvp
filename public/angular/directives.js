'use strict';

angular.module('sproutupApp').directive('upVideo', ['FileService',
    function (fileService) {
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
            }
        }
    }
]);

angular.module('sproutupApp').directive('upFiles', ['FileService',
    function (fileService) {
        return {
            restrict: 'E',
            replace: true,
            scope: true,
            templateUrl: 'assets/templates/up-files.html',
            link: function (scope, element, attrs) {
                // listen for the event in the relevant $scope
                scope.$on('fileUploadEvent', function (event, data) {
                    console.log('on fileUploadEvent');
                    loadFiles();
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

angular.module('sproutupApp').directive('toptags', function () {
    return {
        template:   '<div class="col-sm-12 popular-tags-row">'+
        '<div class="popular-tags-header">Popular tags</div>' +
        '<div class="popular-tags-list">'+
        '<button ng-repeat="tag in toptags | orderBy:counter:true" type="button" class="btn btn-popular-tags"><i class="fa fa-tag"></i>{{tag.name}}<span class="popular-tags-count">{{tag.counter}}</span></button>'+
        '</div>'+
        '</div>',

        controller: function($scope, $log, $http) {

            getTopTags(10);

            function getTopTags(size) {
                $http({
                    method: 'GET',
                    url: '/api/tags/top',
                    params: {size: size}
                }).success(function(data, status, headers, config){
                    // this callback will be called asynchronously
                    // when the response is available
                    $scope.toptags = data;
                }).error(function(data, status, headers, config){
                    // called asynchronously if an error occurs
                    // or server returns response with an error status.
                });
            }

        }
    };
});

angular.module('sproutupApp').directive('navbar', function () {
    return {
        templateUrl: '/assets/templates/navbar.html',

        controller: function($scope, $log, $http, AuthService) {

            AuthService.isLoggedIn();

            getTopTags(10);

            function getTopTags(size) {
                $http({
                    method: 'GET',
                    url: '/api/tags/top',
                    params: {size: size}
                }).success(function(data, status, headers, config){
                    // this callback will be called asynchronously
                    // when the response is available
                    $scope.toptags = data;
                }).error(function(data, status, headers, config){
                    // called asynchronously if an error occurs
                    // or server returns response with an error status.
                });
            }

        }
    };
});
