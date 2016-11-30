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
.controller('appCtrl', ['$scope', '$log', '$timeout', 'appacc', '$window',
                function($scope,   $log,   $timeout,   appacc,   $window) {

  $log.debug("AppAccelerator : using controller 'appCtrl'");
  //only turn on analytics for live site
  var googleAnalytics = window.location.hostname == 'liberty-starter.wasdev.developer.ibm.com';
  $log.debug("Google Analytics is currently set to  : " + googleAnalytics);

  $scope.rowCount = 4;    //used by angular to layout rows correctly
  $scope.hasTechnologies = false;
  $scope.serverError = false;
  $scope.technologies = [];   //this controls the layout of the technologies, the service has the list of selected ones
  $scope.states = ['SELECTED TECHNOLOGIES', 'DOWNLOAD'];
  $scope.state = 0;         //the state of the application e.g. have you selected a technology, not what step you are on
  $scope.step = 1;   //the current step that is being configured by the user
  $scope.maxSteps = 2;
  $scope.selectedCount = 0;

  var currentWindowSize = $(window).height();

  angular.element($window).bind('resize', function () {
    currentWindowSize = $(window).height();
    $scope.$apply();    //use this to ensure that any corrrect decisions as made based on the new window size
  });

  $scope.toggleSelected = function(technology) {
    (technology.selected) ? appacc.removeTechnology(technology) : appacc.addTechnology(technology);
    $scope.selectedCount = appacc.getSelectedCount();
    $log.debug("Selected count " + $scope.selectedCount);
    technology.panel = technology.selected ? "panel-success" : "panel-info";
  }

  $scope.toggleInfo = function(technology) {
    technology.info = !technology.info; //toggle selection
    technology.displayOptions = false;
  }

  $scope.showOptions = function(technology) {
    technology.info = false;
    technology.displayOptions = !technology.displayOptions;
  }

  //get a technology for specific ID
  $scope.getTechnology = function(id) {
    $log.debug("AppAccelertor : looking for technology with ID : " + id)
    for(var i = 0; i < $scope.technologies.length; i++) {
      var row = $scope.technologies[i];
      for(var j = 0; j < row.length; j++) {
        var technology = row[j];
        if(technology.id == id) {
          return technology;
        }
      }
    }
    return undefined;
  }

  //download the project skeleton
  $scope.downloadProject = function() {
    appacc.download($scope.technologies);
  }

  $scope.getNavClass = function() {
    return ($scope.selectedCount) ? "navEnabled" : "navDisabled";
  }

  $scope.getFinalClass = function() {
    //if a technology has been selected or the window is too small put text at end of doc
    return ($scope.selectedCount || currentWindowSize < 700) ? "finalText2" : "finalText";
  }

  //advance to the next state, will also move to the next step
  $scope.nextState = function() {
    if($scope.state != $scope.states.length) {
      $scope.state++;
    }
  }

  //go back to the previous step
  $scope.prevStep = function() {
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
        if(!(i % $scope.rowCount)) {
          if(row) {
            $scope.technologies.push(row);
          }
          row = [];
        }
        var technology = response[i];       //just make the code a bit more readable
        technology.selected = false;        //flag to indicate if user has selected this technology
        technology.info = false;            //do not show the information for this technology
        technology.displayOptions = false;  //dpn't show any options
        technology.panel = "panel-info";
        row.push(technology);
      }
      if(row.length) {
        $scope.technologies.push(row);
      }
      $scope.hasTechnologies = true;
      $log.debug('AppAccelerator : getTechnologies %o', $scope.technologies);
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
