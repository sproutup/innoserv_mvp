angular
    .module('sproutupApp')
    .config(config);

function config($stateProvider) {

    $stateProvider
        .state('user.product.detail', {
            url: '/:slug?refId',
            abstract: true,
            templateUrl: 'views/product-details',
            controller: 'productDetailCtrl',
            onEnter: function(){
                console.log("enter product detail");
            },
            data: {
                title: ''
            }
        })
        .state('user.product.detail.about', {
            url: '',
            templateUrl: 'views/product-about',
            //templateUrl: 'assets/app/products/about.html',
            onEnter: function(){
                console.log("enter product detail about");
            },
            data: {
                title: 'Product - About'
            }
        })
        .state('user.product.detail.buzz', {
            url: '/buzz',
            templateUrl: 'assets/app/products/product-detail-buzz.html',
            controller: 'ContentController',
            controllerAs: 'vm',
            onEnter: function(){
                console.log("enter product detail bar");
            },
            data: {
                title: 'Product - Geekout'
            }
        })
        .state('user.product.detail.bar', {
            url: '/bar',
            templateUrl: 'views/product-bar',
            controller: 'ForumCtrl',
            onEnter: function(){
                console.log("enter product detail bar");
            },
            data: {
                title: 'Product - Geekout'
            }
        })
        .state('user.product.detail.bar.question', {
            url: '/question',
            controller: function($scope){
                $scope.changeCategory(1);
            },
            onEnter: function(){
                console.log("enter product detail bar question");
            },
            data: {
                title: 'Product - Geekout'
            }
        })
        .state('user.product.detail.bar.compliment', {
            url: '/compliment',
            controller: function($scope){
                $scope.changeCategory(2);
            },
            onEnter: function(){
                console.log("enter product detail bar compliment");
            },
            data: {
                title: 'Product - Geekout'
            }
        })
        .state('user.product.detail.gallery', {
            url: '/gallery',
            templateUrl: 'views/product-gallery',
            onEnter: function(){
                console.log("enter product detail gallery");
            },
            data: {
                title: 'Product - Gallery'
            }
        });
}