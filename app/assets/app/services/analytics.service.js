// factory
angular
    .module('sproutupApp')
    .factory('AnalyticsService', AnalyticsService);

AnalyticsService.$inject = ['$http', '$q', '$log', 'UserService', '$timeout'];

function AnalyticsService($http, $q, $log, userService, $timeout){
    var user = {};
    var isReady = false;
    var urlBase = '/api/analytics';

    var service = {
        getAll: getAll
    };

    activate();

    return service;

    function activate() {
        $log.debug("analytics service - activated");
    }

    function getAll(){
        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: urlBase,
            headers: {'Content-Type': 'application/json'}
        }).success(function(data, status, headers, config){
            switch(status){
                case 200:
                    deferred.resolve(data);
                    break;
                case 302: // no content
                    $log.debug("google api service - found");
                    deferred.resolve("found");
                    break;
                default:
                    $log.debug("unhandled return value");
                    deferred.reject("unhandled return value");
                    break;
            }
        }).error(function(data, status, headers, config){
            $log.debug("analytics service returned error");
            deferred.reject("failed");
        });

        return deferred.promise;
    }
}
