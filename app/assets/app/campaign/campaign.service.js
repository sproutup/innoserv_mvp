angular
  .module('sproutupApp')
  .factory('CampaignService', CampaignService);

CampaignService.$inject = ['$resource'];

function CampaignService($resource){
  var user = {};

  var service = {
    campaign: campaign,
    listByUser: listByUser
  };

  activate();

  return service;

  function activate() {
    console.log("creator service - activated");
  }

  function campaign () {
    return $resource('/api/creator/campaign/:campaignId', { campaignId:'@id' }, { 'update': {method:'PUT'}, 'query': {method:'GET', isArray:true} } );
  }

  function listByUser () {
    return $resource('/api/creator/user/:userId/campaign', { userId:'@id' }, { 'update': {method:'PUT'}, 'query': {method:'GET', isArray:true} } );
  }
}
