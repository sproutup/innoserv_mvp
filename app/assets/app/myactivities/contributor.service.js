angular
  .module('sproutupApp')
  .factory('ContributorService', ContributorService);

ContributorService.$inject = ['$resource'];

function ContributorService($resource){
  var user = {};

  var service = {
    contributor: contributor
  };

  activate();

  return service;

  function activate() {
    console.log("creator service - activated");
  }

  function contributor () {
    return $resource('/api/creator/user/:userId/campaign/:campaignId', { userId:'@userId', campaignId:'@Id' }, { 'update': {method:'PUT'}, 'query': {method:'GET', isArray:true} } );
  }
}
