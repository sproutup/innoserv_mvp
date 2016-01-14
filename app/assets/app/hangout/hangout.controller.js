(function () {
  'use strict';

angular
  .module('sproutupApp')
  .controller('HangoutController', HangoutController);

HangoutController.$inject = ['HangoutService'];

function HangoutController(HangoutService) {
  var vm = this;
  vm.item = {};
  vm.list = [];
  vm.find = find;

  function find() {
    vm.list = HangoutService.hangout().query({
      }, function() {
        console.log('hangouts found');
      }, function(err) {
        console.log(err);
      });
  }

  function findOne() {
    vm.success = false;

    vm.item = HangoutService.hangout().get({
      campaignId: $state.params.hangoutId
    }, function(val) {
    }, function(err) {
      //$state.go('landing.default');
      console.log(err);
    });
  }

}

})();
