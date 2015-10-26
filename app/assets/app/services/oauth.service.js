// factory
angular
    .module('sproutupApp')
    .factory('OAuthService', oauthService);

oauthService.$inject = ['$http', '$q', '$cookieStore', '$log', 'UserService', '$timeout', '$state', '$analytics', '$resource'];

function oauthService($http, $q, $cookieStore, $log, userService, $timeout, $state, $analytics, $resource){
    var user = {};
    var isReady = false;
    var urlBase = '/api/auth';
    var accessLevels = routingConfig.accessLevels,
        userRoles = routingConfig.userRoles;

    var model = {
      OAuth1Connect: $resource('http://localhost:3000/api/network/:provider/:userId/connect', { provider:'@provider', userId:'@userId' }),
      OAuth1Callback: $resource('http://localhost:3000/api/network/callback/:token', { token:'@token' }),
      Network: $resource('http://localhost:3000/api/network/user/:userId', { userId:'@userId' }),
      UserNetwork: $resource('http://localhost:3000/api/user/:userId/network/:provider')
    };

    var service = {
        m: model,
        getNetwork: getNetwork,
        deleteNetwork: deleteNetwork,
        getAuthorizeUrl: getAuthorizeUrl,
        saveCallback: saveCallback
    };

    init();

    return service;

    function init() {
    }

    function getAuthorizeUrl(provider, userId) {
        var deferred = $q.defer();

        model.OAuth1Connect.get({provider: provider, userId: userId})
            .$promise.then(function(data) {
              console.log('res: ', data);
              deferred.resolve(data);
            });

        return deferred.promise;
    }

    function deleteNetwork(provider, userId) {
        var deferred = $q.defer();

        $http({
            method: 'DELETE',
            url: 'http://localhost:3000/api/user/' + userId + '/network/' + provider,
            headers: {'Content-Type': 'application/json'}
        }).success(function(data, status, headers, config){
            deferred.resolve(data);
        }).error(function(data, status, headers, config){
            // called asynchronously if an error occurs
            // or server returns response with an error status.
            $log.debug("auth user service returned error");
            deferred.reject("delete failed");
        });

/*        model.UserNetwork.delete({provider: provider, userId: userId})
            .$promise.then(function(data) {
              console.log('res: ', data);
              deferred.resolve(data);
            });
*/
        return deferred.promise;
    }

    function getNetwork(userId){
        var deferred = $q.defer();

        model.Network.query({userId: userId})
            .$promise.then(function(data) {
              console.log('[oauth1] ', data);
              deferred.resolve(data);
            });

        return deferred.promise;
    }

    function saveCallback(token, verifier) {
        var deferred = $q.defer();
        var item = new model.OAuth1Callback({token: token});
        item.verifier = verifier;

        item.$save(function (data, headers) {
               // Success
              console.log('callback res: ', data);
              deferred.resolve(data);
           }, function (error) {
               // failure
               console.log("$save failed " + JSON.stringify(error));
           });

        return deferred.promise;
    }

    /*
    *   Return true when user auth data has been received
    */
    function ready(){
//        var deferred = $q.defer();
//        deferred.resolve(model.user);
        return isReady;
//        return deferred.promise;
    }
}