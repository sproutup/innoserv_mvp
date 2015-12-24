angular
    .module('sproutupApp')
    .factory('SuggestService', SuggestService);

SuggestService.$inject = ['$resource', '$http', '$q'];

function SuggestService($resource, $http, $q){
	var service = {
    addSuggestedProduct: addSuggestedProduct,
		suggestedProducts: suggestedProducts
	};

	return service;

  function suggestedProducts() {
    return $resource('api/suggest/:start', {start:'@start'}, {update:{method:'PUT'}} );
  }

  function addSuggestedProduct(bodyText, nameText) {
    var deferred = $q.defer();

    $http({
        method: 'POST',
        url: '/api/suggest/post',
        data: {body: bodyText, name: nameText},
        headers: {'Content-Type': 'application/json'}
    }).success(function(data, status, headers, config){
        deferred.resolve(data);
    }).error(function(data, status, headers, config) {
        deferred.reject(data);
    });

    return deferred.promise;
  }

}