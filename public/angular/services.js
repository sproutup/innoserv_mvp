'use strict';

var productServices = angular.module('productServices', ['ngResource']);

productServices.factory('ProductService', ['$resource',
  function($resource) {
    return $resource('/api/products/:slug'); // Note the full endpoint address
  }]);

productServices.factory('AuthService', ['$http', '$q', '$cookieStore','$log',
    function($http,$q, $cookieStore, $log){
    var AuthService = {};
    var urlBase = '/api/auth';
    var accessLevels = routingConfig.accessLevels
        , userRoles = routingConfig.userRoles
        , currentUser = $cookieStore.get('user') || { username: '', role: userRoles.public };

    $cookieStore.remove('user');

    function changeUser(user) {
        angular.extend(currentUser, user);
    }

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
            deferred.reject("failed to signup");
        });

        $log.debug("signup service returned promise");
        return deferred.promise;
    };

    AuthService.isLoggedIn = function(){
        $log.debug("isLoggedIn");
        if(user === undefined) {
            user = currentUser;
        }
        return user.role.title === userRoles.user.title || user.role.title === userRoles.admin.title;
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
