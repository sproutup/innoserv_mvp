angular
    .module('sproutupApp')
    .directive('upContent', upContent);

function upContent() {
    var directive = {
        restrict: 'EA',
        templateUrl: 'assets/app/content/up-content.html',
        scope: {
            content: "="
        },
        link: linkFunc,
        controller: UpContentController,
        controllerAs: 'vm',
        bindToController: true
    };

    return directive;

    function linkFunc(scope, element, attr, ctrl) {
        
    }    
}

function UpContentController() {
    var vm = this;
    vm.likes = content.likes;
}