angular
    .module('sproutupApp')
    .factory('ContentService', ContentService);

ContentService.$inject = ['$resource'];

function ContentService($resource){
    return $resource('/api/content/:id', {id:'@id'}, {update:{method:'PUT'}} );
}
