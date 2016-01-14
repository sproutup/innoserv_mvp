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
        .state('user.theme.mocks.view-campaign', {
          url: '/view-campaign',
          templateUrl: 'assets/app/theme/mocks/view-campaign.html'
        })
        .state('user.theme.mocks.search', {
          url: '/search',
          templateUrl: 'assets/app/theme/mocks/search.html'
        });
}
