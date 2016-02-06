angular
  .module('sproutupApp')
  .config(config);

function config($stateProvider) {

  $stateProvider
    .state('user.discover', {
      url: '/discover',
      abstract: true,
      templateUrl: 'assets/app/campaign/discover.view.html',
      controller: 'CampaignController',
      controllerAs: 'vm',
      onEnter: function(){
        console.log("enter campaign");
      },
      data: {
        title: ''
      }
    })
    .state('user.campaign', {
      url: '/campaign',
      abstract: true,
      template: '<section ng-init="company.findByStateParam()"><div ui-view></div></section>',
      controller: 'CampaignController',
      controllerAs: 'vm',
      onEnter: function(){
        console.log("enter campaign");
      },
      data: {
        title: ''
      }
    })
    .state('user.discover.list', {
      url: '/everything',
      templateUrl: 'assets/app/campaign/list-campaign.view.html',
      data: {
          title: 'Campaign - List'
      }
    })
    .state('user.discover.mine', {
      url: '/my-stuff',
      templateUrl: 'assets/app/campaign/my-campaign.view.html',
      data: {
          title: 'Campaign - List'
      }
    });
}
