angular
  .module('sproutupApp')
  .config(config);

function config($stateProvider) {

  $stateProvider
    .state('user.my-activities', {
      url: '/my-activities',
      controller: 'MyActivitiesController',
      controllerAs: 'vm',
      templateUrl: 'assets/app/myactivities/my-activities.view.html',
      data: {
        title: 'My Activities'
      }
    });
}