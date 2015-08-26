angular
    .module('sproutupApp')
    .factory('PostService', PostService);

PostService.$inject = ['$resource'];

function PostService($resource){
	var service = {
		post: post
	};
	return service;

	function post() {
		return $resource('/api/post/:id', {id:'@id'}, {update:{method:'PUT'}} );
	}
}