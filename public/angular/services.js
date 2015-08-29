'use strict';

var productServices = angular.module('productServices', ['ngResource']);


productServices.factory('Utils', ['$log',
    function($log) {
        var utils = {};

        utils.equalHeight = function(container){
            var currentTallest = 0,
                    currentRowStart = 0,
                    rowDivs = new Array(),
                    $el,
                    topPosition = 0;

            $(container).each(function() {

                $el = $(this);
                $($el).height('auto')
                topPosition = $el.position().top;

                console.log("top postion: ", topPosition);

                if (currentRowStart != topPosition) {
                    for (var currentDiv = 0 ; currentDiv < rowDivs.length ; currentDiv++) {
                        rowDivs[currentDiv].height(currentTallest);
                    }
                    rowDivs.length = 0; // empty the array
                    currentRowStart = topPosition;
                    currentTallest = $el.height();
                    rowDivs.push($el);
                } else {
                    rowDivs.push($el);
                    currentTallest = (currentTallest < $el.height()) ? ($el.height()) : (currentTallest);
                }
                for (currentDiv = 0 ; currentDiv < rowDivs.length ; currentDiv++) {
                    rowDivs[currentDiv].height(currentTallest);
                }
            });
        };

        return utils;
    }
]);

productServices.factory('ProductService', ['$resource',
    function($resource) {
        return $resource('/api/products/:slug', {slug:'@slug'}, {update:{method:'PUT'}} ); // Note the full endpoint address
    }
]);

productServices.factory('CompanyService', ['$resource',
    function($resource) {
        return $resource('/api/company/:id'); // Note the full endpoint address
    }
]);

productServices.factory('UserService', ['$resource',
    function($resource) {
        return $resource('/api/users/:nickname', {nickname:'@nickname'}, {update:{method:'PUT'}} ); // Note the full endpoint address
    }
]);

productServices.factory('ProductServicex', ['$http','$log', '$q',
    function($http, $log, $q) {
        var productService = {};

        productService.query = function(){
            var deferred = $q.defer();

            $http({
                method: 'GET',
                url: '/api/products',
                headers: {'Content-Type': 'application/json'}
            }).success(function(data, status, headers, config){
                deferred.resolve(data);
            }).error(function(data, status, headers, config){
                deferred.reject("product service : error querying products");
            });

            return deferred.promise;
        };

        return productService;
    }
]);

productServices.factory('TwitterService', ['$http','$log', '$q',
    function($http, $log, $q) {
        var twitterService = {};

        twitterService.show = function(id){
            var deferred = $q.defer();

            $http({
                method: 'GET',
                url: '/api/twitter/' + id + '/show',
                headers: {'Content-Type': 'application/json'}
            }).success(function(data, status, headers, config){
                $log.debug("twitterService > show success");
                deferred.resolve(data);
            }).error(function(data, status, headers, config){
                $log.debug("twitterService > failed");
                deferred.reject("twitterService > failed");
            });

            return deferred.promise;
        };

        twitterService.user_timeline = function(id){
            var deferred = $q.defer();

            $http({
                method: 'GET',
                url: '/api/twitter/statuses/user_timeline/' + id,
                headers: {'Content-Type': 'application/json'}
            }).success(function(data, status, headers, config){
                $log.debug("twitterService > statuses/user_timeline success");
                deferred.resolve(data);
            }).error(function(data, status, headers, config){
                $log.debug("twitterService > failed");
                deferred.reject("twitterService > failed");
            });

            return deferred.promise;
        };


        twitterService.search = function(id){
            var deferred = $q.defer();

            $http({
                method: 'GET',
                url: '/api/twitter/' + id + '/search',
                headers: {'Content-Type': 'application/json'}
            }).success(function(data, status, headers, config){
                $log.debug("twitterService > search success");
                deferred.resolve(data);
            }).error(function(data, status, headers, config){
                $log.debug("twitterService > failed");
                deferred.reject("twitterService > failed");
            });

            return deferred.promise;
        };

        return twitterService;
    }
]);

productServices.factory('FacebookService', ['$http','$log', '$q',
    function($http, $log, $q) {
        var facebookService = {};

        facebookService.get = function(id){
            var deferred = $q.defer();

            $http({
                method: 'GET',
                url: '/api/facebook/' + id + '/posts',
                headers: {'Content-Type': 'application/json'}
            }).success(function(data, status, headers, config){
                $log.debug("facebookService > add success");
                deferred.resolve(data);
            }).error(function(data, status, headers, config){
                $log.debug("facebookService > failed");
                deferred.reject("facebookService > failed");
            });

            return deferred.promise;
        };

        return facebookService;
    }
]);

productServices.factory('EarlyAccessRequestService', ['$http','$log', '$q',
    function($http, $log, $q) {
        var earlyAccessRequestService = {};

        earlyAccessRequestService.add = function(request){
            var deferred = $q.defer();

            $http({
                method: 'POST',
                url: '/api/creator/access',
                headers: {'Content-Type': 'application/json'},
                data: request
            }).success(function(data, status, headers, config){
                $log.debug("earlyAccessRequestService > add success");
                deferred.resolve(data);
            }).error(function(data, status, headers, config){
                $log.debug("earlyAccessRequestService > failed to update");
                deferred.reject("earlyAccessRequestService > failed to add");
            });

            return deferred.promise;
        };

        return earlyAccessRequestService;
    }
]);

productServices.factory('ProductSuggestionService', ['$http','$log', '$q',
    function($http, $log, $q) {
        var productSuggestionService = {};

        productSuggestionService.add = function(suggestion){
            var deferred = $q.defer();

            $http({
                method: 'POST',
                url: '/api/suggest/product',
                headers: {'Content-Type': 'application/json'},
                data: suggestion
            }).success(function(data, status, headers, config){
                $log.debug("productSuggestionService > add success");
                deferred.resolve(data);
            }).error(function(data, status, headers, config){
                $log.debug("productSuggestionService > failed to update");
                deferred.reject("productSuggestionService > failed to add");
            });

            return deferred.promise;
        };

        return productSuggestionService;
    }
]);

productServices.factory('ProductTrialService', ['$http','$log', '$q',
    function($http, $log, $q) {
        var productTrialService = {};

        productTrialService.add = function(trial){
            var deferred = $q.defer();

            $http({
                method: 'POST',
                url: '/api/trial/product',
                headers: {'Content-Type': 'application/json'},
                data: trial
            }).success(function(data, status, headers, config){
                $log.debug("productTrialService > add success");
                deferred.resolve(data);
            }).error(function(data, status, headers, config){
                $log.debug("productTrialService > failed to add");
                deferred.reject("failed to add");
            });

            return deferred.promise;
        };

        productTrialService.didSignUp = function(refId, refType){
            var deferred = $q.defer();

            return $http({
                method: 'GET',
                url: "/api/trial/product" + "/" + refType + "/" + refId
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
        };

        return productTrialService;
    }
]);

productServices.factory('UserServiceOld', ['$http','$log', '$q', '$upload', '$filter',
    function($http, $log, $q, $upload, $filter) {
        var userService = {};

        userService.update = function(user){
            var deferred = $q.defer();

            $log.debug("userservice > user.name: " + user.name);

            $http({
                method: 'PUT',
                url: '/api/users/' + user.id,
                headers: {'Content-Type': 'application/json'},
                data: user
            }).success(function(data, status, headers, config){
                $log.debug("userservice > update success");
                deferred.resolve(data);
            }).error(function(data, status, headers, config){
                $log.debug("userservice > failed to update");
                deferred.reject("userservice > failed to update");
            });

            return deferred.promise;
        };

        return userService;
    }
]);


productServices.factory('FileService', ['$http','$log', '$q', '$upload', '$filter',
function($http, $log, $q, $upload, $filter){
    var FileService = {};

    var files = [];
    var photos = [];
    var videos = [];

    function hashFile(file, chunkSize, onLoad, onProgress) {
        var sha256 = CryptoJS.algo.SHA256.create();
        var size = file.size;
        var offset = 0;
        var chunk = file.slice(offset, offset + chunkSize);

        var hashChunk = function() {
            var reader = new FileReader();
            reader.onload = function(e) {
                // Increment hash
                //sha256.update(e.target.result);
                sha256.update(reader.result);

                // Update offset for next chunk
                offset += chunkSize;

                // Hash next chunk if available
                if(offset < size) {
                    // Splice the next Blob from the File
                    chunk = file.slice(offset, offset + chunkSize);
                    if(onProgress != null){
                        onProgress((offset/size*100).toFixed(1));
                    }
                    // Recurse to hash next chunk
                    $log.debug("hashChunk - progress - " + offset);
                    hashChunk();
                    // Done hashing
                } else {
                    // Report digest
                    $log.debug("hashChunk - finished");
                    onLoad.call(file, sha256.finalize().toString(CryptoJS.enc.Hex));
                }
            };

            reader.readAsArrayBuffer(chunk);
        };

        // Start hashing chunks
        $log.debug("hashChunk - start");
        hashChunk();
    }

    FileService.sha256 = function(file){
        var deferred = $q.defer();
        var sha256 = CryptoJS.algo.SHA256.create();
        var size = file.size;
        var offset = 0;
        var chunkSize = 1048576*10; // 1Mb * 5
        var chunk = file.slice(offset, offset + chunkSize);
        var reader = new FileReader();
        var startTime = +new Date();

        var hashChunk = function(file) {
            var reader = new FileReader();
            reader.onload = function(e) {
                // Increment hash

                if (reader.readyState == FileReader.DONE) { // DONE == 2
                    var endTime = +new Date();
                    console.log('hashed', file.name, 'in', endTime - startTime, 'ms', reader.result.byteLength, 'len');
                    var wordarray = CryptoJS.lib.WordArray.create(reader.result);
                    sha256.update(wordarray);
                }

                // Update offset for next chunk
                offset += chunkSize;

                // Hash next chunk if available
                if(offset < size) {
                    // Splice the next Blob from the File
                    chunk = file.slice(offset, offset + chunkSize);
                    var result = {
                        file : file,
                        progress : (offset/size*100).toFixed(1)
                    };
                    deferred.notify(result);
                    // Recurse to hash next chunk
                    hashChunk(file);
                    // Done hashing
                } else {
                    // Report digest
                    $log.debug("hashChunk - finished");
                    file.hash = sha256.finalize().toString(CryptoJS.enc.Hex);
                    deferred.resolve(file);
                }
            };

            reader.readAsArrayBuffer(chunk);
        };

        // Start hashing chunks
        $log.debug("hashChunk - start");
        hashChunk(file);

        return deferred.promise;
    };

    FileService.getAllFiles = function(refId, refType){
        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: "/api/file" + "/" + refType + "/" + refId
        }).success(function(data, status, headers, config){
            $log.debug("fileservice - getAllFiles - success - " + data.length);
            deferred.resolve(data);
        }).error(function(data, status, headers, config){
            deferred.resolve(false);
        });

        return deferred.promise;
    };

    /*
    Get all files belonging to the current session user
     */
    FileService.getAllUserFiles = function(){
        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: "/api/file/user"
        }).success(function(data, status, headers, config){
            $log.debug("fileservice - getAllUserFiles - success - " + data.length);
            files = data;
            photos = $filter("filter")(files, {type:"image"});
            videos = $filter("filter")(files, {type:"video"});
            deferred.resolve(data);
        }).error(function(data, status, headers, config){
            deferred.resolve(false);
        });

        return deferred.promise;
    };

    FileService.allUserFiles = function(){
        return files;
    };

    FileService.allUserPhotos = function(){
        return photos;
    };

    FileService.allUserVideos = function(){
        return videos;
    };

    FileService.verify = function(file, uuid){
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
            $log.debug("type: " + data.type);
            var result = {file : file, data : data, uuid : uuid, status : true};
            deferred.resolve(result);
        }).error(function(data, status, headers, config){
            $log.debug("file verify returned error");
            var result = {file : file, uuid : uuid, status : false};
            deferred.reject(result);
        });

        return deferred.promise;
    };

    FileService.addAvatar = function(file, uuid){
        var deferred = $q.defer();

        $http({
            method: 'POST',
            url: '/api/file/avatar',
            headers: {'Content-Type': 'application/json'},
            params: {
                'uuid' : uuid
            }
        }).success(function(data, status, headers, config){
            $log.debug("add avatar returned success");
            var result = {file : file, data : data, status : true};
            deferred.resolve(result);
        }).error(function(data, status, headers, config){
            $log.debug("add avatar returned error");
            var result = {file : file, status : false};
            deferred.reject(result);
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
            var result = {file : file, progress : percentComplete};
            deferred.notify(result);
        }).success(function (data, status, headers, config) {
            console.log('file uploaded. Response: ' + payload.uuid);
            var result = {file : file, uuid : payload.uuid};
            deferred.resolve(result);
        });

        return deferred.promise;
    };

    FileService.authenticate = function(file, comment, refId, refType){
        var self = this;
        var deferred = $q.defer();

        $log.debug("file: " + file.size);

        var pos = 0;
        var reader = new FileReader();
        var startTime = +new Date();
        var hash;

        this.sha256(file).then(
            function(file){
                $log.debug("sha256: " + file.hash);

                $http({
                    method: 'GET',
                    url: '/api/file/policy',
                    headers: {'Content-Type': 'application/json'},
                    params: {
                        'contentHash' : file.hash,
                        'contentName' : file.name,
                        'contentLength': file.size,
                        'contentType': file.type,
                        'comment': comment? comment : "",
                        'refId': refId,
                        'refType': refType}
                }).success(function(data, status, headers, config){
                    $log.debug("auth rest returned success");
                    var result = {file : file, data : data};
                    deferred.resolve(result);
                }).error(function(data, status, headers, config){
                    $log.debug("auth rest returned error");
                    deferred.reject(null);
                });
            },
            function(error){
                $log.debug("error: " + error);
            },
            function(result){
                deferred.notify(result);
            }
        );

        //reader.readAsArrayBuffer(file);
        //hashFile(file, 4096, onLoad, onProgress );

        //var hash = CryptoJS.SHA256("HOORAY").toString(CryptoJS.enc.Hex);

        $log.debug("authenticate returned promise");
        return deferred.promise;
    };

    return FileService;
}]);
/*
productServices.factory('AuthService', ['$http', '$q', '$cookieStore','$log', '$rootScope',
    function($http, $q, $cookieStore, $log, $rootScope){
    var AuthService = {};
    var urlBase = '/api/auth';
    var accessLevels = routingConfig.accessLevels
        , userRoles = routingConfig.userRoles
        , currentUser = $cookieStore.get('user') || { username: '', role: userRoles.public };

    AuthService.user = {};
    AuthService.isLoggedIn = false;

    $log.debug("AuthService > init");

    $cookieStore.remove('user');

    function changeUser(user) {
        angular.extend(currentUser, user);
        AuthService.user = user;
    }

    AuthService.currentUser = function() {
        return currentUser;
    };

    AuthService.authorize = function(accessLevel, role) {
        if(role === undefined) {
            if(!currentUser == null){
                role = currentUser.role;
            }
            else{
                return false;
            }
        }

        return accessLevel.bitMask & role.bitMask;
    };

    AuthService.getUser = function(){
        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: '/api/auth/user',
            headers: {'Content-Type': 'application/json'}
        }).success(function(data, status, headers, config){
            switch(status){
                case 200:
                    angular.extend(AuthService.user,data);
                    AuthService.isLoggedIn = true;
                    console.log("status: ", status);
                    changeUser(data);
                    $log.debug("auth user service returned success: " + currentUser.name);
                    deferred.resolve(AuthService.user);
                    break;
                case 204: // no content
                default:
                    AuthService.isLoggedIn = false;
                    $log.debug("user not logged in");
                    deferred.reject("user not logged in");
                    break;
            }
        }).error(function(data, status, headers, config){
            // called asynchronously if an error occurs
            // or server returns response with an error status.
            $log.debug("auth user service returned error");
            deferred.reject("auth user - failed to get user");
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
            angular.extend(AuthService.user,data);
            AuthService.isLoggedIn = true;
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
            angular.extend(AuthService.user,data);
            AuthService.isLoggedIn = true;
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
            AuthService.user = {};
            AuthService.isLoggedIn = false;
            deferred.resolve("success");
        }).error(function(data, status, headers, config){
            // called asynchronously if an error occurs
            // or server returns response with an error status.
            deferred.reject("could not logout");
        });

        $log.debug("auth signup service returned promise");
        return deferred.promise;
    };

    AuthService.accessLevels = accessLevels;

    AuthService.userRoles = userRoles;

    return AuthService;

}]);
*/
productServices.factory('TagsService', ['$http', '$q', '$log',
    function($http, $q, $log){
    var urlBase = '/api/tags';
    var tagsService = {};

    tagsService.getTopTags = function(size){
        return $http({
            method: 'GET',
            url: urlBase,
            params: {size: size}
        })
    };

    tagsService.getPopularPostTags = function(productId, category){
        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: urlBase + "/post/top/" + productId + "/" + category,
            headers: {'Content-Type': 'application/json'}
        }).success(function(data, status, headers, config){
            deferred.resolve(data);
        }).error(function(data, status, headers, config) {
            deferred.reject();
        });

        return deferred.promise;
    };


    return tagsService;

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
    };

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
    };

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
    };

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

productServices.factory('CampaignService', ['$http','$log', function($http,$log){

	  var urlBase = '/api/campaign';
	  var CampaignService = {};

	  CampaignService.getCampaign = function(product_id){
	    return $http({
	        method: 'GET',
	        url: urlBase,
	        params: {prod: product_id}
	      })
	    }

	  CampaignService.getPost = function(id){
	    return $http({
	        method: 'GET',
	        url: urlBase + '/' + id,
	        params: {prod:'belleds'}//check why hardcoded?
	      })
	      .success(function (data, status, headers, config) {
	        //$scope.campaign = data.campaign;
	      })
	      .error(function (data, status, headers, config) {
	        // something went wrong :
	        log.debug("forum service http.get error");
	      });
	    }
	  
	    return CampaignService;
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
