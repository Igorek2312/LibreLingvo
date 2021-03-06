'use strict';

describe('Controller: LessonViewCtrl', function () {

  // load the controller's module
  beforeEach(module('libreLingvoApp'));

  var LessonViewCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    LessonViewCtrl = $controller('LessonViewCtrl', {
      $scope: scope
      // place here mocked dependencies
    });
  }));

  it('should attach a list of awesomeThings to the scope', function () {
    expect(LessonViewCtrl.awesomeThings.length).toBe(3);
  });
});
