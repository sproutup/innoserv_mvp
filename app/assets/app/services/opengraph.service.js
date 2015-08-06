// factory
angular
    .module('sproutupApp')
    .factory('OpenGraphService', OpenGraphService);

OpenGraphService.$inject = ['$resource'];

function OpenGraphService($resource){
    return $resource('/api/openGraph/:id', {id:'@id'}, {
        parse: {
            url:'api/openGraph/parse/:url',
            params:{url:""},
            method: 'GET'
        }
    });
}
