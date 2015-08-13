// factory
angular
    .module('sproutupApp')
    .factory('LikesService', likesService);

likesService.$inject = ['$http','$log','$q',];

function likesService($http, $log, $q) {
  var urlBase = '/api/likes';
  var LikesService = {};

  LikesService.getLikes = function(refId, refType){
    return $http({
        method: 'GET',
        url: urlBase + "/" + refType + "/" + refId,
      });
    };

  LikesService.addLikes = function(refId, refType, userId, data){
    return $http({
        method: 'POST',
        url: urlBase + "/" + refType + "/" + refId,
        params: {user_id: userId},
        data: "{}",
        headers: {'Content-Type': 'application/json'}
      });
    };

  LikesService.addLike = function(refId, refType, userId){
    var deferred = $q.defer();
    $http({
        method: 'POST',
        url: urlBase + "/" + refType + "/" + refId,
        params: {user_id: userId},
        data: "{}",
        headers: {'Content-Type': 'application/json'}
    }).success(function(data, status, headers, config){
        console.log(data);
        deferred.resolve(data);
    }).error(function(data, status, headers, config) {
        deferred.reject();
    });

    return deferred.promise;
  };

  return LikesService;

}