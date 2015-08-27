angular
    .module('sproutupApp')
    .factory( 'MyTrialProductsService', MyTrialProductsService);

MyTrialProductsService.$inject = ['$resource'];

function MyTrialProductsService($resource){
    return $resource('/api/trials/products', {'query':  {method:'GET', isArray:true}} );
}