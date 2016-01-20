angular
    .module('sproutupApp')
    .config(config);

function config($stateProvider) {
  $stateProvider
    .state('user.navbar.trial', {
      url: '/trials',
      abstract: true,
      template: '<div ui-view><div>',
      controller: 'CampaignTrialController',
      controllerAs: 'vm',
      data: {
        title: ''
      }
    })
    .state('user.navbar.trial.view', {
      url: '/:campaignId',
      templateUrl: 'assets/app/trial/trial.html',
      data: {
        title: 'Campaign - List'
      }
    });
}