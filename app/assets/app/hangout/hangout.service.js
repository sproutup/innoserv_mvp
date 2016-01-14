angular
  .module('sproutupApp')
  .factory('HangoutService', HangoutService);

HangoutService.$inject = ['$resource'];

function HangoutService($resource){
  var user = {};

  var service = {
    hangout: hangout
  };

  activate();

  return service;

  function activate() {
    console.log("hangout service - activated");
  }

  function hangout () {
    return $resource('/api/creator/hangout/:hangoutId', {hangoutId:'@id'}, { 'update': {method:'PUT'}, 'query': {method:'GET', isArray:true} } );
  }
}
