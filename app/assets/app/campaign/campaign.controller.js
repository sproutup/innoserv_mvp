(function () {
  'use strict';

angular
  .module('sproutupApp')
  .controller('CampaignController', CampaignController);

CampaignController.$inject = ['CampaignService', '$state', 'AuthService', '$scope'];

function CampaignController(CampaignService, $state, AuthService, $scope) {
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
    if (!AuthService.ready()) {
      var listener = $scope.$watch(AuthService.ready, function(val) {
        if(val) {
          listener();
          findMyCampaigns();
        }
      });
      return;
    }

    CampaignService.listMyContributions().query({
      userId: AuthService.m.user.id
    }, function(data) {
      vm.myCampaigns = data;
      for (var i = 0; i < vm.myCampaigns.length; i++) {
        sortLog(vm.myCampaigns[i]);
      }
    }, function(error) {
      console.log(error);
    });
  }

  function sortLog(campaign) {
    if (campaign.log) {
      // Find approved logs
      campaign.approved = campaign.log.filter(function(item) {
        return item.state === 1;
      });

      // Find completed logs
      campaign.completed = campaign.log.filter(function(item) {
        return item.state === 10 || item.state === '10';
      });
    }
  }

  function findOne(campaignId) {
    vm.success = false;
    var _id = null;

    if(campaignId){
      _id = campaignId;
    }
    else{
      _id = $state.params.campaignId;
    }

    var campaign = CampaignService.campaign().get({
      campaignId: _id
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
