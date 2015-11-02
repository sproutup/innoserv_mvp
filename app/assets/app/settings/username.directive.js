angular
  .module('sproutupApp')

.directive('username', function($q, $timeout, AuthService) {
  return {
    require: 'ngModel',
    link: function(scope, elm, attrs, ctrl) {
      var usernames = ['Jim', 'John', 'Jill', 'Jackie'];

      ctrl.$asyncValidators.username = function(modelValue, viewValue) {

        if (ctrl.$isEmpty(modelValue)) {
          // consider empty model valid
          return $q.when();
        }

        var def = $q.defer();
        var ValidateUsername = AuthService.validateUsername();
        var item = new ValidateUsername();

        if (scope.loaded) {
          scope.checking = true;
          item.$save({
            username: viewValue
          }, function(res) {
            if (res.unique) {
              def.resolve();
            } else {
              def.reject();
            }
          });
        } else {
          scope.loaded = true;
        }

        return def.promise;
      };
    }
  };
});