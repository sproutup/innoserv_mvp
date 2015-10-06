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
            context: "@"
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

        if (attr.context === 'product-trial-big') {
            element.addClass('product-trial-user-card big'); 
        } else if (attr.context === 'product-trial-small') {
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

function UpUserCardController() {
    var vm = this;
    if (vm.user && vm.user.urlTwitter) {
        vm.user.twitterHandle = "@" + vm.user.urlTwitter.slice(20);
    }
}