angular
    .module('sproutupApp')
    .config(config);

function config($stateProvider) {
    $stateProvider
        .state('user.theme', {
          url: '/theme',
          abstract: true,
          template: '<ui-view/>',
          controller: '',
          controllerAs: ''
        })
        .state('user.theme.index' ,{
            url: '',
            templateUrl: 'assets/app/theme/theme-index.html'
        })
        .state('user.theme.mocks', {
          url: '/mocks',
          abstract: true,
          template: '<ui-view/>'
        })
        .state('user.theme.mocks.hangout', {
          url: '/hangout',
          templateUrl: 'assets/app/theme/mocks/hangout.html'
        })
        .state('user.theme.mocks.browse-campaigns', {
          url: '/browse-campaigns',
          templateUrl: 'assets/app/theme/mocks/browse-campaigns.html'
        })
        .state('user.theme.mocks.search', {
          url: '/search',
          templateUrl: 'assets/app/theme/mocks/search.html'
        })
        .state('user.theme.mocks.view-trial-campaign', {
          url: '/view-trial-campaign',
          templateUrl: 'assets/app/theme/mocks/view-trial-campaign.html'
        })
        .state('user.theme.mocks.view-contest-campaign', {
          url: '/view-contest-campaign',
          templateUrl: 'assets/app/theme/mocks/view-contest-campaign.html'
        })
        .state('user.theme.mocks.join-contest-campaign', {
          url: '/join-contest-campaign',
          templateUrl: 'assets/app/theme/mocks/join-contest-campaign.html'
        })
        .state('user.theme.mocks.my-activities', {
          url: '/my-campaigns',
          templateUrl: 'assets/app/theme/mocks/my-activities.html'
        })
        .state('user.theme.mocks.company-profile', {
          url: '/company-profile',
          templateUrl: 'assets/app/theme/mocks/company-profile.html'
        });
}
