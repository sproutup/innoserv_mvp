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
    })
    .state('user.activity', {
      url: '/activity/:campaignId',
      controller: 'MyActivitiesController',
      controllerAs: 'vm',
      template: '<ui-view ng-init="vm.findOne()"></ui-view>',
      data: {
        title: 'My Activities'
      }
    })
    .state('user.activity.trial', {
      url: '/trial',
      template: '<div ui-view></div>',
      abstract: true
    })
    .state('user.activity.trial.confirmation', {
      url: '/confirmation',
      templateUrl: 'assets/app/myactivities/trial-confirmation.view.html',
      data: {
        title: 'Confirmation'
      }
    })
    .state('user.activity.trial.edit', {
      url: '/edit',
      templateUrl: 'assets/app/myactivities/trial-edit.view.html',
      data: {
        title: 'Edit'
      }
    })
    .state('user.activity.contest', {
      url: '/contest',
      template: '<div ui-view></div>',
      abstract: true
    })
    .state('user.activity.contest.confirmation', {
      url: '/confirmation',
      templateUrl: 'assets/app/myactivities/contest-confirmation.view.html',
      data: {
        title: 'Confirmation'
      }
    })
    .state('user.activity.contest.edit', {
      url: '/edit',
      templateUrl: 'assets/app/myactivities/contest-edit.view.html',
      data: {
        title: 'Edit'
      }
    });
}
