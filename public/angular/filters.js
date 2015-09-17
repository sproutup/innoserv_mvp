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

/*
 * Filter for encoding strings for use in URL query strings
 */
angular.module('sproutupApp').filter('urlEncode', [
    function() {
        return window.encodeURIComponent;
    }
]);

angular.module('sproutupApp').filter('humanizeNumber', [
    function () {
        return function (n) {
            console.log('##humanize', n);
            var num = parseInt(n, 10);
            if (isNaN(num)) {
                return n;
            }

            var si = [
            { value: 1E18, symbol: "E" },
            { value: 1E15, symbol: "P" },
            { value: 1E12, symbol: "T" },
            { value: 1E9,  symbol: "G" },
            { value: 1E6,  symbol: "M" },
            { value: 1E3,  symbol: "k" }
            ], i;
            for (i = 0; i < si.length; i++) {
              if (num >= si[i].value) {
                return (num / si[i].value).toFixed(1).replace(/\.?0+$/, "") + si[i].symbol;
              }
            }
            return num;
        };
    }
]);
