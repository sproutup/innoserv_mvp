angular
    .module('sproutupApp')
    .directive('upSuggest', upSuggest);

function upSuggest() {
    var directive = {
        restrict: 'EA',
        templateUrl: 'assets/app/suggest/up-suggested-product.html',
        scope: {
            product: "="
        },
        link: linkFunc,
        controller: upSuggestController,
        controllerAs: 'vm',
        bindToController: true // because the scope is isolated
    };

    return directive;

    function linkFunc(scope, el, attr, ctrl) {

    }
}

upSuggestController.$inject = ['$state', '$scope', 'AuthService'];

function upSuggestController($state, $scope, AuthService) {
    var vm = this;
    vm.commentToggle = commentToggle;

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

    activate();

    if (vm.product.user.id === vm.user.id) {
        vm.shareLink = 'https://twitter.com/intent/tweet' +
                       '?text=I suggested ' + vm.product.name + ' on SproutUp—http://sproutup.co/suggest';
    } else {
        vm.shareLink = 'https://twitter.com/intent/tweet' +
                       '?text=' + vm.product.user.name + ' suggested ' + vm.product.name + ' on SproutUp—http://sproutup.co/suggest';
    }

    function commentToggle() {
        if (!vm.content.commenting) {
            vm.content.commenting = true;
        } else {
            vm.content.commenting = false;
        }
    }
}