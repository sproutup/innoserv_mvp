angular
    .module('sproutupApp')
    .config(config);

function config($stateProvider) {
  $stateProvider
    .state('user.navbar.trial', {
      url: '/trials/:campaignId',
      abstract: true,
      template: '<div ui-view><div>',
      controller: 'CampaignTrialController',
      controllerAs: 'vm',
      data: {
        title: ''
      }
    })
    .state('user.navbar.trial.view', {
      url: '',
      abstract: true,
      templateUrl: 'assets/app/trial/trial.html'
    })
    .state('user.navbar.trial.view.details', {
      url: '',
      templateUrl: 'assets/app/trial/trial-details.html',
      data: {
        title: 'Campaign - Details'
      }
    })
    .state('user.navbar.trial.view.buzz', {
      url: '/buzz',
      templateUrl: 'assets/app/trial/trial-buzz.html',
      data: {
        title: 'Campaign - Buzz'
      }
    })
    .state('user.navbar.trial.info', {
      url: '/request/info',
      templateUrl: 'assets/app/trial/info.html',
      data: {
        title: 'Trial - Info'
      }
    })
    .state('user.navbar.trial.edit', {
      url: '/request/edit/:userId',
      templateUrl: 'assets/app/trial/edit.html',
      data: {
        title: 'Trial - Info'
      }
    })
    .state('user.navbar.trial.connect', {
      url: '/request/connect',
      templateUrl: 'assets/app/trial/social-connection.html',
      data: {
        title: 'Trial - Social'
      }
    });
}