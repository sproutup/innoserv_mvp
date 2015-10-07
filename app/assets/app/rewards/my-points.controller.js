(function() {

angular
    .module('sproutupApp')
    .controller('myPointsController', myPointsController);

myPointsController.$inject = ['$scope'];

function myPointsController($scope) {
    console.log('ey');
}

})();