'use strict';

var productServices = angular.module('productServices', ['ngResource']);

productServices.factory('ProductService', ['$resource',
  function($resource) {
    return $resource('/api/products/:slug'); // Note the full endpoint address
  }]);

productServices.factory('FileService', ['$http','$log', '$q', '$upload',
function($http, $log, $q, $upload){
    var FileService = {};

    FileService.verify = function(uuid){
        var deferred = $q.defer();

        $http({
            method: 'POST',
            url: '/api/file/verify',
            headers: {'Content-Type': 'application/json'},
            params: {
                'uuid' : uuid
            }
        }).success(function(data, status, headers, config){
            $log.debug("file verify returned success");
            deferred.resolve(true);
        }).error(function(data, status, headers, config){
            $log.debug("file verify returned error");
            deferred.reject(false);
        });

        return deferred.promise;
    };

    FileService.upload = function(file, payload){
        var deferred = $q.defer();

        $log.debug("file.name: " + file.name);
        $log.debug("file.size: " + file.size);
        $log.debug("file.type: " + file.type);
        $log.debug("url: " + payload.url);
        $log.debug("Host: " + payload.host);
        $log.debug("content-length: " + payload.contentlength);

        $upload.http({
            //$http({
            url: payload.url,
            method: 'PUT',
            headers: {
                'x-amz-content-sha256': payload.xamzcontentsha256,
                'content-length': payload.contentlength,
                'content-type': file.type,
                'x-amz-storage-class': payload.xamzstorageclass,
                'x-amz-date': payload.xamzdate,
                'Host': payload.host,
                'Authorization': payload.authorization
            },
            data: file
        }).progress(function (evt) {
            //var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
            var percentComplete = Math.min(100, parseInt(100.0 * evt.loaded / evt.total));
            console.log('progress: ' + percentComplete + '% ');
            deferred.notify(percentComplete);
        }).success(function (data, status, headers, config) {
            console.log('file uploaded. Response: ' + payload.uuid);
            deferred.resolve(payload.uuid);
        });

        return deferred.promise;
    };

    FileService.authenticate = function(file, comment, refId, refType){
        var deferred = $q.defer();

        $log.debug("file: " + file.size);

        var pos = 0;
        var reader = new FileReader();
        var startTime = +new Date();
        var hash;

        var sha256 = CryptoJS.algo.SHA256.create();

        reader.onprogress = function(progress) {
            //var chunk = new Uint8Array(reader.result); //.subarray(pos, progress.loaded);
            //sha256.update(chunk);
            //pos = progress.loaded;
            var length = progress.loaded - pos;
            console.log("pos: " + pos);
            console.log("length: " + length);
            //var arr = new Uint8Array(reader.result, pos, length);
            //sha256.update(chunk);
            pos = progress.loaded;
            if(progress.lengthComputable) {
                console.log((progress.loaded/progress.total*100).toFixed(1)+'%');
            }
            var percentComplete = (progress.loaded/progress.total*100).toFixed(1);
            deferred.notify(percentComplete);
        };

        reader.onload = function() {
            if (reader.readyState == FileReader.DONE) { // DONE == 2
                var endTime = +new Date();
                console.log('hashed', file.name, 'in', endTime - startTime, 'ms', reader.result.byteLength, 'len');
                //var chunk = new Uint8Array(reader.result, pos);
                //if(chunk.length > 0) sha256.update(chunk);
                //var hash = sha256.finalize();

                //var arrayBuffer = reader.result;

                var chunk = CryptoJS.lib.WordArray.create(reader.result);
                sha256.update(chunk);
                hash = sha256.finalize().toString(CryptoJS.enc.Hex);
                //hash = CryptoJS.SHA256(arrayBuffer).toString(CryptoJS.enc.Hex);

                $http({
                    method: 'GET',
                    url: '/api/file/policy',
                    headers: {'Content-Type': 'application/json'},
                    params: {
                        'contentHash' : hash,
                        'contentName' : file.name,
                        'contentLength': file.size,
                        'contentType': file.type,
                        'comment': comment,
                        'refId': refId,
                        'refType': refType}
                }).success(function(data, status, headers, config){
                    $log.debug("file auth returned success");
                    deferred.resolve(data);
                }).error(function(data, status, headers, config){
                    $log.debug("file auth returned error");
                    deferred.reject(null);
                });


                console.log("hashing done");
            }
        };

        reader.readAsArrayBuffer(file);

        //var hash = CryptoJS.SHA256("HOORAY").toString(CryptoJS.enc.Hex);

        $log.debug("file auth returned promise");
        return deferred.promise;
    };

    return FileService;
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
            deferred.reject(data);
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
