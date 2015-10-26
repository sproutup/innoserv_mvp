angular
    .module('sproutupApp')
    .directive('upBuyButton', upBuyButton);

function upBuyButton() {
    var directive = {
        restrict: 'EA',
        templateUrl: 'assets/app/shop/up-buy-button.html',
        scope: {
            handle: '@'
        },
        link: linkFunc,
        controller: upBuyButtonController,
        controllerAs: 'vm',
        bindToController: true // because the scope is isolated
    };

    return directive;

    function linkFunc(scope, el, attr, ctrl) {

    }
}

upBuyButtonController.$inject = ['$state'];

function upBuyButtonController($state) {

}