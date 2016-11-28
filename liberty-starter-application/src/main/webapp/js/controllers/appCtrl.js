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
.controller('appCtrl', ['$scope', '$log', '$timeout', 'appacc',
                function($scope,   $log,   $timeout,   appacc) {

  $log.debug("AppAccelerator : using controller 'appCtrl'");

  var rowCount = 3;

  $scope.hasTechnologies = false;
  $scope.serverError = false;
  $scope.technologies = [];
  $scope.states = ['SELECTED TECHNOLOGIES', 'DOWNLOAD'];
  $scope.state = 0;         //the state of the application e.g. have you selected a technology, not what step you are on
  $scope.step = 1;   //the current step that is being configured by the user
  $scope.maxSteps = 2;
  $scope.selectedCount = 0;

  $scope.toggleSelected = function(technology) {
    technology.selected = !technology.selected; //toggle selection
    $scope.selectedCount += (technology.selected ? 1 : -1);
    $log.debug("Selected count " + $scope.selectedCount);
    technology.panel = technology.selected ? "panel-success" : "panel-info";
    $.scrollify.update();
  }

  $scope.toggleInfo = function(technology) {
    technology.info = !technology.info; //toggle selection
  }

  $scope.showOptions = function(technology) {

  }


  //download the project skeleton
  $scope.downloadProject = function() {
    appacc.download($scope.technologies);
  }

  $scope.getNavClass = function() {
    return ($scope.selectedCount) ? "navEnabled" : "navDisabled";
  }

  //advance to the next state, will also move to the next step
  $scope.nextState = function() {
    if($scope.state != $scope.states.length) {
      $scope.state++;
    }
    $.scrollify.next();
  }

  //go back to the previous step
  $scope.prevStep = function() {
    $.scrollify.previous();
  }

  //return a list of the selected technologies
  $scope.getSelectedTechnologies = function() {
    var selected = "";
    for(var i = 0; i < $scope.technologies.length; i++) {
      var row = $scope.technologies[i];
      for(var j = 0; j < row.length; j++) {
        var technology = row[j];
        if(technology.selected) {
          selected += (" " + technology.name + ",");
        }
      }
    }
    if(selected.length > 0) {
      return selected.substring(0, selected.length - 1);
    }
    return selected;
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
        technology.panel = "panel-info";
        row.push(technology);
      }
      if(row.length) {
        $scope.technologies.push(row);
      }
      $scope.hasTechnologies = true;
      $log.debug('AppAccelerator : getTechnologies %o', $scope.technologies);

      //enable scroll options on the screen
      $.scrollify({
          section: ".step",
          after: function(index) {
            $timeout(function() {
              $scope.step = index + 1;
            });
          }
      });

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
