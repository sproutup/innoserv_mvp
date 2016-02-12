angular
  .module('sproutupApp')
  .directive('upPostContent', upPostContent);

function upPostContent() {
  var directive = {
    require: 'ngModel',
    link: linkFunc,
    //template: '<textarea ng-model="vm.post.body" name="content" placeholder="Write your post here..." class="form-control post-new-textarea link" required></textarea>',
    templateUrl: 'assets/app/buzz/up-post-content.template.html'
  };

  return directive;

  function linkFunc(scope, element, attrs, ngModel) {
    var vm = scope.vm;
    vm.selectVideo = selectVideo;
    vm.removeVideo = removeVideo;
    vm.post = {
      body: '',
      media: '',
      ref: ''
    };

    // Check which social networks you can post with
    if (attrs.networks) {
      vm.youtube = attrs.networks.indexOf('yt') > -1;
      vm.facebook = attrs.networks.indexOf('fb') > -1;
      vm.twitter = attrs.networks.indexOf('tw') > -1;
      vm.instagram = attrs.networks.indexOf('ig') > -1;
      vm.googleanalytics = attrs.networks.indexOf('ga') > -1;
      vm.url = attrs.networks.indexOf('url') > -1;
    }

    ngModel.$valid = false;
    vm.contentState = 'select';

    function selectVideo(video) {
      vm.selectedVideo = video;
      vm.post.media = 'yt';
      vm.post.ref = video.id.videoId;
      vm.post.title = video.snippet.title;
      vm.contentState = 'write';
    }

    function removeVideo() {
      vm.selectedVideo = {};
      vm.contentState = 'select';
    }

    // vm.model = ngModel;
    // vm.post.body = ngModel.body;
    // vm.post.group = attrs.group;

    ngModel.$render = function() {
      element.val(ngModel.$modelValue);
      element.change();
    };
  }
}
