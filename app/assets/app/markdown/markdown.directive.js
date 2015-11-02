angular
    .module('sproutupApp')
    .directive('upMarkdown', upMarkdown);

function upMarkdown() {
    var directive = {
        restrict: 'EA',
        link: linkFunc,
        controller: upMarkdownController,
        controllerAs: 'vm',
        bindToController: true // because the scope is isolated
    };

    return directive;

    function linkFunc(scope, el, attr) {
        // console.log('LINK: scope.vm.likes: ', scope.vm.likes);
        // console.log('LINK: scope.vm.id = %s', scope.vm.id);
        console.log("## markdown ##", attr.upMarkdown);
        if (attr.upMarkdown) {
          scope.$watch(attr.upMarkdown, function (newVal) {
            var html = newVal ? scope.vm.makeHtml(newVal) : '';
            console.log("## html ##", html);
            el.html(html);
          });
        } else {
          var html = scope.vm.makeHtml(el.text());
          console.log("## html ##", html);
          el.html(html);
        }

    }
}

upMarkdownController.$inject = ['$sanitize'];

function upMarkdownController($sanitize) {
    var vm = this;

    vm.converter = new showdown.Converter();
    vm.makeHtml = makeHtml;

    function makeHtml(markdown){
        console.log("## makehtml ##", markdown);
        return $sanitize(vm.converter.makeHtml(markdown));
    }
}

