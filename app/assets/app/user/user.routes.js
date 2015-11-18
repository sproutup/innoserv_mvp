angular
    .module('sproutupApp')
    .config(config);

function config($stateProvider) {
    $stateProvider
      .state('user.password', {
        abstract: true,
        url: '/password',
        template: '<ui-view/>'
      })
      .state('user.password.forgot', {
        url: '/forgot',
        templateUrl: 'assets/app/user/password/forgot-password.client.view.html'
      })
      .state('user.password.sent', {
        url: '/sent',
        templateUrl: 'assets/app/user/password/forgot-password-confirmation.client.view.html'
      })
      .state('user.password.reset', {
        abstract: true,
        url: '/reset',
        template: '<ui-view/>'
      })
      .state('user.password.reset.form', {
        url: '/:token',
        templateUrl: 'assets/app/user/password/reset-password.client.view.html'
      });
}