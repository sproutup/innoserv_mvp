'use strict';

/* Controllers */

/*
sproutupApp.controller('productTabCtrl', function($scope) {
  $scope.phones = [
    {'name': 'Nexus S',
     'snippet': 'Fast just got faster with Nexus S.'},
    {'name': 'Motorola XOOM™ with Wi-Fi',
     'snippet': 'The Next, Next Generation tablet.'},
    {'name': 'MOTOROLA XOOM™',
     'snippet': 'The Next, Next Generation tablet.'}
  ];
});

function My1Ctrl() {}
My1Ctrl.$inject = [];

function My2Ctrl() {}
My2Ctrl.$inject = [];

function My3Ctrl() {}
My3Ctrl.$inject = [];
*/

// products
var productControllers = angular.module('productControllers', []);

productControllers.controller('ProductTabCtrl', function($scope, $window, $location, $log) {
    // Tab directive
    $scope.tabs = [
        {title:'About', page: '/views/product-about'},
        {title:'Bar', page: '/views/product-bar'},
        {title:'Gallery', page: '/views/product-gallery'}
        ];
    $scope.tabs.activeTab = 0;
    $log.debug("activeTab = " + $scope.tabs.activeTab );
    $location.search('tab', '0');
});

productControllers.controller('productListCtrl', ['$scope', 'ProductService',
  function($scope, ProductService) {
    $scope.products = ProductService.query();
 //   $scope.orderProp = 'age';
  }]);

productControllers.controller('productDetailCtrl', ['$scope', '$routeParams', 'ProductService',
  function($scope, $routeParams, ProductService) {
    $scope.product = ProductService.get({slug: $routeParams.slug}, function(product) {
//    $scope.product = ProductService.get({slug: 1}, function(product) {

//      $scope.mainImageUrl = phone.images[0];
    });

//    $scope.setImage = function(imageUrl) {
//      $scope.mainImageUrl = imageUrl;
//    }
  }]);

productControllers.controller('ForumCtrl', ['$scope', 'ForumService','$log',
  function($scope, ForumService, log) {

    $scope.posts = [];
    $scope.forum = {
        showNewPost : false,
        selectedCategory : 0   // default to 0 = compliments
    }

    // create a blank object to hold our form information
    // $scope will allow this to pass between controller and view
    $scope.forum.newPostForm = {};
    $scope.forum.newCommentForm = {};

    // Load initial data
    getPosts($scope.product.id, $scope.forum.selectedCategory);

    // process the new post form
    $scope.processNewPostForm = function() {
        console.log("post: ", $scope.forum.newPostForm);
        $scope.forum.newPostForm.product_id = $scope.product.id;
        $scope.forum.newPostForm.category = $scope.forum.selectedCategory;
        ForumService.addPost($scope.forum.newPostForm)
        .success(function(data){
            // reload data
            getPosts($scope.product.id, $scope.forum.selectedCategory);
            $scope.forum.showNewPost = false;
            $scope.forum.newPostForm.title = "";
            $scope.forum.newPostForm.content = "";
            // $scope.forum.newPostForm.tabs = "";
        });
    };

    // process the new comment form
    $scope.processNewCommentForm = function(post) {
        $scope.forum.newCommentForm[post.id].parent = post.id;
        console.log("comment: ", $scope.forum.newCommentForm[post.id]);
        ForumService.addPost($scope.forum.newCommentForm[post.id])
        .success(function(data){
            // reload data
            getPosts($scope.product.id, $scope.forum.selectedCategory);
            $scope.forum.newCommentForm[post.id].content = "";
            $scope.forum.showNewComment[post.id] = false;
        });
    };

    // change category
    $scope.changeCategory = function(category_id) {
        $scope.forum.selectedCategory = category_id;
        getPosts($scope.product.id, $scope.forum.selectedCategory);
    };

    function getPosts(product_id, category_id) {
        ForumService.getPosts(product_id, category_id)
        .success(function(data, status, headers, config){
            // this callback will be called asynchronously
            // when the response is available
            $scope.posts = data;
            log.debug("posts: " + $scope.posts);
        })
        .error(function(data, status, headers, config){
            // called asynchronously if an error occurs
            // or server returns response with an error status.
            log.debug("error: " + $scope.posts);
        });
    }

/*
    $scope.postsxx = [{
                     "id": 1,
                     "content": "first post",
                     "user": {"name": "peter"},
                     "tags": [{"name": "energy"},{"name": "led"}],
                     "comments": [
                        {
                        "id": 1,
                        "user": {"name": "bill"},
                        "content": "kjsadfh ksdafjhasdk fdksa f"
                        },
                        {
                        "id": 1,
                        "user": {"name": "josh"},
                        "content": "kjsadfh ksdafjhasdk fdksa f"
                        },
                        {
                        "id": 1,
                        "user": {"name": "alfred"},
                        "content": "kjsadfh ksdafjhasdk fdksa f"
                        }
                        ]
                     },
                     {
                     "id": 2,
                     "content": "second post",
                     "user": {"name": "peter"}
                     },
                     {
                     "id": 3,
                     "content": "third post",
                     "user": {"name": "peter"}
                     }
                   ];  */
}]);
