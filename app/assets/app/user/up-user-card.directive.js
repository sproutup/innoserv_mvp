angular
    .module('sproutupApp')
    .directive('upUserCard', upUserCard);

// todo -rename to upUserCard

function upUserCard() {
    var directive = {
        restrict: 'EA',
        templateUrl: '/assets/app/user/up-user-card.html',
        scope: {
            user: "=",
            context: "@",
            product: "="
        },
        link: linkFunc,
        controller: UpUserCardController,
        controllerAs: 'vm',
        bindToController: true
    };

    return directive;

    function linkFunc(scope, element, attr, ctrl) {
        var elementOffset = element.offset();
        var windowWidth = $(window).width();
        var elementFromRight = windowWidth - elementOffset.left;

        if (attr.context === 'product-trial-active') {
            element.addClass('product-trial-user-card big');
        } else if (attr.context === 'product-trial-next' || attr.context === 'product-trial-finished') {
            element.addClass('product-trial-user-card small'); 
        } else {
            arrangeDefault();
        }

        function arrangeDefault() {
            if (elementOffset.top < 275) {
                // code for images at top of apge
            } else {
                if (elementOffset.left < 140) {
                    element.addClass('left-user-card');
                } else if (elementFromRight < 140) {
                    element.addClass('right-user-card');
                }
            }
        }
    }    
}

UpUserCardController.$inject = ['$state', '$scope', 'AuthService'];

function UpUserCardController($state, $scope, AuthService) {
    var vm = this;
    vm.loggedInUserId = AuthService.m.user.id;
    // Get this twitter handle. Then set up tweets if we're on the product page.
    if (vm.user.handleTwitter) {
        // tweets for active trial
        if (vm.context === 'product-trial-active') {
            if (vm.product.twitterUserName) {
                vm.tweet = 'https://twitter.com/intent/tweet' +
                           '?text=' + vm.user.handleTwitter + ' how\'s the @' +
                           vm.product.twitterUserName + ' trial going? 🌱';
            } else {
                vm.tweet = 'https://twitter.com/intent/tweet' +
                           '?text=' + vm.user.twitterHandle + ' hey, how\'s the ' +
                           vm.product.name + ' trial going? 🌱';
            }
        } else if (vm.context === 'product-trial-finished') {
            if (vm.product.twitterUserName) {
                vm.tweet = 'https://twitter.com/intent/tweet' +
                           '?text=' + vm.user.handleTwitter + ' hey, how was the @' +
                           vm.product.twitterUserName + ' trial?';
            } else {
                vm.tweet = 'https://twitter.com/intent/tweet' +
                           '?text=' + vm.user.handleTwitter + ' hey, how was the ' +
                           vm.product.name + ' trial?';
            }
        }
    }
}