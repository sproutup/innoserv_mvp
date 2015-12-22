(function () {
    'use strict';

angular
    .module('sproutupApp')
    .controller('SuggestController', SuggestController);

// This controller contains logic for main buzz page as well as individual product buzz pages
// Functions loadInit and loadMore have seperate queries for when a slug is present (product buzz page) vs. when there is no slug (main buzz page)

SuggestController.$inject = ['$rootScope', '$stateParams', '$state', 'SuggestService', 'AuthService', 'usSpinnerService'];

function SuggestController($rootScope, $stateParams, $state, SuggestService, AuthService, usSpinnerService) {
	var vm = this;
	// vm.products = SuggestService.suggestedProducts();
	vm.suggestProduct = suggestProduct;
    vm.toggleSuggestProduct = toggleSuggestProduct;

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
        SuggestService.suggestedProducts().then(function(data) {
            vm.products = data;
        });
    }

    activate();

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