'use strict';

var SAVED_ICON_URL = 'https://goo.gl/qFVQnz';
var NOT_SAVED_ICON_URL = 'https://goo.gl/CyvLz1';

var mainModal = {};
var errorModal = {};
var saveButton = {};
var saveSourceBox = {};
var saveResultBox = {};
var partOfSpeechCombobox = {};
var errorMessage = {};
var sourceBox = $('#source');
var resultBox = $('#result_box');
var sourceLangKeyInput = $('input[name="sl"]').first();
var resultLangKeyInput = $('input[name="tl"]').first();
//dto.definitionHtml = $('.gt-cd.gt-cd-md').html();
var starButtonId = '#gt-pb-star';


$.get(chrome.extension.getURL('/templates/modal.html'), function (data) {
  $($.parseHTML(data)).appendTo('body');
  mainModal = document.getElementById('main-modal');
  var closeSpan = document.getElementById('close-main-modal');
  closeSpan.onclick = closeMainModal;

  saveSourceBox = document.getElementById('save-source-box');
  saveResultBox = document.getElementById('save-result-box');
  partOfSpeechCombobox = document.getElementById('part-of-speech');

  var submitSaveButton = document.getElementById('submit-save-button');

  submitSaveButton.onclick = function () {
    var translationDto = new TranslationDto(
      saveSourceBox.innerText,
      sourceLangKeyInput.val(),
      saveResultBox.value,
      resultLangKeyInput.val(),
      partOfSpeechCombobox.value
    );

    libreLingvoService.saveTranslation(translationDto).then(
      function (data) {
        setTranslationIsSaved(true);
        closeMainModal();
      },
      function (error) {
        console.error(error);
        if (error.description && error.description==='No access token')
            window.location.href=BASE_URL;

        if (error.responseText) {
          var err = JSON.parse(error.responseText);
          if (err.message)
            showErrorMessage(err.message);
        }
      }
    );

  };

  var cancelSaveButton = document.getElementById('cancel-save-button');
  cancelSaveButton.onclick = closeMainModal;
});

var showMainModal = function () {
  mainModal.style.display = "block";
};

var closeMainModal = function () {
  mainModal.style.display = "none";
};

$.get(chrome.extension.getURL('/templates/error-modal.html'), function (data) {
  $($.parseHTML(data)).appendTo('body');
  errorModal = document.getElementById('error-modal');
  errorMessage = document.getElementById('error-message');
  var errorModalOkButton = document.getElementById('error-modal-ok-button');
  errorModalOkButton.onclick = closeErrorModal;
  var closeSpan = document.getElementById('close-error-modal');
  closeSpan.onclick = closeErrorModal;
});

var showErrorMessage = function (message) {
  errorModal.style.display = "block";
  errorMessage.innerText = message;
};

var closeErrorModal = function () {
  errorModal.style.display = "none";
};

window.onclick = function (event) {
  if (event.target == mainModal) {
    closeMainModal()
  }
  else if (event.target == errorModal) {
    closeErrorModal()
  }
};

$.get(chrome.extension.getURL('/templates/save-button.html'), function (data) {
  saveButton = $($.parseHTML(data));
  saveButton.insertBefore(starButtonId);
  saveButton.on('click', function () {
    showMainModal();
  });
});

var setTranslationIsSaved = function (saved) {
  if (saveButton.css)
    saveButton.css('background', 'url(' + (saved ? SAVED_ICON_URL : NOT_SAVED_ICON_URL) + ') no-repeat center');
};

var checkTranslation = function () {
  var source = sourceBox.val();
  var sourceLangKey = sourceLangKeyInput.val();
  var resultLangKey = resultBox.attr('lang');
  saveSourceBox.innerText = source;
  saveResultBox.value = resultBox.text();

  libreLingvoService.getUserTranslations(source, sourceLangKey, resultLangKey).then(
    function (data) {
      if (data.translations && data.translations.length)
        setTranslationIsSaved(true);
      else
        setTranslationIsSaved(false);
    },
    function (error) {
      setTranslationIsSaved(false);
    }
  );
};

//checkTranslation();
resultBox.on('DOMSubtreeModified', _.debounce(checkTranslation, 800));

