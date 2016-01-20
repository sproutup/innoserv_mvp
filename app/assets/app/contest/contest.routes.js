angular
    .module('sproutupApp')
    .config(config);

function config($stateProvider) {
  $stateProvider
    .state('user.navbar.contest', {
      url: '/contests',
      abstract: true,
      template: '<div ui-view><div>',
      controller: 'CampaignContestController',
      controllerAs: 'vm',
      data: {
        title: ''
      }
    })
    .state('user.navbar.contest.view', {
      url: '/:campaignId',
      templateUrl: 'assets/app/contest/contest.html',
      data: {
        title: 'Campaign - List'
      }
    });
}