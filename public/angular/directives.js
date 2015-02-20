'use strict';

angular.module('sproutupApp').directive('toptags', function () {
    return {
        template:   '<div class="col-sm-12 popular-tags-row">'+
                    '<div class="popular-tags-header">Popular tags</div>' +
                        '<div class="popular-tags-list">'+
                            '<button ng-repeat="tag in toptags | orderBy:counter:true" type="button" class="btn btn-popular-tags"><i class="fa fa-tag"></i>{{tag.name}}<span class="popular-tags-count">{{tag.counter}}</span></button>'+
                        '</div>'+
                    '</div>',

        controller: function($scope, $log, $http) {

            getTopTags(10);

            function getTopTags(size) {
                $http({
                    method: 'GET',
                    url: '/api/tags/top',
                    params: {size: size}
                }).success(function(data, status, headers, config){
                    // this callback will be called asynchronously
                    // when the response is available
                    $scope.toptags = data;
                }).error(function(data, status, headers, config){
                    // called asynchronously if an error occurs
                    // or server returns response with an error status.
                });
            }

        }
    };
});
