(function () {
    'use strict';

angular
    .module('sproutupApp')
    .controller('SearchController', SearchController);

SearchController.$inject = ['ProductServicex'];

function SearchController(productService) {
    var vm = this;

    vm.products = {};
    vm.query = '';

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