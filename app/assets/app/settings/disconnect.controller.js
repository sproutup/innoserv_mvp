(function () {
    'use strict';

    angular
        .module('sproutupApp')
        .controller('DisconnectController', DisconnectController);

    DisconnectController.$inject = ['$scope', '$modalInstance'];

    function DisconnectController($scope, $modalInstance) {
        $scope.disconnect = function() {
            $modalInstance.close('disconnected');
        };

        $scope.cancelDisconnect = function() {
            $modalInstance.dismiss('cancel');
        };
    }

})();
