(function () {
    'use strict';

angular
    .module('sproutupApp')
    .controller('SuggestController', SuggestController);

// This controller contains logic for main buzz page as well as individual product buzz pages
// Functions loadInit and loadMore have seperate queries for when a slug is present (product buzz page) vs. when there is no slug (main buzz page)

SuggestController.$inject = ['$stateParams', '$state', 'SuggestService'];

function SuggestController($stateParams, $state, SuggestService) {

	console.log(SuggestService.suggestedProducts());

}

})();