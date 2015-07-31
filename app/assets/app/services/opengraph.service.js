// factory
angular
    .module('sproutupApp')
    .factory('OpenGraphService', OpenGraphService);

OpenGraphService.$inject = ['$http', '$q', '$log'];

function OpenGraphService($http, $q, $log){

    var service = {
        parse: parse
    };

    activate();

    return service;

    function activate() {
        $log.debug("opengraph service - activated");
    }

    function parse(url){
        var deferred = $q.defer();
        $log.debug("OpenGraph - parse");

        $http({
            method: 'GET',
            url: 'api/openGraph/parse/',
            params: { "url": url },
            headers: {'Content-Type': 'application/json'}
        }).success(function(data, status, headers, config) {
            deferred.resolve(data);
        }).error(function(data, status, headers, config){
            $log.debug("Failed to obtain ogData from url");
            deferred.reject("Failed to obtain ogData from url");
        });

        return deferred.promise;
    }
}
