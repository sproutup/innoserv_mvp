(function () {
  'use strict';

angular
  .module('sproutupApp')
  .controller('CampaignTrialController', CampaignTrialController);

CampaignTrialController.$inject = ['CampaignService', '$state'];

function CampaignTrialController(CampaignService, $state) {
  var vm = this;
  vm.find = find;
  vm.findOne = findOne;

  function find() {
    vm.campaigns = CampaignService.campaign().query({
      }, function() {
        console.log('campaigns found');
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
      console.log(vm.campaign);
    }, function(err) {
      //$state.go('landing.default');
      console.log(err);
    });
  }

}

})();