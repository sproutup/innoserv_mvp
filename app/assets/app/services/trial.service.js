angular
    .module('sproutupApp')
    .factory('TrialService', TrialService);

TrialService.$inject = ['$resource'];

function TrialService($resource){
    return $resource('/api/trials/:id', {id:'@id'}, {update:{method:'PUT'}} );
}
