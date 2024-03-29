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
        .state('user.theme.mocks.my-campaigns', {
          url: '/my-campaigns',
          templateUrl: 'assets/app/theme/mocks/my-campaigns.html'
        })
        .state('user.theme.mocks.search', {
          url: '/search',
          templateUrl: 'assets/app/theme/mocks/search.html'
        })
        .state('user.theme.mocks.view-trial-campaign', {
          url: '/view-trial-campaign',
          templateUrl: 'assets/app/theme/mocks/view-trial-campaign.html'
        })
        .state('user.theme.mocks.join-trial-campaign', {
          url: '/join-trial-campaign',
          templateUrl: 'assets/app/theme/mocks/join-trial-campaign.html'
        })
        .state('user.theme.mocks.after-join-trial-campaign', {
          url: '/after-join-trial-campaign',
          templateUrl: 'assets/app/theme/mocks/after-join-trial-campaign.html'
        })
        .state('user.theme.mocks.trial-campaign-buzz', {
          url: '/trial-campaign-buzz',
          templateUrl: 'assets/app/theme/mocks/trial-campaign-buzz.html'
        })
        .state('user.theme.mocks.view-contest-campaign', {
          url: '/view-contest-campaign',
          templateUrl: 'assets/app/theme/mocks/view-contest-campaign.html'
        })
        .state('user.theme.mocks.enter-contest-campaign', {
          url: '/enter-contest-campaign',
          templateUrl: 'assets/app/theme/mocks/enter-contest-campaign.html'
        })
        .state('user.theme.mocks.finished-contest-campaign', {
          url: '/finished-contest-campaign',
          templateUrl: 'assets/app/theme/mocks/finished-contest-campaign.html'
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
        })
        .state('user.theme.mocks.notification', {
          url: '/notification',
          templateUrl: 'assets/app/theme/mocks/notification.html'
        })
        .state('user.theme.mocks.buzz', {
          url: '/buzz',
          templateUrl: 'assets/app/theme/mocks/buzz.html'
        })
        .state('user.theme.mocks.suggest', {
          url: '/suggest',
          templateUrl: 'assets/app/theme/mocks/suggest.html'
        })
        .state('user.theme.mocks.my-profile-buzz', {
          url: '/my-profile-buzz',
          templateUrl: 'assets/app/theme/mocks/my-profile-buzz.html'
        })
        .state('user.theme.mocks.my-profile-activities', {
          url: '/my-profile-activities',
          templateUrl: 'assets/app/theme/mocks/my-profile-activities.html'
        })
        .state('user.theme.mocks.my-profile-suggested-products', {
          url: '/my-profile-suggested-products',
          templateUrl: 'assets/app/theme/mocks/my-profile-suggested-products.html'
        })
        // messages
        .state('user.theme.mocks.list-conversation', {
          url: '/list-conversation',
          templateUrl: 'assets/app/theme/mocks/list-conversation.html'
        })
        .state('user.theme.mocks.view-a-conversation', {
          url: '/view-a-conversation',
          templateUrl: 'assets/app/theme/mocks/view-a-conversation.html'
        });
}
