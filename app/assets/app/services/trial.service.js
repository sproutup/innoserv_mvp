angular
    .module('sproutupApp')
    .factory('TrialService', trialService);

trialService.$inject = ['$resource'];

function trialService($resource){
    return $resource('/api/trial/product/:id', {id:'@id'}, {update:{method:'PUT'}} ); // Note the full endpoint address
}
