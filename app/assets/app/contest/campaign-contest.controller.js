(function () {
  'use strict';

angular
  .module('sproutupApp')
  .controller('CampaignContestController', CampaignContestController);

CampaignContestController.$inject = ['CampaignService', '$state', 'AuthService', '$rootScope'];

function CampaignContestController(CampaignService, $state, AuthService, $rootScope) {
  var vm = this;
  vm.find = find;
  vm.findOne = findOne;
  vm.join = join;

  activate();

  function activate() {
    if(!AuthService.ready()){
      var unbindWatch = $rootScope.$watch(AuthService.ready, function (value) {
        if ( value === true ) {
          unbindWatch();
          activate();
        }
      });
    }
    else {
      init();
    }
  }

  function init() {
    vm.user = angular.copy(AuthService.m.user);
  }

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

  function join() {
    CampaignService.contributor().save({
      userId: vm.user.id,
      campaignId: vm.campaign.id
    }, function(res) {
      console.log(res);
      $state.go('user.activity.contest.edit', {campaignId: vm.campaign.id});
    }, function(err) {
      //$state.go('landing.default');
      console.log(err);
    });
  }

}

})();
