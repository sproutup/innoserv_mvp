(function () {
    'use strict';

angular
    .module('sproutupApp')
    .controller('ProductController', ProductController);

ProductController.$inject = ['ProductServicex'];

function ProductController(productService) {
    var vm = this;

    vm.product = {};

    activate();

    function activate() {
        productService.query().then(
            function(result){
                console.log("product result: ", result);
                vm.products = result;
                for (var i = 0; i < vm.products.data.length; i++) {
                    vm.products.data[i].random = Math.random();
                }
            }
        );
    }
}

})();