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

UpContentController.$inject = ['AuthService', '$rootScope', '$scope'];

function UpContentController(AuthService, $rootScope, $scope) {
    var vm = this;
    vm.likes = vm.content.likes;

    vm.commentToggle = function() {
        if (!vm.content.commenting) {
            vm.content.commenting = true;
        } else {
            vm.content.commenting = false;
        }
    };

}