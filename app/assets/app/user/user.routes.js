angular
    .module('sproutupApp')
    .config(config);

function config($stateProvider) {
    $stateProvider
      .state('password', {
        abstract: true,
        url: '/password',
        template: '<ui-view/>'
      })
      .state('password.forgot', {
        url: '/forgot',
        templateUrl: 'assets/app/user/password/forgot-password.client.view.html'
      })
      .state('password.sent', {
        url: '/sent',
        templateUrl: 'assets/app/user/password/forgot-password-confirmation.client.view.html'
      })
      .state('password.reset', {
        abstract: true,
        url: '/reset',
        template: '<ui-view/>'
      })
      .state('password.reset.form', {
        url: '/:token',
        templateUrl: 'assets/app/user/password/reset-password.client.view.html'
      });
}