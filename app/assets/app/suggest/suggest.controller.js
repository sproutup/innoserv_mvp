(function () {
    'use strict';

angular
    .module('sproutupApp')
    .controller('SuggestController', SuggestController);

// This controller contains logic for main buzz page as well as individual product buzz pages
// Functions loadInit and loadMore have seperate queries for when a slug is present (product buzz page) vs. when there is no slug (main buzz page)

SuggestController.$inject = ['$stateParams', '$state', 'SuggestService', 'AuthService'];

function SuggestController($stateParams, $state, SuggestService, AuthService) {
	var vm = this;
	vm.products = SuggestService.suggestedProducts();
	vm.suggestProduct = suggestProduct;

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
        if (vm.user.id) {
            vm.myTrialProducts = MyTrialProductsService.query();
        }
    }

	function suggestProduct() {
		var suggestion = {
			product: {
				name: vm.suggestProductName,
				url: vm.suggestedProductUrl
			},
			user: AuthService.m.user
		};

		vm.products.push(suggestion);

	}

}

})();