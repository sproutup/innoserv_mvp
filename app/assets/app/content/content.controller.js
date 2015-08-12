(function () {
    'use strict';

angular
    .module('sproutupApp')
    .controller('ContentController', ContentController);

ContentController.$inject = ['$stateParams', '$state', 'FeedService'];

function ContentController($stateParams, $state, FeedService) {
    var vm = this;
    vm.content = [];

    vm.content = FeedService.query();

    console.log(vm.content);



    // content = cs.$query();

    // cs.$get(function(content) {
    //     console.log(content);
    // }, function(err) {
    //     console.log(err);
    // });

    // var stream = [];
    // stream = new ContentService({param: '1'}).$query();

}

})();