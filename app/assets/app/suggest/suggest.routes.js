angular
    .module('sproutupApp')
    .config(config);

function config($stateProvider) {
    $stateProvider
        .state('user.suggest' ,{
            url: '/suggest',
            templateUrl: 'assets/app/suggest/suggest.html',
            controller: 'SuggestController',
            controllerAs: 'vm',
            data: {
                title: 'Suggest Cool Products - SproutUp'
            }
        })
        .state('user.suggestPreview' ,{
            url: '/suggest-coming-soon',
            templateUrl: 'assets/app/suggest/suggest-coming-soon.html',
            controller: 'SuggestController',
            controllerAs: 'vm',
            data: {
                title: 'Suggest Cool Products - SproutUp'
            }
        })
        .state('user.suggestMock' ,{
            url: '/suggestMock',
            templateUrl: 'assets/app/suggest/mock.suggest.html',
            controller: 'SuggestController',
            controllerAs: 'vm',
            data: {
                title: 'Suggest Cool Products - SproutUp'
            }
        });
}