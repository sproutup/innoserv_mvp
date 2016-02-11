(function () {
  'use strict';

angular
  .module('sproutupApp')
  .controller('CampaignTrialController', CampaignTrialController);

CampaignTrialController.$inject = ['CampaignService', '$state', 'AuthService', '$scope', 'YouTubeService'];

function CampaignTrialController(CampaignService, $state, AuthService, $scope, YouTubeService) {
  var vm = this;
  vm.find = find;
  vm.findOne = findOne;
  vm.submitRequest = submitRequest;
  vm.connected = connected;
  vm.showYouTubeVideos = showYouTubeVideos;

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

    CampaignService.campaign().get({
      campaignId: $state.params.campaignId
    }, function(res) {
      vm.campaign = res;
    }, function(err) {
      //$state.go('landing.default');
      console.log(err);
    });
  }

  function submitRequest() {
    if (!AuthService.ready()) {
      var listener = $scope.$watch(AuthService.ready, function(val) {
        if(val) {
          listener();
          findOne();
        }
      });
      return;
    }

    CampaignService.contributor().save({
      userId: AuthService.m.user.id,
      campaignId: vm.campaign.id,
      address: vm.address,
      phone: vm.phone,
      comment: vm.comment,
      bid: vm.bid
    }, function(res) {
      $state.go('user.activity.trial.confirmation', {campaignId: vm.campaign.id});
    }, function(err) {
      vm.error = true;
    });
  }

  function connected() {
    $state.go('user.navbar.trial.view', {campaignId: vm.campaign.id});
  }

  function showYouTubeVideos() {
    vm.status = 'youtube';
    getVideos();
  }

  function getVideos() {
    YouTubeService.videos().get({
      userId: AuthService.m.user.id
    }, function(res) {
      vm.videos = res.items;
    }, function(err) {
      console.log('err here', err);
    });
  }
}

})();