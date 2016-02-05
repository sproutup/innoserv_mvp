(function () {
  'use strict';

angular
  .module('sproutupApp')
  .controller('CampaignController', CampaignController);

CampaignController.$inject = ['CampaignService', '$state', 'AuthService'];

function CampaignController(CampaignService, $state, AuthService) {
  var vm = this;
  vm.product = {};
  vm.find = find;
  vm.findOne = findOne;
  vm.findMyCampaigns = findMyCampaigns;
  vm.returnMatch = returnMatch;

  function find() {
     CampaignService.campaign().query({
      }, function(res) {
        vm.campaigns = res;
      }, function(err) {
        console.log(err);
      });
  }

  function findMyCampaigns() {
    CampaignService.listMyContributions().query({
      userId: AuthService.m.user.id
    }).$promise.then(function(data) {
      vm.myCampaigns = data;
    },
    function(error) {
      console.log(error);
    });
  }

  function findOne() {
    vm.success = false;

    var campaign = CampaignService.campaign().get({
      campaignId: $state.params.campaignId
    }, function() {
      vm.campaign = campaign;
    }, function(err) {
      //$state.go('landing.default');
      console.log(err);
    });
  }

  function returnMatch(actual, expected) {
    if (!expected) {
       return true;
    }
    return angular.equals(expected, actual);
  }

}

})();
