'use strict';

var productServices = angular.module('productServices', ['ngResource']);

productServices.factory('ProductService', ['$resource',
  function($resource) {
    return $resource('/api/products/:slug'); // Note the full endpoint address
  }]);

productServices.factory('AuthService', ['$http', '$q', '$cookieStore','$log',
    function($http, $q, $cookieStore, $log){
    var AuthService = {};
    var urlBase = '/api/auth';
    var accessLevels = routingConfig.accessLevels
        , userRoles = routingConfig.userRoles
        , currentUser = $cookieStore.get('user') || { username: '', role: userRoles.public };

    $cookieStore.remove('user');

    function changeUser(user) {
        angular.extend(currentUser, user);
    }

    AuthService.user = function(user){
        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: '/api/auth/user',
            headers: {'Content-Type': 'application/json'}
        }).success(function(data, status, headers, config){
            // this callback will be called asynchronously
            // when the response is available
            changeUser(data);
            $log.debug("auth user service returned success: " + currentUser.name);
            deferred.resolve(currentUser);
        }).error(function(data, status, headers, config){
            // called asynchronously if an error occurs
            // or server returns response with an error status.
            $log.debug("auth user service returned error");
            deferred.reject("failed to login");
        });

        $log.debug("auth user service returned promise");
        return deferred.promise;
    };


    AuthService.provider = function(provider, path){
        var deferred = $q.defer();
        // get the current path
        //var currentPath = $location.path();
        // redirect external url
        //$window.location.href = 'http://www.google.com';

        $http({
            method: 'POST',
            url: '/api/auth/provider/' + provider,
            data: path,
            headers: {'Content-Type': 'application/json'}
        }).success(function(data, status, headers, config){
            $log.debug("provider returned success");
            deferred.resolve(data.redirect);
        }).error(function(data, status, headers, config){
            $log.debug("provider returned error");
            deferred.reject("failed to login");
        });

        $log.debug("provider returned promise");
        return deferred.promise;
    };

    AuthService.login = function(user){
        var deferred = $q.defer();

        $http({
            method: 'POST',
            url: '/api/auth/login',
            data: user,
            headers: {'Content-Type': 'application/json'}
        }).success(function(data, status, headers, config){
            // this callback will be called asynchronously
            // when the response is available
            changeUser(data);
            $log.debug("login service returned success");
            deferred.resolve("success");
        }).error(function(data, status, headers, config){
            // called asynchronously if an error occurs
            // or server returns response with an error status.
            $log.debug("login service returned error");
            deferred.reject("failed to login");
        });

        $log.debug("login service returned promise");
        return deferred.promise;
    };

    AuthService.signup = function(user){
        var deferred = $q.defer();

        $http({
            method: 'POST',
            url: '/api/auth/signup',
            data: user,
            headers: {'Content-Type': 'application/json'}
        }).success(function(data, status, headers, config){
            // this callback will be called asynchronously
            // when the response is available
            changeUser(data);
            deferred.resolve("success");
        }).error(function(data, status, headers, config){
            // called asynchronously if an error occurs
            // or server returns response with an error status.
            deferred.reject("not signed in");
        });

        $log.debug("auth signup service returned promise");
        return deferred.promise;
    };

    AuthService.logout = function(){
        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: '/api/auth/logout',
            headers: {'Content-Type': 'application/json'}
        }).success(function(data, status, headers, config){
            // this callback will be called asynchronously
            // when the response is available
            currentUser.name = '';
            currentUser.isLoggedIn = false;
            deferred.resolve("success");
        }).error(function(data, status, headers, config){
            // called asynchronously if an error occurs
            // or server returns response with an error status.
            deferred.reject("could not logout");
        });

        $log.debug("auth signup service returned promise");
        return deferred.promise;
    };

    AuthService.isLoggedIn = function(user){
        $log.debug("isLoggedIn");
        //if(user === undefined) {
        //    user = currentUser;
        //}
        //return user.role.title === userRoles.user.title || user.role.title === userRoles.admin.title;

        return currentUser.name != null
    };

    return AuthService;

}]);

productServices.factory('TagsService', ['$http','$log', function($http,$log){
    var urlBase = '/api/tags';
    var TagsService = {};

    TagsService.getTopTags = function(size){
        return $http({
            method: 'GET',
            url: urlBase,
            params: {size: size}
        })
    }

    return TagsService;

}]);

productServices.factory('LikesService', ['$http','$log', function($http,$log){
  var urlBase = '/api/likes';
  var LikesService = {};

  LikesService.getLikes = function(refId, refType){
    return $http({
        method: 'GET',
        url: urlBase + "/" + refType + "/" + refId,
      })
    }

  LikesService.addLikes = function(refId, refType, userId, data){
    return $http({
        method: 'POST',
        url: urlBase + "/" + refType + "/" + refId,
        params: {user_id: userId},
        data: "{}",
        headers: {'Content-Type': 'application/json'}
      });
    }

  return LikesService;

}]);

productServices.factory('FollowService', ['$http', '$q', '$log',
    function($http, $q, $log){
    var urlBase = '/api/follow';
    var FollowService = {};

    FollowService.isFollowing = function(refId, refType){
        var deferred = $q.defer();

        return $http({
            method: 'GET',
            url: urlBase + "/" + refType + "/" + refId
        }).success(function(data, status, headers, config){
            // this callback will be called asynchronously
            // when the response is available
            $log.debug("isFollowing = true");
            deferred.resolve(true);
        }).error(function(data, status, headers, config){
            // called asynchronously if an error occurs
            // or server returns response with an error status.
            $log.debug("isFollowing = false");
            deferred.reject(false);
        });

        return deferred.promise;
    }

    FollowService.follow = function(refId, refType, userId, data){
        var deferred = $q.defer();

        return $http({
            method: 'POST',
            url: urlBase + "/" + refType + "/" + refId,
            params: {user_id: userId},
            data: "{}",
            headers: {'Content-Type': 'application/json'}
        }).success(function(data, status, headers, config){
            // this callback will be called asynchronously
            // when the response is available
            $log.debug("isFollowing = true");
            deferred.resolve(true);
        }).error(function(data, status, headers, config){
            // called asynchronously if an error occurs
            // or server returns response with an error status.
            $log.debug("isFollowing = false");
            deferred.reject(false);
        });

        return deferred.promise;
    }

    FollowService.unfollow = function(refId, refType, userId, data){
        var deferred = $q.defer();

        return $http({
            method: 'DELETE',
            url: urlBase + "/" + refType + "/" + refId,
            params: {user_id: userId},
            data: "{}",
            headers: {'Content-Type': 'application/json'}
        }).success(function(data, status, headers, config){
            // this callback will be called asynchronously
            // when the response is available
            $log.debug("isFollowing = false");
            deferred.resolve(true);
        }).error(function(data, status, headers, config){
            // called asynchronously if an error occurs
            // or server returns response with an error status.
            $log.debug("unfollow failed");
            deferred.reject(false);
        });

        return deferred.promise;
    }

    return FollowService;

}]);


productServices.factory('ForumService', ['$http','$log', function($http,$log){

  var urlBase = '/api/forum/posts';
  var ForumService = {};

  ForumService.getPosts = function(product_id, category){
    return $http({
        method: 'GET',
        url: urlBase,
        params: {prod: product_id, cat: category}
      })
    }

  ForumService.getPost = function(id){
    return $http({
        method: 'GET',
        url: urlBase + '/' + id,
        params: {prod:'belleds', cat:'compliments'}
      })
      .success(function (data, status, headers, config) {
        //$scope.posts = data.posts;
      })
      .error(function (data, status, headers, config) {
        // something went wrong :
        log.debug("forum service http.get error");
      });
    }

  ForumService.addPost = function(data){
    return $http({
        method: 'POST',
        url: urlBase,
        data: JSON.stringify(data),
        headers: {'Content-Type': 'application/json'}
      });
    }

    return ForumService;
  }]);


//module.factory('MyService', function() {
//
//  var factory = {};
//
//  factory.method1 = function() {
//          //..
//      }
//
//  factory.method2 = function() {
//          //..
//      }
//
//  return factory;
//});
