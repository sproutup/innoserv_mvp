angular
    .module('sproutupApp')
    .factory('FeedService', FeedService);

FeedService.$inject = ['$resource'];

function FeedService($resource){
	var service = {
		buzzAll: buzzAll,
		buzzProduct: buzzProduct
	};
	return service;

	function buzzAll() {
        return $resource('/api/buzz/:start', {start:'@start'}, {update:{method:'PUT'}} );
	}

	function buzzProduct() {
        return $resource('/api/buzz/product/:id/:start', {id:'@id', start:'@start'}, {update:{method:'PUT'}} );
	}
}
