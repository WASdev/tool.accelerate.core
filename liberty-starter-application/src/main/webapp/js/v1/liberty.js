/*******************************************************************************
 * Copyright (c) 2015 IBM Corp.
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

var app = angular.module('libertyTechnology', []);
app.controller('libertyTechnologySelector', function($scope, $http, $window) {

  $scope.techTypes = [];
  
  $http.get("api/v1/tech").then(function(response) {
	 response.data.forEach(function(tech) {
		 var techType = {
				 "id" : tech.id,
				 "name" : tech.name,
				 selected : false
		 };
		 $scope.techTypes.push(techType);
	 });
  });

  $scope.techSelected = true;
   
  $scope.checkSelections = function() {
    var anySelected = false;
    angular.forEach($scope.techTypes, function(value, key) {
      if (value.selected) {
        anySelected = true;
      }
    });
    
    if (anySelected)
      $scope.techSelected = false;
    else 
      $scope.techSelected = true;
  }
  
  $scope.downloadZip = function() {
    var selectionData = "";
  
    angular.forEach($scope.techTypes, function(value, key) {
      if (value.selected) {
        if (selectionData.length > 0)
          selectionData += "&"
        selectionData += "name=" + value.id;
      }
    });
    
    if (selectionData.length > 0)
      $window.location.href = "/start/api/v1/data?" + selectionData
    
  };
});