(function () {
  'use strict';

  angular
    .module('sproutupApp')
    .controller('MyActivitiesController', MyActivitiesController);

  MyActivitiesController.$inject = ['$scope', 'AuthService', 'ContributorService', '$state'];

  function MyActivitiesController($scope, authService, ContributorService, $state) {
    var vm = this;
    vm.findOne = findOne;

    function findOne() {
      if (!authService.ready()) {
        var listener = $scope.$watch(authService.ready, function(val) {
          if(val) {
            listener();
            findOne();
          }
        });
        return;
      }

      vm.success = false;
      var item = ContributorService.contributor().get({
        userId: authService.m.user.id,
        campaignId: $state.params.campaignId
      }, function() {
        vm.item = item;
      }, function(err) {
        console.log(err);
      });
    }
  }

})();
