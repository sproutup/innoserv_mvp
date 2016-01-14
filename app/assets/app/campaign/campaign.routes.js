angular
  .module('sproutupApp')
  .config(config);

function config($stateProvider) {

  $stateProvider
    .state('user.campaign', {
      url: '/campaign',
      abstract: true,
      template: '<div ui-view><div>',
      controller: 'CampaignController',
      controllerAs: 'vm',
      onEnter: function(){
        console.log("enter campaign");
      },
      data: {
        title: ''
      }
    })
    .state('user.campaign.list', {
      url: '/list',
      templateUrl: 'assets/app/campaign/list-campaign.view.html',
      data: {
          title: 'Campaign - List'
      }
    });
}
