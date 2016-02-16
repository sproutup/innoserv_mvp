angular
  .module('sproutupApp')
  .directive('upPostContent', upPostContent);

function upPostContent() {
  var directive = {
    require: 'ngModel',
    scope: {
      min: '=',
      max: '=',
      ngModel: '=',
      ngDisabled: '='
    },
    controller: upPostContentController,
    controllerAs: 'vm',
    bindToController: true,
    link: linkFunc,
    templateUrl: 'assets/app/buzz/up-post-content.template.html'
  };

  return directive;

  function linkFunc(scope, element, attrs, ngModel) {
    scope.item = {
      body: '',
      media: '',
      ref: ''
    };
    ngModel.$valid = false;

    scope.selectVideo = selectVideo;
    scope.removeVideo = removeVideo;

    // Check which social networks you can post with
    if (attrs.networks) {
      scope.youtube = attrs.networks.indexOf('yt') > -1;
      scope.facebook = attrs.networks.indexOf('fb') > -1;
      scope.twitter = attrs.networks.indexOf('tw') > -1;
      scope.instagram = attrs.networks.indexOf('ig') > -1;
      scope.googleanalytics = attrs.networks.indexOf('ga') > -1;
      scope.url = attrs.networks.indexOf('url') > -1;
    }

    ngModel.$valid = false;
    scope.vm.contentState = 'select';

    scope.showYouTubeVideos = function(){
      scope.vm.showYouTubeVideos(function(items){
        scope.vm.contentState = 'youtube';
        scope.videos = items;
      });
    };

    function selectVideo(video) {
      scope.selectedVideo = video;
      scope.item.media = 'yt';
      scope.item.ref = video.id.videoId;
      scope.item.title = video.snippet.title;
      scope.vm.contentState = 'write';
      scope.onChange();
    }

    function removeVideo() {
      scope.selectedVideo = {};
      scope.vm.contentState = 'select';
      scope.onChange();
    }

    scope.onChange = function(){
      ngModel.$setViewValue(scope.item);
    };

    ngModel.$render = function() {
      scope.item = ngModel.$modelValue;
    };
  }
}

upPostContentController.$inject = ['YouTubeService', 'AuthService'];

function upPostContentController(YouTubeService, AuthService) {
  var vm = this;

  vm.showYouTubeVideos = function() {
    YouTubeService.videos().get({
      userId: AuthService.m.user.id
    }, function(res) {
      vm.contentState = 'youtube';
      vm.videos = res.items;
    }, function(err) {
      console.log('err here', err);
    });
  };
}
