angular
    .module('sproutupApp')
    .directive('upProductCard', upProductCard);


function upProductCard() {
    var directive = {
        restrict: 'EA',
        templateUrl: 'assets/app/products/product-card.html',
        scope: {
            product: "="
        },
        link: linkFunc,
        controller: upProductCardController,
        controllerAs: 'vm',
        bindToController: true // because the scope is isolated
    };

    return directive;

    function linkFunc(scope, el, attr, ctrl) {
        attr.$observe('product', function(){
            console.log('prod scope vm ', scope.vm.product);
        });
    }
}

//upProductCardController.$inject = [];

function upProductCardController() {
    var vm = this;

    vm.getActiveRequests = getActiveRequests;

    console.log('product: ', vm.product);

    activate();

    function activate() {
    }

    function getActiveRequests() {
        if (typeof vm.product.trials !== 'undefined') {
            return vm.product.trials.filter(function (element) {
                return element.status >= 0;
            });
        }
        else return [];
    }

}
