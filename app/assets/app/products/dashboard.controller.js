(function () {
    'use strict';

angular
    .module('sproutupApp')
    .controller('ProductDashboardController', ProductDashboardController);

ProductDashboardController.$inject = ['$q', '$rootScope', '$stateParams', '$state', '$log', 'ProductService', 'AuthService'];

function ProductDashboardController($q, $rootScope, $stateParams, $state, $log, productService, authService) {
    $log.debug("entered product details ctrl. slug=" + $stateParams.slug);
    var vm = this;

    vm.update = update;
    vm.cancel = cancel;
    vm.isThisMyProduct = false;
    vm.product = {};
    vm.ready = authService.ready;

    activate();

    function activate() {
        if(!authService.ready()){
            var unbindWatch = $rootScope.$watch(authService.ready, function (value) {
                if ( value === true ) {
                  unbindWatch();
                  activate();
                }
            });
        }
        else{
            if(authService.m.isLoggedIn) {
                var user = authService.m.user;
                if (user.company.products === undefined) {
                    $state.go("home");
                }
                for (var i = 0; i < user.company.products.length; i++) {
                    if (user.company.products[i].slug == $stateParams.slug) {
                        vm.isThisMyProduct = true;
                    }
                }
            }

            if(vm.isThisMyProduct === false){
                $state.go("home");
            }

            var promises = [getProduct()];
            return $q.all(promises).then(function() {
                $log.info('Activated Dashboard View');
            });
        }
    }

    function getProduct() {
        return productService.get({slug: $stateParams.slug}).$promise.then(function (data) {
            vm.product = data;
            return vm.product;
        });
    }

    function update(){
        console.log("product update", vm.product);

        vm.product.$save({},
            function(data) {
                // success
                console.log("product update success");
                authService.getUser().then(
                    function(result){
                        $rootScope.$broadcast('alert:success', {
                            message: 'Product updated'
                        });
                        // just in case the slug has changed
                        $state.go('dashboard.products.info', {slug: data.slug} );
                    }
                );
            },
            function(error) {
                // error handler
                console.log("product update error");
                $rootScope.$broadcast('alert:error', {
                    message: 'Product update failed'
                });
            }
        );
    }

    function cancel(){
        console.log("product cancel");
        $state.go('home');
    }
}

})();
