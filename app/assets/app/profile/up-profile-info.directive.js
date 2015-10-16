angular
    .module('sproutupApp')
    .directive('upProfileInfo', upProfileInfo);

function upProfileInfo() {
    var directive = {
        restrict: 'E',
        templateUrl: '/assets/app/profile/up-profile-info.html',
        scope: {
            user: "="
        },
        link: linkFunc,
        controller: UpProfileInfoController,
        controllerAs: 'vm',
        bindToController: true
    };

    return directive;

    function linkFunc(scope, element, attr, ctrl) {
    }
}

function UpProfileInfoController() {
    var vm = this;
}
