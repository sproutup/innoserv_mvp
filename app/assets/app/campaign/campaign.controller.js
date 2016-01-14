(function () {
  'use strict';

angular
  .module('sproutupApp')
  .controller('CampaignController', CampaignController);

CampaignController.$inject = ['CampaignService'];

function CampaignController(CampaignService) {
  var vm = this;
  vm.product = {};
  vm.find = find;

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
    }, function(err) {
      //$state.go('landing.default');
      console.log(err);
    });
  }

}

})();
