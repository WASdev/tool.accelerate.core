describe('appacc', function() {
  var appacc;
  beforeEach(module('appAccelerator'));
  beforeEach(inject(function($injector){
    appacc = $injector.get('appacc');
  }));

  describe('createDownloadUrl', function() {
    it('adds gradle to the URL if gradle is the selected build technology', function() {
      appacc.updateBuildType(appacc.buildType.GRADLE);
      appacc.addTechnology({id: 'testTechnology', selected: true});
      var url = appacc.createDownloadUrl();
      expect(url).toContain('&build=GRADLE');
    });
  });

  describe('updateBuildType', function() {
    it('sets the build type if a valid value is supplied', function() {
      buildType = appacc.updateBuildType(appacc.buildType.GRADLE);
      expect(buildType).toBe(appacc.buildType.GRADLE);
    }),
    it('ignores an invalid value', function() {
      var invalidValue = 'invalid';
      buildType = appacc.updateBuildType(invalidValue);
      expect(buildType).not.toBe(invalidValue);
    }),
    it('ignores an undefined value', function() {
      buildType = appacc.updateBuildType(undefined);
      expect(buildType).not.toBe(undefined);
    });
  });
});