angular
    .module('sproutupApp')
    .directive('upSuggest', upSuggest);

function upSuggest() {
    var directive = {
        restrict: 'EA',
        templateUrl: 'assets/app/suggest/up-suggested-product.html',
        scope: {
            product: "=",
            state: "@",
            params: "="
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

upSuggestController.$inject = ['$state'];

function upSuggestController($state) {

}