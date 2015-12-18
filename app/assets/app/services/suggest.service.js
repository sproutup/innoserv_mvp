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

  function addSuggestedProduct(bodyText, nameText) {
    var deferred = $q.defer();

    $http({
        method: 'POST',
        url: '/api/suggest/post',
        data: {body: bodyText, name: nameText},
        headers: {'Content-Type': 'application/json'}
    }).success(function(data, status, headers, config){
        console.log(data);
        deferred.resolve(data);
    }).error(function(data, status, headers, config) {
        deferred.reject(data);
    });

    return deferred.promise;
  }

  function suggestedProducts() {
    var deferred = $q.defer();

    $http({
      method: 'GET',
      url: 'api/suggest'
    }).success(function(data, status, headers, config){
      console.log(data);
      deferred.resolve(data);
    }).error(function(data, status, headers, config) {
      deferred.reject();
    });

    return deferred.promise;
  }

}