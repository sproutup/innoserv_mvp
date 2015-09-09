angular
    .module('sproutupApp')
    .factory('FeedService', FeedService);

FeedService.$inject = ['$resource'];

function FeedService($resource){
	var service = {
		buzzAll: buzzAll,
		buzzProduct: buzzProduct,
		buzzSingle: buzzSingle
	};

	return service;

	function buzzAll() {
        return $resource('/api/buzz/:start', {start:'@start'}, {update:{method:'PUT'}} );
	}

	function buzzProduct() {
        return $resource('/api/buzz/product/:slug/:start', {slug:'@slug', start:'@start'}, {update:{method:'PUT'}} );
	}

	function buzzSingle() {
		return $resource('/api/post/:id', {id:'@id'}, {update:{method:'PUT'}});
	}

}
