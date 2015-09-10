angular
    .module('sproutupApp')
    .directive('upContent', upContent);

function upContent() {
    var directive = {
        restrict: 'EA',
        templateUrl: 'assets/app/content/up-content.html',
        scope: {
            content: "=",
            state: "@",
            params: "="
        },
        link: linkFunc,
        controller: UpContentController,
        controllerAs: 'vm',
        bindToController: true
    };

    return directive;

    function linkFunc(scope, element, attr, ctrl) {
        
    }    
}

UpContentController.$inject = ['AuthService', '$scope'];

function UpContentController(AuthService, $scope) {
    var vm = this;
    vm.likes = vm.content.likes;

    vm.commentToggle = function() {
        if (!vm.content.commenting) {
            vm.content.commenting = true;
        } else {
            vm.content.commenting = false;
        }
    };

    // Get twitter handle of the product for twitter share
    if (vm.content.product && vm.content.product.urlTwitter) {
        var index = vm.content.product.urlTwitter.indexOf('twitter.com/');
        vm.content.product.twitterHandle = vm.content.product.urlTwitter.substring((index + 12), vm.content.product.urlTwitter.length);
    }
    
    // Set the twitter share info based on whether or not the user and product have twitter handles
    if (vm.content.user && vm.content.user.handleTwitter && vm.content.product.urlTwitter) {
        vm.content.tweetContentLink = 'https://twitter.com/intent/tweet' +
                                      '?text=Check out @' + vm.content.user.handleTwitter + '\'s post about @' + vm.content.product.twitterHandle +
                                      ' on @sproutupco—http://sproutup.co/buzz/' + vm.content.id;
    } else if (vm.content.user && vm.content.user.handleTwitter) {
        vm.content.tweetContentLink = 'https://twitter.com/intent/tweet' +
                                      '?text=Check out @' + vm.content.user.handleTwitter + '\'s post about ' + vm.content.product.name +
                                      ' on @sproutupco—http://sproutup.co/buzz/' + vm.content.id;
    } else if (vm.content.user && vm.content.product.urlTwitter) {
        vm.content.tweetContentLink = 'https://twitter.com/intent/tweet' +
                                      '?text=Check out ' + vm.content.user.name + '\'s post about @' + vm.content.product.twitterHandle +
                                      ' on @sproutupco—http://sproutup.co/buzz/' + vm.content.id;
    } else if (vm.content.user) {
        vm.content.tweetContentLink = 'https://twitter.com/intent/tweet' +
                                      '?text=Check out ' + vm.content.user.name + '\'s post about ' + vm.content.product.name +
                                      ' on @sproutupco—http://sproutup.co/buzz/' + vm.content.id;
    }

}