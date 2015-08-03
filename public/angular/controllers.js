'use strict';

/* Controllers */

var fileControllers = angular.module('FileControllers', ['ui.bootstrap']);

fileControllers.controller('FileCtrl', ['$scope', '$rootScope', '$upload', 'FileService', '$timeout', '$modal', '$log',
    function($scope, $rootScope, $upload, FileService, $timeout, $modal, $log){

        $scope.gallery = {
            'showUpload': false
        };

        $scope.upload = {
            progress : 0
        };

        $scope.getAllFiles = function (refId, refType) {
            FileService.getAllFiles(refId, refType).then(
                function(result){
                    $log.debug("fileCtrl - getAllFiles - result: " + result.size);
                    $scope.files = result;
                },
                function(error){
                    $scope.files = null;
                }
            );
        };

//        $scope.$watch('product', function(product) {
//            $log.debug("watch product found: " + product.id);
//        });

        $scope.$watch('files', function(files) {
            $log.debug("watch files...");
            if (files != null) {
                for (var i = 0; i < files.length; i++) {
                    $scope.errorMsg = null;
                    (function(file) {
                        $log.debug("generate thumbnail");
                        generateThumb(file);
                    })(files[i]);
                }
            }
        });

        function generateThumb(file) {
            if (file != null) {
                $log.debug("set thumbnail...found file of type: " + file.type);
                if (file.type.indexOf('image') > -1) {
                    $log.debug("set thumbnail...reader supported");
                    $timeout(function() {
                        $log.debug("set thumbnail...timeout entered");
                        var fileReader = new FileReader();
                        fileReader.readAsDataURL(file);
                        fileReader.onload = function(e) {
                            $timeout(function() {
                                $log.debug("set thumbnail url");
                                file.dataUrl = e.target.result;
                            });
                        }
                    });
                }
                else
                if (file.type.indexOf('video') > -1) {
                    $log.debug("set thumbnail...reader supported");
                    file.dataUrl = "/assets/images/video-thumbnail.png";
                }
            }
        };

        $scope.toggle = function() {
            $log.debug("cancel");
            $scope.gallery.showUpload = !$scope.gallery.showUpload;
            $scope.reset();
        };

        $scope.reset = function() {
            $log.debug("reset");
            $scope.files = null;
            $scope.upload.text = "";
        };

        $scope.upload = function (files, message, refId, refType) {
            $log.debug("upload: " + refId);

            var startTime = +new Date();

            if (files && files.length) {
                for (var i = 0; i < files.length; i++) {
                    FileService.authenticate(files[i], message, refId, refType).then(
                        function (result) {
                            $log.debug("upload auth returned");
                            result.file.progress = 50;

                            FileService.upload(result.file, result.data).then(
                                function (result) {
                                    // verify that upload to s3 is done
                                    FileService.verify(result.file, result.uuid).then(
                                        function (result) {
                                            result.file.progress = 100;
                                            var endTime = +new Date();
                                            $log.debug('upload...finished in ' + (endTime - startTime) + ' ms');

                                            $log.debug("broadcast fileUploadEvent");
                                            $log.debug("type: " + result.data.type);
                                            // firing an event downwards
                                            $rootScope.$broadcast('fileUploadEvent', {
                                                data: result.data
                                            });
                                            $rootScope.$broadcast('alert:success', {
                                                message: 'Upload success'
                                            });
                                            $scope.reset();
                                        }
                                    )
                                },
                                function (error) {
                                    // todo handle error
                                },
                                function (result) {
                                    result.file.progress = (50 + (result.progress / 2)).toFixed(1);
                                }
                            );
                        },
                        function (errorPayload) {
                        },
                        function (result) {
                            $log.debug("auth progress - " + result.progress);
                            result.file.progress = (result.progress / 2).toFixed(1);
                        }
                    );
                }
            }
        };
}]);

var authControllers = angular.module('AuthControllers', ['ui.bootstrap']);

authControllers.controller('AuthCtrl', ['$scope', '$rootScope', '$modal', '$log', 'AuthService', '$q', '$state',
    function ($scope, $rootScope, $modal, $log, authService, $q, $state) {

    var vm = this;
    vm.m = authService.m;
    vm.user = authService.user;
    vm.isLoggedIn = authService.isLoggedIn;
    vm.init = false;

    activate();

    function activate() {
        if(!authService.ready()){
            var unbindWatch = $rootScope.$watch(authService.ready, function (value) {
                if ( value === true ) {
                  unbindWatch();
                  activate();
                }
            });
        }
        else{
            vm.init = true;
        }
    }

    vm.getActiveTrials = function () {
        if (authService.m.isLoggedIn && typeof authService.m.user.trials !== 'undefined') {
            return authService.m.user.trials.filter(function (item) {
                return (item.status >= 0 && item.status < 4);
            }).length;
        }
        else return 0;
    }

    $scope.signup = {
        'email': '',
        'password': '',
        'confirm': ''
    };
    $scope.login = {
        'email': '',
        'password': '',
        'confirm': ''
    };

    $scope.$on('SignupEvent', function(event, mass) {
        $log.debug("SignupEvent received");
        $scope.signup('sm');
    });

    $scope.$on('LoginEvent', function(event, mass) {
        $log.debug("LoginEvent received");
        $scope.login('sm');
    });

    $scope.$on('auth:trial', function(event, mass) {
        $log.debug("Trial Event received");
        $scope.trial('sm');
    });

    $scope.signup = function (size) {
        var signupInstance = $modal.open({
            templateUrl: '/assets/templates/signup.html',
            controller: 'SignupInstanceCtrl',
            size: size,
            resolve: {
                signup: function () {
                    return $scope.signup;
                }
            }
        });

        signupInstance.result.then(function (signup) {
            $log.info('Modal signup at: ' + new Date());
            $state.go("wizard.start");
        }, function (result) {
            $log.info('Modal dismissed at: ' + new Date());
            if(result == "login"){
                $scope.login('sm');
            }
        });
    };

    $scope.login = function (size) {
        var loginInstance = $modal.open({
            templateUrl: '/assets/templates/login.html',
            controller: 'LoginInstanceCtrl',
            size: size,
            resolve: {
                login: function () {
                    return $scope.login;
                }
            }
        });

        loginInstance.result.then(function (result) {
            $log.debug("login return value: " + result);
            vm.user = authService.user;
            vm.isLoggedIn = authService.isLoggedIn;
        }, function (result) {
            $log.info('Login dismissed at: ' + new Date());
            if(result == "signup"){
                $scope.signup('sm');
            }
        });
    };

    $scope.trial = function (size, items) {
        var deferred = $q.defer();

        $log.debug("trial modal prod id: " + items.product_id);
        var trialInstance = $modal.open({
            templateUrl: '/assets/templates/trial.html',
            controller: 'TrialInstanceCtrl',
            size: size,
            resolve: {
                items: function () {
                    return items;
                }
            }
        });

        trialInstance.result.then(function (result) {
            $log.debug("trial return");
            deferred.resolve();
            //updateUser();
        }, function (result) {
            $log.info('trial dismissed at: ' + new Date());
            if(result == "signup"){
                $scope.signup('sm');
            }
            if(result == "login"){
                $scope.login('sm');
            }
            deferred.reject();
        });

        return deferred.promise;
    };

    vm.logout = function (size) {
        var promise = authService.logout();
        promise.then(
            function(payload){
                $log.info('logout success: ' + new Date());
                $state.go('user.home');
            },
            function(errorPayload){
                $log.info('logout failed: ' + new Date());
            }
        );
    };
}]);

// Please note that $modalInstance represents a modal window (instance) dependency.
// It is not the same as the $modal service used above.

authControllers.controller('SignupInstanceCtrl', ['$scope', '$location', '$window', '$modalInstance', 'AuthService', '$log', 'signup', '$state',
    function ($scope, $location, $window, $modalInstance, AuthService, $log, signup, $state) {

    $scope.ok = function () {
        var dataObject = {
            "name" : $scope.signup.name,
            "email" : $scope.signup.email,
            "password" : $scope.signup.password,
            "nickname" : $scope.signup.name.toLowerCase().replace(/\W+/g, '')
        };

        // reset error message
        $scope.signup.error = "";

        var promise = AuthService.signup(dataObject);

        promise.then(
            function(payload){
                // this callback will be called asynchronously
                // when the response is available
                $modalInstance.close($scope.signup);
            },
            function(errorPayload){
                $log.info('Signup failed: ' + errorPayload + " " + new Date());
                if(errorPayload.status=="USER_EXISTS"){
                    $scope.signup.error = "Oops, email's been taken. If it's you, please log in.";
                }
                else{
                    $scope.signup.error = "Signup failed";
                }
            }
        );
    };

    $scope.provider = function (provider) {
        $log.debug("provider: " + provider);

        var currentPath = $location.path();
        $log.debug("path: " + currentPath);

        var dataObject = {
            originalUrl : currentPath
        };

        var promise = AuthService.provider(provider, dataObject);

        promise.then(
            function(payload){
                $log.debug(payload);
                $window.location.href = payload;
                $modalInstance.close("ok");
            },
            function(errorPayload){
                $log.info('Login failed: ' + new Date());
            }
        );
    };

    $scope.login = function () {
        $modalInstance.dismiss('login');
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
}]);

authControllers.controller('LoginInstanceCtrl', ['$scope', '$location', '$window', '$modalInstance', 'AuthService', '$log', 'login',
    function ($scope, $location, $window, $modalInstance, AuthService, $log, login) {

    $scope.ok = function () {
        var dataObject = {
            email : $scope.login.email,
            password  : $scope.login.password
        };

        $log.info('login attempt: ' + new Date());

        // reset error message
        $scope.login.error = "";

        var promise = AuthService.login(dataObject);

        promise.then(
            function(payload){
                $modalInstance.close(payload);
            },
            function(errorPayload){
                $log.info('Login failed: ' + new Date());
                $scope.login.error = true;
            }
        );
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

    $scope.signup = function () {
        $modalInstance.dismiss('signup');
    };

    $scope.provider = function (provider) {
        $log.debug("provider: " + provider);

        var currentPath = $location.path();
        $log.debug("path: " + currentPath);

        var dataObject = {
            originalUrl : currentPath
        };

        var promise = AuthService.provider(provider, dataObject);

        promise.then(
            function(payload){
                $log.debug(payload);
                $window.location.href = payload;
                $modalInstance.close("ok");
            },
            function(errorPayload){
                $log.info('Login failed: ' + new Date());
            }
        );
    };
}]);

authControllers.controller('TrialInstanceCtrl', ['$scope', '$modalInstance', '$rootScope', '$location', '$window', 'ProductTrialService', '$log', 'items',
    function ($scope, $modalInstance, $rootScope, $location, $window, trialService, $log, items) {

        $log.debug("trial instance items: ", items);
        $scope.trial = {
            "isLoggedIn" : items.isLoggedIn
        };

        $scope.ok = function () {
            var dataObject = {
                "name" : $scope.trial.name,
                "email" : $scope.trial.email,
                "product_id": items.product_id
            };

            // reset error message
            $scope.trial.error = "";

            var promise = trialService.add(dataObject);

            promise.then(
                function(payload){
                    // this callback will be called asynchronously
                    // when the response is available
                    $rootScope.$broadcast('alert:success', {
                        message: 'Thanks! You have a spot in line :)'
                    });
                    $modalInstance.close($scope.signup);
                },
                function(errorPayload){
                    $log.info('Trial failed: ' + errorPayload + " " + new Date());
                    $scope.trial.error = "Trial failed";
                }
            );
        };

        $scope.login = function () {
            $modalInstance.dismiss('login');
        };

        $scope.signup = function () {
            $modalInstance.dismiss('signup');
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    }]);


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
  }]);


// Controller for modal share (Sprout Up) instance created by @apurv
productControllers.controller('modalShareCtrl', ['$scope', '$window', '$stateParams', '$modalInstance', '$http', 'ProductService', '$log',
   function ($scope, $window, $stateParams, $modalInstance, $http, ProductService, $log) {
	var processShareData = {};
	ProductService.get({slug: $stateParams.slug}).$promise.then(
			function(data) {
				// success
				$scope.product = data;

				$http({
			        method: 'GET',
			        url: '/api/campaign/getInfo/' + $scope.product.id,
			        headers: {'Content-Type': 'application/json'}
				}).success(function(data, status, headers, config){
			        // this callback will be called asynchronously
			        // when the response is available
					$scope.campaignName = data.campaignName;
					$scope.longDescription = data.campaignLongDescription;
					if (typeof data.perks !== 'undefined') { $scope.perk1 = data.perks[0]; }
					$scope.uniqueLink = data.url;
					$scope.showCheckbox = data.offerDiscount;
					$scope.checkboxText = data.discountText;
					processShareData = {
						"userId":data.userId,
						"referralId":data.referralId,
						"referrerId":$window.sessionStorage.refId,
						"campaignId":data.campaignId,
						"requestedDisc":false,
						"sharedOnSocialMedia":false
					}

					$scope.fbShare = function () {
						var obj = {
							method: 'feed',
							link: data.url,
							picture: data.productPictureURL,
							name: 'Sprout ' + $scope.product.productName + ' Up!',
							caption: $scope.product.productDescription,
							description: data.campaignShareMessage
						};
						function callback(response) {
							if (typeof response !== "undefined" && response !== null) {
								processShareData.sharedOnSocialMedia = true;
							}
						}
						FB.ui(obj, callback);
					}

					$scope.twtLink = 'https://twitter.com/intent/tweet' +
						'?text=' + encodeURIComponent(data.campaignShareMessage) +
						'&via=' + 'sproutupco' +
						'&url=' + encodeURIComponent($scope.uniqueLink);
			    })
			},
			function(error) {
				// error handler
				$state.go("home");
			}
	);

	$scope.handleCheckbox = function () {
		processShareData.requestedDisc = !processShareData.requestedDisc;
	}

	twttr.events.bind('tweet', function(event) {
		processShareData.sharedOnSocialMedia = true;
	});

	$scope.close = function () {
		if (processShareData.requestedDisc || processShareData.sharedOnSocialMedia) {
            $log.debug(JSON.stringify(processShareData, null, 4))
            $http({
                method: 'POST',
                url: '/api/campaign/processShare',
                data: processShareData,
                headers: {'Content-Type': 'application/json'}
            });
		}
		$modalInstance.close();
	};
}]);


productControllers.controller('productDetailCtrl', ['$scope', '$rootScope', '$stateParams', '$state', '$log', 'ProductService', 'AuthService', '$window', '$http', '$modal', '$analytics',
  function($scope, $rootScope, $stateParams, $state, $log, ProductService, authService, $window, $http, $modal, $analytics) {
    $log.debug("entered product details ctrl. slug=" + $stateParams.slug);

    var slug = $stateParams.slug;
    $scope.init = false;
    $scope.isLoggedIn = false;
    $scope.activeSprout = false;
    $scope.hasTrial = false;

    activate();

    //Open function for modal share added by @apurv
    $scope.open = function () {
		var modalInstance = $modal.open({
			animation: true,
			templateUrl: 'myModalContent.html',
			controller: 'modalShareCtrl'
		});
	};

	$scope.login = function(state, params){
	    console.log("## login redirect:", state, params);
        authService.loginAndRedirect(state, params);
	}

    function activate() {
        if(!authService.ready()){
            var unbindWatch = $rootScope.$watch(authService.ready, function (value) {
                if ( value === true ) {
                  unbindWatch();
                  activate();
                }
            });
        }
        else{
            init();
        }
    }

    function init(){
        $scope.isLoggedIn = authService.m.isLoggedIn;
        if(authService.ready() && authService.m.isLoggedIn){
            $log.debug("## check for current trial");
            $scope.hasCurrentTrial = false;
            if(authService.m.user.trials !== undefined){
                var item = authService.m.user.trials.filter(function(element){
                    return element.product.slug == slug && element.status >= 0;
                })
                console.log("filter result: ", item);
                if(item.length > 0){
                    $log.debug("## found current trial");
                    $scope.hasCurrentTrial = true;
                }
                else{
                    $log.debug("## didn't find current trial");
                    $scope.hasCurrentTrial = false;
                }
            }
            console.log("## check for number of active trials")
            $scope.hasReachedTrialLimit = false;
            if(authService.m.user.trials !== undefined) {
                var activeTrials = authService.m.user.trials.filter(function(element) {
                    return (element.status >= 0 && element.status < 4)
                });
                $scope.hasReachedTrialLimit = (activeTrials.length >= 3);
                console.log("## found " + activeTrials.length + " active trials");
            }
        };

        ProductService.get({slug: $stateParams.slug}).$promise.then(
            function(data) {
                // success
                $scope.product = data;
                if (typeof data.trials !== 'undefined') {
                    var productTrials = data.trials.filter(function(trial) {
                        return trial.status > 0;
                    });
                }
                if (typeof productTrials !== 'undefined' && productTrials.length !== 0) {
                    $scope.hasTrial = true;
                    productTrials = shuffleArray(productTrials);
                    $scope.trialName = productTrials[0].user.name;
                    $scope.trialReason = productTrials[0].reason;
                    $scope.avatarUrl = productTrials[0].user.avatarUrl;
                }
                $scope.init = true;
            },
            function(error) {
                // error handler
                $state.go("home");
            }
        );

        if($stateParams.refId!==undefined){
            $analytics.eventTrack('Referral Page Views', { category: 'Referral Link', refId: $stateParams.refId, label: $stateParams.refId });
        }
    }

    function shuffleArray(array) {
      var m = array.length, t, i;
      while (m) {
        i = Math.floor(Math.random() * m--);
        t = array[m];
        array[m] = array[i];
        array[i] = t;
      }
      return array;
    }

    // Function for getting parameters from url added by @apurv
	function getParameterByName(name) {
	    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
	    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
	        results = regex.exec(location.search);
	    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
	}
	// Check for/storage of refId added by @apurv
	if (typeof $window.sessionStorage.refId === 'undefined') {
		var referrerId = getParameterByName("refId");
		if (referrerId !== "") {
			$window.sessionStorage.refId = referrerId;
		}
	}

    ProductService.get({slug: $stateParams.slug}).$promise.then(
        function(data) {
            // success
            $scope.product = data;
            // Active campaign check added by @apurv
			$http({
		        method: 'GET',
		        url: '/api/campaign/getActive/' + $scope.product.id,
		        headers: {'Content-Type': 'application/json'}
			}).success(function(data, status, headers, config) {
		        // this callback will be called asynchronously
		        // when the response is available
				$scope.activeSprout = data.active;
				$scope.init = true;
		    })
        },
        function(error) {
            // error handler
            $state.go("home");
        }
    );
  }]);

productControllers.controller('userDetailCtrl', ['$scope', '$stateParams', '$state', '$log', 'UserService',
  function($scope, $stateParams, $state, $log, userService) {
    $log.debug("entered user details ctrl. nickname=" + $stateParams.nickname);
    userService.get({nickname: $stateParams.nickname}).$promise.then(
        function(data) {
            // success
            $scope.stranger = data;
        },
        function(error) {
            // error handler
            $state.go("home");
        }
    );
  }]);

productControllers.controller('ForumCtrl', ['$scope', 'ForumService', 'LikesService', '$log', 'AuthService',
  function($scope, ForumService, LikesService, $log, authService) {

    $scope.posts = [];
    $scope.forum = {
        showNewPost : false,
        selectedCategory : 0,   // default to 0 = suggestions
        category : ["suggestions","questions", "compliments"]
    };
    $scope.user = authService.m.user;

      $log.debug("forum ctrl loaded");

      $scope.$watch('product.id', function(files) {
          $log.debug("watch product.id...");
          getPosts($scope.product.id, $scope.forum.selectedCategory);
      });

          // create a blank object to hold our form information
    // $scope will allow this to pass between controller and view
    $scope.forum.newPostForm = {};
    $scope.forum.newCommentForm = {};

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
            while($scope.forum.newPostForm.tags.length > 0) {
                $scope.forum.newPostForm.tags.pop();
            }
        });
    };

    // add like
    $scope.addLike = function(post, userId) {
        console.log("like: ", userId);
        LikesService.addLikes(post.id, "models.Post", userId)
        .success(function(data){
            // reload data
            getPosts($scope.product.id, $scope.forum.selectedCategory);
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

    $scope.toogleComment = function(id){
        $scope.forum.showNewComment[id] = !$scope.forum.showNewComment[id];
    }

    $scope.loginAndRedirect = function(state, params){
        authService.loginAndRedirect(state, params);
    }

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
        })
        .error(function(data, status, headers, config){
            // called asynchronously if an error occurs
            // or server returns response with an error status.
            $log.debug("error: " + $scope.posts);
        });
    }
}]);
