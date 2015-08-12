angular
    .module('sproutupApp')
    .directive('userCard', userCard);

function userCard() {
    var directive = {
        restrict: 'EA',
        templateUrl: '/assets/templates/user-card.html',
        scope: {
            user: "="
        },
        link: linkFunc,
        controller: CardController,
        controllerAs: 'vm',
        bindToController: true
    };

    return directive;

    function linkFunc(scope, element, attr, ctrl) {
        var cardWrapper = element[0].children[0];
        var hoverBox = element[0].children[1];
        var arrowDown = element[0].children[0].children[3];
        var arrowUp = element[0].children[0].children[4];

        var elementOffset = element.offset();
        var windowWidth = $(window).width();
        var elementFromRight = windowWidth - elementOffset.left;

        if (elementOffset.top < 275) {
            // cardWrapper.style.bottom = '-266px';
            // hoverBox.style.bottom = '-75px';
            // arrowDown.style.display = 'none';
            // arrowUp.style.display = 'block';
            // if (elementFromRight < 140) {
            //     console.log(element);
            //     cardWrapper.style.left = 'auto';
            //     cardWrapper.style.right = '-17px';
            //     hoverBox.style.left = 'auto';
            //     hoverBox.style.right = '0px';
            //     arrowUp.style.right = '25px';
            // } else if (elementOffset.left < 140) {
            //     cardWrapper.style.left = '-17px';
            //     hoverBox.style.left = '0px';
            //     arrowUp.style.left = '21px';
            // } else {
            //     cardWrapper.style.left = '-80px';
            //     arrowUp.style.left = '84px';
            // }
        } else {
            if (elementOffset.left < 140) {
                cardWrapper.style.left = '-17px';
                hoverBox.style.left = '0px';
                arrowDown.style.left = '21px';
            } else if (elementFromRight < 140) {
                cardWrapper.style.left = 'auto';
                cardWrapper.style.right = '-17px';
                hoverBox.style.left = 'auto';
                hoverBox.style.right = '0px';
                arrowDown.style.left = 'auto';
                arrowDown.style.right = '25px';
            }
        }
    }    
}

function CardController() {
    var vm = this;
    if (vm.user && vm.user.urlTwitter) {
        vm.user.twitterHandle = "@" + vm.user.urlTwitter.slice(20);
    }
}