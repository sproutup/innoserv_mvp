angular
    .module('sproutupApp')
    .config(config);

function config($stateProvider) {
    $stateProvider
        .state('user.hangout', {
          url: '/hangout',
          abstract: true,
          template: '<div ui-view><div>',
          controller: 'HangoutController',
          controllerAs: 'vm',
          data: {
            title: 'Hangout With Cool People - SproutUp'
          }
        })
        .state('user.hangout.list', {
          url: '/list',
          abstract: true,
          templateUrl: 'assets/app/hangout/list-hangout.view.html',
          data: {
              title: 'Hangout - List'
          }
        })
        .state('user.hangout.list.future', {
          url: '',
          templateUrl: 'assets/app/hangout/list-future-hangout.view.html',
          data: {
              title: 'Hangout - List'
          }
        })
        .state('user.hangout.list.past', {
          url: '/past',
          templateUrl: 'assets/app/hangout/list-past-hangout.view.html',
          data: {
              title: 'Hangout - List'
          }
        })
        .state('user.hangoutPreview' ,{
            url: '/hangout-coming-soon',
            templateUrl: 'assets/app/hangout/hangout-coming-soon.html',
            controller: '',
            controllerAs: '',
            data: {
                title: 'Hangout With Cool People - SproutUp'
            }
        });
}
