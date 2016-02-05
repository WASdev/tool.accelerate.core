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

var app = angular.module('libertyTechnologyDeveloperApp', []);
app.controller('libertyTechnologyDeveloperController', function($scope, $http, $window) {
	
	$scope.form = {};
	  $scope.submitTech = function() {
		  $scope.returnedApiKey = null;
		  $scope.failureMessage = null;
		  $http({
			  method: 'POST',
			  url: 'tech/plugin',
			  params : {'techName' :  $scope.form.techName,  'namespace' : $scope.form.namespace},
			  responseType : 'arraybuffer'
			}).then(function successCallback(response) {
				console.log(response.headers('returnedApiKey'));
				$scope.returnedApiKey = response.headers('returnedApiKey');
				var blob = new Blob([response.data], {type: "application/zip"});
			    saveAs(blob, response.headers('returnedFileName'));
			  }, function errorCallback(response) {
				  $scope.failureMessage = response.headers('failureMessage');
			  });
		  };
});