angular
    .module('sproutupApp')
    .factory('FeedService', FeedService);

FeedService.$inject = ['$resource'];

function FeedService($resource){
    return $resource('/api/feed/:start', {start:'@start'}, {update:{method:'PUT'}} );
}
