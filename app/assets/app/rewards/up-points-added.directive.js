// Since this directive needs to sit somewhere waiting for points to be added, the directive is on the bottom of layout/navbar.html

angular
    .module('sproutupApp')
    .directive('upPointsAdded', upPointsAdded);

function upPointsAdded() {
    var directive = {
        restrict: 'E',
        link: linkFunc,
        templateUrl: '/assets/app/rewards/up-points-added.html'
    };

    return directive;

    function linkFunc(scope, el, attr, ctrl) {
        el[0].childNodes[0].style.left = String((scope.$root.eventObj.x - 40) + 'px');
        el[0].childNodes[0].style.top = String((scope.$root.eventObj.y - 40) + 'px');
        setTimeout(function() {
            el[0].childNodes[0].classList.add('zoomOut');
            setTimeout(function() {
                el[0].remove();
            }, 1000);
        }, 2500);
    }
}