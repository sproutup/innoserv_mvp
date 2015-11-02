angular
    .module('sproutupApp')
    .directive('upLazyScript', upLazyScript);

function upLazyScript() {
    var directive = {
        restrict: 'E',
        scope: {},
        link: linkFunc
    };

    return directive;

    function linkFunc(scope, el, attr, ctrl) {
        var s = document.createElement("script");
        s.type = "text/javascript";
        s.id = attr.id;
        s.src = attr.src;
        document.head.appendChild(s);
        el.remove();
    }
}