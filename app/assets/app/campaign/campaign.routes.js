angular
  .module('sproutupApp')
  .config(config);

function config($stateProvider) {

  $stateProvider
    .state('user.campaign', {
      url: '/campaign',
      abstract: true,
      templateUrl: 'assets/app/campaign/campaign.view.html',
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
      url: '/everything',
      templateUrl: 'assets/app/campaign/list-campaign.view.html',
      data: {
          title: 'Campaign - List'
      }
    })
    .state('user.campaign.mine', {
      url: '/my-stuff',
      templateUrl: 'assets/app/campaign/my-campaign.view.html',
      data: {
          title: 'Campaign - List'
      }
    });
}
