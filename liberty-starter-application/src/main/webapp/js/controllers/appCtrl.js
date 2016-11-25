/*******************************************************************************
 * Copyright (c) 2016 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

'use strict';

angular.module('appAccelerator')
.controller('appCtrl', ['$scope', '$log', 'appacc',
                function($scope,   $log,   appacc) {

  $log.debug("AppAccelerator : using controller 'appCtrl'");

  var rowCount = 3;

  $scope.hasTechnologies = false;
  $scope.serverError = false;
  $scope.technologies = [];

  $scope.toggleSelected = function(technology) {
    technology.selected = !technology.selected; //toggle selection
  }

  $scope.toggleInfo = function(technology) {
    technology.info = !technology.info; //toggle selection
  }

  this.getTech = function() {
    appacc.getTechnologies().then(function(response) {
      //split the returned technologies into rows of X elements
      var row = undefined;
      for(var i = 0; i < response.length; i++) {
        if(!(i % rowCount)) {
          if(row) {
            $scope.technologies.push(row);
          }
          row = [];
        }
        var technology = response[i];   //just make the code a bit more readable
        technology.selected = false;   //flag to indicate if user has selected this technology
        technology.info = false;      //do not show the information for this technology
        row.push(technology);
      }
      if(row.length) {
        $scope.technologies.push(row);
      }
      $scope.hasTechnologies = true;
      $log.debug('AppAccelerator : getTechnologies %o', $scope.technologies);

      //enable scroll options on the screen
      $.scrollify({
          section: ".step:not(.hidden)"
      });
      $.scrollify.instantMove(0);
      $.scrollify.disable();
    }, function(error) {
      //error, so mark call as complete but show warning to user
      $scope.serverError = true;
      $scope.hasTechnologies = true;
    });
  };

//
  //trigger an initial population of technologies
  this.getTech();
}]);
