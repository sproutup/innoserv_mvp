(function () {
  'use strict';

  angular
    .module('sproutupApp')
    .controller('MyActivitiesController', MyActivitiesController);

  MyActivitiesController.$inject = ['$rootScope', 'AuthService'];

  function MyActivitiesController($rootScope, authService) {
    var vm = this;

    activate();

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
    }
  }
})();