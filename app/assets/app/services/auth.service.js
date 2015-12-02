// factory
angular
    .module('sproutupApp')
    .factory('AuthService', authService);

authService.$inject = ['$http', '$q', '$cookieStore', '$log', 'UserService', '$timeout', '$state', '$analytics', '$resource', '$rootScope'];

function authService($http, $q, $cookieStore, $log, userService, $timeout, $state, $analytics, $resource, $rootScope){
    var user = {};
    var isReady = false;
    var urlBase = '/api/auth';
    var accessLevels = routingConfig.accessLevels,
        userRoles = routingConfig.userRoles;
//        , currentUser = $cookieStore.get('user') || { username: '', role: userRoles.public };

    var model = {
      user: {name: ''},
      isLoggedIn: false,
      trials: {},
      redirectState: null
    };

    var service = {
        m: model,
        authorize: authorize,
        getUser: getAuthenticatedUser,
        save: save,
        provider: provider,
        login: login,
        signup: signup,
        logout: logout,
        ready: ready,
        loggedIn: loggedIn,
        addTrial: addTrial,
        redirect: redirect,
        setRedirect: setRedirect,
        loginAndRedirect: loginAndRedirect,
        refreshTrials: refreshTrials,
        validateUsername: validateUsername,
        refreshPoints: refreshPoints,
        getNetwork: getNetwork
    };

    activate();

    return service;

    function activate() {
        return getAuthenticatedUser().then(function() {
            console.log('Return User');
        });
    }

    function validateUsername() {
        return $resource('/api/auth/username/validate/:username', { username:'@username' });
    }

    /*
    *   Return true when user auth data has been received
    */
    function ready(){
//        var deferred = $q.defer();
//        deferred.resolve(model.user);
        return isReady;
//        return deferred.promise;
    }

    function loggedIn(){
        return model.isLoggedIn;
    }

    function parseStateRef(ref) {
        parsed = ref.replace(/\n/g, " ").match(/^([^(]+?)\s*(\((.*)\))?$/);
        if (!parsed || parsed.length !== 4) throw new Error("Invalid state ref '" + ref + "'");
        return { state: parsed[1], paramExpr: parsed[3] || null };
    }

    /*
     *  If no redirect state exists then redirect to state in param
     */
    function redirect(state){
        model.redirectState = $cookieStore.get("redirectState");
        model.redirectParams = $cookieStore.get("redirectParams");
        if(model.redirectState === undefined){
            $state.go(state);
        }
        else{
            // console.log("redirect state from cookie:", model.redirectState);
            // console.log("model.redirectParams:" ,model.redirectParams);
            var state_tmp = model.redirectState;
            var params_tmp = model.redirectParams;
            model.redirectState = null;
            model.redirectParams = null;
            $cookieStore.remove("redirectState");
            $cookieStore.remove("redirectParams");
            $state.go(state_tmp, params_tmp);
        }
    }

    function loginAndRedirect(state, params){
        model.redirectState = state;
        $cookieStore.put("redirectState", state);
        $cookieStore.put("redirectParams", params);
        $state.go("user.login");
    }

    function setRedirect(state, params){
        $cookieStore.put("redirectState", state);
        $cookieStore.put("redirectParams", params);
    }

    function addTrial(data){
        if(model.user.trials === undefined){
            console.log("first trials, add empty object");
            model.user.trials = [];
        }

        // update users address and phone
        updateuser = {
            nickname: model.user.nickname,
            address: data.address,
            phone: data.phone
        };
        save(updateuser);

        model.user.trials.push(data);
        refreshTrials();
    }

    function refreshTrials() {
        model.trial = {};
        if(model.user.trials !== undefined){
            model.trials = model.user.trials.filter(function (item) {
                return item.active === true;
            });
        }
    }

    function authorize(accessLevel, role) {
        if(role === undefined) {
            if(currentUser !== null){
                role = currentUser.role;
            }
            else{
                return false;
            }
        }

        return accessLevel.bitMask & role.bitMask;
    }

    function getAuthenticatedUser(signup){
        var deferred = $q.defer();

        signup = typeof signup !== 'undefined' ? signup : false;

        $http({
            method: 'GET',
            url: '/api/auth/user',
            headers: {'Content-Type': 'application/json'}
        }).success(function(data, status, headers, config){
            switch(status){
                case 200:
                    //angular.extend(user, data);
                    //isLoggedIn = true;
                    angular.extend(model.user, data);
                    // console.log("#3 status: ", status);
                    isReady = true;
                    model.isLoggedIn = true;

                    if(signup){
                        // console.log('signup data: ', data);
                        $analytics.setAlias(model.user.id);
                    }
                    else{
                        // console.log('signin data: ', data);
                        $analytics.setUsername(model.user.id);
                    }
                    $analytics.setUserPropertiesOnce({name: model.user.name});

                    refreshTrials();
                    // $log.debug("auth user service returned success: " + model.user.name);
                    deferred.resolve(model.user);
                    break;
                case 204: // no content
                    isReady = true;
                    model.isLoggedIn = false;
                    // $log.debug("user not logged in");
                    deferred.reject("user not logged in");
                    break;
                default:
                    // $log.debug("unhandled return value");
                    deferred.reject("auth user - unhandled return value");
                    break;
            }
        }).error(function(data, status, headers, config){
            // called asynchronously if an error occurs
            // or server returns response with an error status.
            $log.debug("auth user service returned error");
            deferred.reject("auth user - failed to get user");
        });

        $log.debug("#2 auth user service returned promise");
        return deferred.promise;
    }

    function save(updateduser){
        var deferred = $q.defer();
        $log.debug("auth - save()");

        $http({
            method: 'POST',
            url: '/api/users/' + model.user.nickname,
            headers: {'Content-Type': 'application/json'},
            data: updateduser
        }).success(function(data, status, headers, config){
            $log.debug("productSuggestionService > add success");
            angular.extend(model.user, data);
            deferred.resolve(data);
        }).error(function(data, status, headers, config){
            $log.debug("productSuggestionService > failed to update");
            deferred.reject("productSuggestionService > failed to add");
        });

        return deferred.promise;
    }


    function provider(authprovider, path){
        var deferred = $q.defer();
        // get the current path
        //var currentPath = $location.path();
        // redirect external url
        //$window.location.href = 'http://www.google.com';

        $http({
            method: 'POST',
            url: '/api/auth/provider/' + authprovider,
            data: path,
            headers: {'Content-Type': 'application/json'}
        }).success(function(data, status, headers, config){
            $log.debug("provider returned success");
            deferred.resolve(data.redirect);
        }).error(function(data, status, headers, config){
            $log.debug("provider returned error");
            deferred.reject("failed to login");
        });

        $log.debug("provider returned promise");
        return deferred.promise;
    }

    function login(user) {
        var deferred = $q.defer();

        $http({
            method: 'POST',
            url: '/api/auth/login',
            data: user,
            headers: {'Content-Type': 'application/json'}
        }).success(function(data, status, headers, config){
            // this callback will be called asynchronously
            // when the response is available
            $log.debug("#1 login service returned success");
            getAuthenticatedUser().then(function () {
                    $log.debug("#4 login service get user success");
                    deferred.resolve("success");
                }
            );
        }).error(function(data, status, headers, config){
            // called asynchronously if an error occurs
            // or server returns response with an error status.
            $log.debug("login service returned error");
            deferred.reject("failed to login");
        });

        $log.debug("login service returned promise");
        return deferred.promise;
    }

    function signup(user){
        var deferred = $q.defer();

        $http({
            method: 'POST',
            url: '/api/auth/signup',
            data: user,
            headers: {'Content-Type': 'application/json'}
        }).success(function(data, status, headers, config){
            //angular.extend(model.user,data);
            getAuthenticatedUser(true).then(function () {
                $log.debug("signup service get user success");
                    deferred.resolve("success");
                }
            );
        }).error(function(data, status, headers, config){
            // called asynchronously if an error occurs
            // or server returns response with an error status.
            deferred.reject(data);
        });

        $log.debug("auth signup service returned promise");
        return deferred.promise;
    }

    function logout(){
        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: '/api/auth/logout',
            headers: {'Content-Type': 'application/json'}
        }).success(function(data, status, headers, config){
            // this callback will be called asynchronously
            // when the response is available
            model.isLoggedIn = false;
            model.user = {};
            model.trials = {};
            deferred.resolve("success");
        }).error(function(data, status, headers, config){
            // called asynchronously if an error occurs
            // or server returns response with an error status.
            deferred.reject("could not logout");
        });

        $log.debug("auth signup service returned promise");
        return deferred.promise;
    }

    function refreshPoints() {
        var Refresh = $resource('/api/user/points');
        Refresh.get(function(res){
            if (model.user.points === res.points) {
                return;
            }
            // keep oldPoints for animation transition
            model.user.oldPoints = model.user.points;
            model.user.points = res.points;
            // set points event for the points directive
            $rootScope.eventObj.points = res.events[0].points;
            $rootScope.$emit('PointsEvent');
            $rootScope.pointsAdded = true;
            setTimeout(function() {
                $rootScope.pointsAdded = false;
            }, 2500);
        });
    }

    function getNetwork() {
        return $resource('/api/analytics/user/:userId/network', { userId: '@userId' }, {'query': { method: 'GET', isArray: true }});
    }

    AuthService.accessLevels = accessLevels;

    AuthService.userRoles = userRoles;
}
