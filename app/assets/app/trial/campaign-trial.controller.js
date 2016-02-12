(function () {
  'use strict';

angular
  .module('sproutupApp')
  .controller('CampaignTrialController', CampaignTrialController);

CampaignTrialController.$inject = ['CampaignService', '$state', 'AuthService', '$scope', 'YouTubeService', 'PostService', 'usSpinnerService', 'ContentService'];

function CampaignTrialController(CampaignService, $state, AuthService, $scope, YouTubeService, PostService, usSpinnerService, ContentService) {
  var vm = this;
  vm.find = find;
  vm.findOne = findOne;
  vm.submitRequest = submitRequest;
  vm.connected = connected;
  vm.createPost = createPost;
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

  // TODO — Put create post and create content into services
  function createPost() {
    vm.posting = true;
    var Post = PostService.post();
    var item = new Post({
      userId: AuthService.m.user.id, // Remove this after we add it server side
      body: vm.post.body,
      groupId: $scope.vm.campaign.groupId
    });

    // Add youtube link to the body
    if (vm.post.ref && vm.post.media === 'yt') {
      item.body = 'https://www.youtube.com/watch?v=' + vm.post.ref + '\n' + vm.post.body;
    }

    usSpinnerService.spin('spinner-1');

    item.$save(function(res) {
      vm.posting = false;
      vm.post = {};
      // vm.content.unshift(res);
      usSpinnerService.stop('spinner-1');
    }, function(err) {
      vm.posting = false;
      usSpinnerService.stop('spinner-1');
    });

    if (vm.post.media) {
      saveContent();
    }
  }

  function saveContent() {
    var Content = ContentService.content();
    var contentItem = new Content({
      media: vm.post.media,
      ref: vm.post.ref,
      campaignId: $state.params.campaignId,
      title: vm.post.title
    });

    contentItem.$save();
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