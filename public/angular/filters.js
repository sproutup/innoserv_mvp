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

/*
 * Take text and return html with a tags around links
 */
angular.module('sproutupApp').filter('urlify', function() {
    return function(text) {
        var urlRegex = /(http|ftp|https):\/\/[\w-]+(\.[\w-]+)+([\w.,@?^=%&amp;:\/~+#-]*[\w@?^=%&amp;\/~+#-])?/g;
        if (text) {
            return text.replace(urlRegex, function(url) {
                var displayedUrl;
                if (url.length > 40) {
                    displayedUrl = url.substring(0, 40);
                    displayedUrl += '...';
                } else {
                    displayedUrl = url;
                }
                return '<a href="' + url + '" target="blank">' + displayedUrl + '</a>';
            });
        }
    }
});

angular.module('sproutupApp').filter('humanizeNumber', [
    function () {
        return function (n) {
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
