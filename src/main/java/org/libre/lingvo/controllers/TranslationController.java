package org.libre.lingvo.controllers;

import org.libre.lingvo.dto.*;
import org.libre.lingvo.entities.User;
import org.libre.lingvo.model.PartOfSpeech;
import org.libre.lingvo.services.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Created by igorek2312 on 29.10.16.
 */
@RestController
@RequestMapping(value = "/api")
public class TranslationController {
    @Autowired
    private TranslationService translationService;

    @RequestMapping(value = "/users/me/translations", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public CreatedResourceDto addTranslation(
            @AuthenticationPrincipal User user,
            @RequestBody @Validated InputTranslationDto dto
    ) {
        return translationService.addUserTranslation(user.getId(), dto);
    }

    @RequestMapping(value = "/users/me/translations", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public TranslationsDto viewUserTranslations(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(required = false, defaultValue = "20") Integer maxRecords,
            @RequestParam(required = false, defaultValue = "") String searchSubstring,
            @RequestParam(required = false) PartOfSpeech partOfSpeech,

            @RequestParam(required = false) String sourceText,
            @RequestParam(required = false) String sourceLangKey,
            @RequestParam(required = false) String resultLangKey
    ) {
        if (sourceText != null && sourceLangKey != null && resultLangKey != null)
            return translationService.checkForUserTranslations(
                    user.getId(),
                    sourceText,
                    sourceLangKey,
                    resultLangKey
            );

        return translationService.getUserTranslations(
                user.getId(),
                pageIndex,
                maxRecords,
                searchSubstring,
                partOfSpeech
        );
    }

    @RequestMapping(value = "/users/me/translations/{translationId}", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public TranslationDetailDto viewUserTranslationDetail(
            @AuthenticationPrincipal User user,
            @PathVariable Long translationId
    ) {
        return translationService.getUserTranslationDetailDto(user.getId(), translationId);
    }

    @RequestMapping(value = "/users/me/translations/{translationId}/note", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public TranslationDetailDto viewTranslationNote(
            @AuthenticationPrincipal User user,
            @PathVariable Long translationId
    ) {
        return translationService.getUserTranslationDetailDto(user.getId(), translationId);
    }

    @RequestMapping(value = "/users/me/translations/{translationId}",method = RequestMethod.PUT)
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editTranslation(
            @AuthenticationPrincipal User user,
            @RequestBody @Validated InputTranslationDto dto,
            @PathVariable Long translationId
    ){
        translationService.updateTranslation(
                user.getId(),
                translationId,
                dto
        );
    }

    @RequestMapping(value = "/users/me/translations/{translationId}/note",method = RequestMethod.PUT)
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editTranslationNote(
            @AuthenticationPrincipal User user,
            @RequestBody @Validated TranslationNoteDto dto,
            @PathVariable Long translationId
    ){
        translationService.updateTranslationNote(
                user.getId(),
                translationId,
                dto
        );
    }

    @RequestMapping(value = "/users/me/translations/{translationId}", method = RequestMethod.DELETE)
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserTranslation(
            @AuthenticationPrincipal User user,
            @PathVariable Long translationId
    ) {
         translationService.deleteUserTranslation(user.getId(),translationId);
    }
}