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
      templateUrl: 'assets/app/trial/trial.html',
      data: {
        title: 'Campaign - List'
      }
    })
    .state('user.navbar.trial.info', {
      url: '/request/info',
      templateUrl: 'assets/app/trial/info.html',
      data: {
        title: 'Trial - Info'
      }
    })
    .state('user.navbar.trial.connect', {
      url: '/request/connect',
      templateUrl: 'assets/app/trial/request-connect.html',
      data: {
        title: 'Trial - Social'
      }
    });
}