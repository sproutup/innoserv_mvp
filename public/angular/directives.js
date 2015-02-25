'use strict';


angular.module('sproutupApp').directive('follow', ['FollowService',
    function (FollowService) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var isFollowing = false;
            var refId = attrs.refId;
            var refType = attrs.refType;

            FollowService.isFollowing(refId, refType)
                .then(
                function(payload){
                    isFollowing = true;
                    element.html('<i class="fa fa-check"></i>Following');
                    element.addClass("btn-following");
                    element.removeClass("btn-outline");
                },
                function(errorPayload){
                    isFollowing = false;
                    element.html('Follow');
                    element.removeClass("btn-following");
                    element.addClass("btn-outline");
                }
            );

            element.on('mouseenter', function () {
                if(isFollowing){
                    element.html('UnFollow');
                }
            });
            element.on('mouseleave', function () {
                if(isFollowing){
                    element.html('Following');
                }
            });

            element.on('click', function () {
                if(!isFollowing){
                    FollowService.follow(refId, refType)
                        .then(
                        function(payload){
                            isFollowing = true;
                            element.html('<i class="fa fa-check"></i>Following');
                            element.addClass("btn-following");
                            element.removeClass("btn-outline");
                        },
                        function(errorPayload){
                            if(errorPayload="forbidden"){
                                scope.login('sm');
                            }
                            isFollowing = false;
                            element.html('Follow');
                            element.removeClass("btn-following");
                            element.addClass("btn-outline");
                        }
                    );
                }
                else{
                    FollowService.unfollow(refId, refType)
                        .then(
                        function(payload){
                            isFollowing = false;
                            element.html('Follow');
                            element.removeClass("btn-following");
                            element.addClass("btn-outline");
                        },
                        function(errorPayload){
                            isFollowing = true;
                            element.html('Following');
                            element.addClass("btn-following");
                            element.removeClass("btn-outline");
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
