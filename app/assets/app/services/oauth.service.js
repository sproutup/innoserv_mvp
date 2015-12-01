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
      Network: $resource('/api/analytics/user/:userId/network/:provider', { provider:'@provider', userId:'@userId' }),
      OAuthCallback: $resource('/api/analytics/network/callback/:token', { token:'@token' })
    };

    var socialMediaChecked = false;
    var networks = [];

    var service = {
        m: model,
        listNetwork: listNetwork,
        deleteNetwork: deleteNetwork,
        createNetwork: createNetwork,
        saveCallback: saveCallback,
        socialMediaChecked: socialMediaChecked,
        networks: networks
    };

    init();

    return service;

    function init() {
    }

    function createNetwork(provider, userId) {
        var deferred = $q.defer();

        model.Network.save({provider: provider, userId: userId})
            .$promise.then(function(data) {
              console.log('res: ', data);
              deferred.resolve(data);
            });

        return deferred.promise;
    }

    function deleteNetwork(provider, userId) {
        var deferred = $q.defer();

        model.Network.delete({provider: provider, userId: userId})
            .$promise.then(function(data) {
              console.log('res: ', data);
              deferred.resolve(data);
            });

        return deferred.promise;
    }

    function listNetwork(userId){
        var deferred = $q.defer();

        model.Network.query({userId: userId})
            .$promise.then(function(data) {
              console.log('[oauth1] list user network: ', data);
              deferred.resolve(data);
            });

        return deferred.promise;
    }

    function saveCallback(token, verifier) {
        var deferred = $q.defer();
        var item = new model.OAuthCallback({token: token});
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
}