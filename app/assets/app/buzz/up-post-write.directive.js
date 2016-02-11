angular
  .module('sproutupApp')
  .directive('upPostWrite', upPostWrite);

function upPostWrite() {
  var directive = {
    require: '?ngModel',
    link: linkFunc,
    template: '<textarea ng-model="vm.post.body" name="content" placeholder="What do you have in mind? A cool product video? Good stuff for others to bite on?" class="form-control post-new-textarea link" required></textarea>'
  };

  return directive;

  function linkFunc(scope, element, attrs, ngModel) {
    var vm = scope.vm;
    vm.post = {
      body: '',
      group: ''
    };
    ngModel.$valid = false;

    vm.model = ngModel;
    vm.post.body = ngModel.body;
    vm.post.group = attrs.group;

    ngModel.$render = function() {
      element.val(ngModel.$modelValue);
      element.change();
    };
  }
}