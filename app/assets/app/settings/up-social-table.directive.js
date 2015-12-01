angular
    .module('sproutupApp')
    .directive('upSocialTable', upSocialTable);

function upSocialTable() {
    var directive = {
        restrict: 'EA',
        templateUrl: 'assets/app/settings/social-connection.html',
        scope: {},
        link: linkFunc,
        controller: 'AnalyticsController',
        controllerAs: 'vm',
        bindToController: true
    };

    return directive;

    function linkFunc(scope, element, attr, ctrl) {
        
    }    
}