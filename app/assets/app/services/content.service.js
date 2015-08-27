angular
    .module('sproutupApp')
    .factory('ContentService', ContentService);

ContentService.$inject = ['$resource'];

function ContentService($resource){
	var service = {
		content: content
	};
	return service;

	function content() {
		return $resource('/api/content/:id', {id:'@id'}, {update:{method:'PUT'}} );
	}
}