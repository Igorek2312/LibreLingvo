'use strict';

chrome.runtime.onInstalled.addListener(function (details) {
  console.log('previousVersion', details.previousVersion);
});

var accessToken = '';

var wasAuthErrorFlag = true;

var ResponseMessage = function (response) {
  this.status = 'success';
  this.response = response;
};

var ErrorMessage = function (error) {
  this.status = 'error';
  this.error = error;
};

var updateAccessToken = function (baseUrl) {
  return new Promise(function (resolve, reject) {
    chrome.cookies.get({url: baseUrl, name: 'access_token'}, function (cookie) {
      if (cookie) {
        accessToken = cookie.value;
        console.log('updated access token:' + cookie.value);
        resolve();
      }
      else {
        reject(new ErrorMessage({description:'No access token'}));
      }
    });
  });
};

var sendAjaxRequest = function (settings, callback) {
  settings.headers.authorization = 'Bearer ' + accessToken;
  $.ajax(settings).then(
    function (response) {
      wasAuthErrorFlag = false;
      callback(new ResponseMessage(response));
    },
    function (error) {
      wasAuthErrorFlag = true;
      callback(new ErrorMessage(error));
    }
  );
};

chrome.extension.onMessage.addListener(function (message, sender, callback) {
  if (message.action === 'sendAjaxRequest') {
    if (wasAuthErrorFlag) {
      updateAccessToken(message.baseUrl).then(
        function () {
          sendAjaxRequest(message.requestSettings, callback);
        },
        function (errorMessage) {
          callback(errorMessage);
        }
      );
    }
    else {
      sendAjaxRequest(message.requestSettings, callback);
    }
    return true;
  }
});

console.log('content script done');