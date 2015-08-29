angular
    .module('sproutupApp')
    .factory('CommentService', CommentService);

CommentService.$inject = ['$resource'];

function CommentService($resource){
	var service = {
		comment: comment
	};
	return service;

	function comment(refType, refId) {
		return $resource('/api/comment/:refType/:refId/:commentId', {refType: refType, refId: refId, commentId: '@id'} );
	}
}