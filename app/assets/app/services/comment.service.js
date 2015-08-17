angular
    .module('sproutupApp')
    .factory('CommentService', CommentService);

CommentService.$inject = ['$resource'];

function CommentService($resource){
    return $resource('/api/comment/:refType/:refId/:id', {id:'@id'}, {update:{method:'PUT'}} );
}
