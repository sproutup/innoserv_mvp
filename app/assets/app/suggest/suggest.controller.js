(function () {
    'use strict';

angular
    .module('sproutupApp')
    .controller('SuggestController', SuggestController);

// This controller contains logic for main buzz page as well as individual product buzz pages
// Functions loadInit and loadMore have seperate queries for when a slug is present (product buzz page) vs. when there is no slug (main buzz page)

SuggestController.$inject = ['$rootScope', '$stateParams', '$state', 'SuggestService', 'AuthService', 'usSpinnerService', '$timeout'];

function SuggestController($rootScope, $stateParams, $state, SuggestService, AuthService, usSpinnerService, $timeout) {
	var vm = this;
	vm.suggestProduct = suggestProduct;
    vm.toggleSuggestProduct = toggleSuggestProduct;
    vm.loadProducts = loadProducts;
    var position = 10;
    var products = [];
    vm.busy = true;


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
        products = SuggestService.suggestedProducts().query({
            start: 0
        }, function() {
            vm.products = products;
            $timeout(function(){vm.busy = false;}, 1000);
        });
    }

    activate();

    function loadProducts() {
        vm.busy = true;
        products = SuggestService.suggestedProducts().query({
            start: position
        }, function() {
            for (var p = 0; p < products.length; p++) {
                vm.products.push(products[p]);
            }
            if (products.length === 10) {
                position += 10;
                $timeout(function(){vm.busy = false;}, 1000);
            }
        });
    }

    function toggleSuggestProduct() {
        if (!AuthService.loggedIn()) {
            $scope.$emit('LoginEvent', {
                someProp: 'Sending you an Object!' // send whatever you want
            });
            return;
        }
        vm.suggestingProduct = !vm.suggestingProduct;
    }

    function suggestProduct(event) {
        vm.error = false;
        vm.disabled = true;
        usSpinnerService.spin('spinner-4');
        SuggestService.addSuggestedProduct(vm.suggestedProductUrl, vm.suggestProductName).then(function(result) {
            vm.products.unshift(result);
            usSpinnerService.stop('spinner-4');
            vm.suggestedProductUrl = null;
            vm.suggestProductName = null;
            vm.suggestingProduct = false;
            $rootScope.eventObj = {
                x: event.pageX,
                y: event.pageY
            };
            vm.disabled = false;
            vm.error = false;
            AuthService.refreshPoints();
        }, function(reason) {
            usSpinnerService.stop('spinner-4');
            vm.disabled = false;
            vm.error = true;
        });
    }

}

})();