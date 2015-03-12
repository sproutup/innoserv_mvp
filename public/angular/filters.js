'use strict';

angular.module('productFilters', []).filter('checkmark', function() {
  return function(input) {
    return input ? '\u2713' : '\u2718';
  };
});

angular.module('sproutupApp').
  filter('fromNow', function() {
    return function(dateString) {
      return moment(dateString).fromNow()
    };
  });

angular.module('sproutupApp').filter('trusted', ['$sce', function ($sce) {
    return function(url) {
        return $sce.trustAsResourceUrl(url);
    };
}]);
