angular
    .module('sproutupApp')
    .factory('TrialService', trialService);

TrialService.$inject = ['$resource'];

function TrialService($resource){
    return $resource('/api/trials/:id', {id:'@id'}, {update:{method:'PUT'}} );
}
