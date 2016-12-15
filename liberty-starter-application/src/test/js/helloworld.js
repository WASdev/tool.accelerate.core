describe('appCtrl', function() {
  beforeEach(module('appAccelerator'));

  var $controller;

  beforeEach(inject(function(_$controller_){
    // The injector unwraps the underscores (_) from around the parameter names when matching
    $controller = _$controller_;
  }));

  describe('$scope.colCount', function() {
    it('sets the strength to "strong" if the password length is >8 chars', function() {
      var $scope = {};
      var controller = $controller('appCtrl', { $scope: $scope });
      expect($scope.colCount).toEqual(4);
    });
  });
});