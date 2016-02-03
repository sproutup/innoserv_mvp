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
    vm.post = {
      body: '',
      group: ''
    };
    ngModel.$valid = false;
    vm.status = 'select';

    vm.model = ngModel;
    vm.post.body = ngModel.body;
    vm.post.group = attrs.group;

    ngModel.$render = function() {
      element.val(ngModel.$modelValue);
      element.change();
    };
  }
}
