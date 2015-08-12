(function () {
    'use strict';

angular
    .module('sproutupApp')
    .controller('ContentController', ContentController);

ContentController.$inject = ['$stateParams', '$state', 'AuthService'];

function ContentController($stateParams, $state, authService) {
    var vm = this;

}

})();