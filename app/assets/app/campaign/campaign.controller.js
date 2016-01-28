(function () {
  'use strict';

angular
  .module('sproutupApp')
  .controller('CampaignController', CampaignController);

CampaignController.$inject = ['CampaignService', '$state'];

function CampaignController(CampaignService, $state) {
  var vm = this;
  vm.product = {};
  vm.find = find;
  vm.findOne = findOne;
  vm.returnMatch = returnMatch;

  function find() {
     CampaignService.campaign().query({
      }, function(res) {
        vm.campaigns = res;
      }, function(err) {
        console.log(err);
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
