(function () {
  'use strict';

  angular
    .module('sproutupApp')
    .controller('MyActivitiesController', MyActivitiesController);

  MyActivitiesController.$inject = ['$rootScope', 'AuthService'];

  function MyActivitiesController($rootScope, authService) {
    var vm = this;

    activate();

    $rootScope.$watch(authService.m.campaigns, function (value, value2) {
      console.log('in here', this, value, value2);
      if ( value ) {
        console.log(value);
      }
    });

    function activate() {
      if(!authService.ready()) {
        var unbindWatch = $rootScope.$watch(authService.ready, function (value) {
          if ( value === true ) {
            unbindWatch();
            activate();
          }
        });
      }
      else {
        if(authService.m.isLoggedIn) {
          init();
        }
      }
    }

    function init() {
      vm.user = authService.m.user;
      vm.authService = authService;
      setTimeout(function() {
        console.log(authService);
        console.log(vm.authService);
      }, 1000);
    }
  }
})();