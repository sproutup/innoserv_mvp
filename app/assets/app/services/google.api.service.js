// factory
angular
    .module('sproutupApp')
    .factory('GoogleApiService', GoogleApiService);

GoogleApiService.$inject = ['$http', '$q', '$log', 'UserService', '$timeout'];

function GoogleApiService($http, $q, $log, userService, $timeout){
    var user = {};
    var isReady = false;
    var urlBase = '/api/google/api/code';
    var urloauth2 = 'https://accounts.google.com/o/oauth2/auth';

    var service = {
        requestToken: requestToken,
        exchangeAuthorizationCodeForToken: exchangeAuthorizationCodeForToken
    };

    activate();

    return service;

    function activate() {
        $log.debug("google api service - activated");
    }

    function buildQueryString(obj) {
        var str = [];
        for(var p in obj)
        str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
        return str.join("&");
    }

// https://accounts.google.com/o/oauth2/auth?
//redirect_uri=https://developers.google.com/oauthplayground&
//response_type=code&
//client_id=407408718192.apps.googleusercontent.com&
//scope=https://www.googleapis.com/auth/analytics.readonly+https://www.googleapis.com/auth/yt-analytics.readonly&
//approval_prompt=force&access_type=offline

    function requestToken(){
        var data = {
            redirect_uri: 'http://dev.sproutup.co:9000/settings/analytics',
            response_type: 'code',
            client_id: '200067319298-cpblm10r8s9o29kjgtahjek2eib7eigk.apps.googleusercontent.com',
            scope: 'https://www.googleapis.com/auth/analytics.readonly https://www.googleapis.com/auth/yt-analytics.readonly',
            access_type: 'offline',
            //login_hint: '',
            include_granted_scopes: 'true'
        };

        return urloauth2 + '?' + buildQueryString(data);
    }


    function exchangeAuthorizationCodeForToken(code){
        var deferred = $q.defer();

//        https://accounts.google.com/o/oauth2/auth?
//        redirect_uri=https%3A%2F%2Fdevelopers.google.com%2Foauthplayground&
//        response_type=code&
//        client_id=407408718192.apps.googleusercontent.com&
//        scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fanalytics&
//        approval_prompt=force&
//        access_type=offline

        // for details https://developers.google.com/identity/protocols/OAuth2WebServer
        $http({
            method: 'POST',
            url: urlBase,
            headers: {'Content-Type': 'application/json'},
//            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
//            transformRequest: function(obj) {
//                var str = [];
//                for(var p in obj)
//                str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
//                return str.join("&");
//            },
            params: {
                code: code
            }
        }).success(function(data, status, headers, config){
            switch(status){
                case 200:
                    console.log("#3 status: ", status);
                    deferred.resolve(data);
                    break;
                case 302: // no content
                    $log.debug("google api service - found");
                    deferred.resolve("found");
                    break;
                default:
                    $log.debug("unhandled return value");
                    deferred.reject("unhandled return value");
                    break;
            }
        }).error(function(data, status, headers, config){
            // called asynchronously if an error occurs
            // or server returns response with an error status.
            $log.debug("google api service returned error");
            deferred.reject("failed");
        });

        $log.debug("google api service returned promise");
        return deferred.promise;
    }
}
