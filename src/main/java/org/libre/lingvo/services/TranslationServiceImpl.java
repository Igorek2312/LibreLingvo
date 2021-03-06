package org.libre.lingvo.services;

import org.libre.lingvo.config.aspects.annotaions.NotForReadOnly;
import org.libre.lingvo.dao.TranslationDao;
import org.libre.lingvo.dao.UserDao;
import org.libre.lingvo.dao.WordDao;
import org.libre.lingvo.dto.*;
import org.libre.lingvo.dto.exception.CustomError;
import org.libre.lingvo.dto.exception.CustomErrorException;
import org.libre.lingvo.entities.Translation;
import org.libre.lingvo.entities.User;
import org.libre.lingvo.entities.Word;
import org.libre.lingvo.reference.PartOfSpeech;
import org.libre.lingvo.reference.SortingOptions;
import org.libre.lingvo.reference.TranslationSortFieldOptions;
import org.libre.lingvo.utils.dto.converters.TagDtoConverter;
import org.libre.lingvo.utils.dto.converters.TranslationDtoConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.libre.lingvo.utils.EntityUtil.findOrThrowNotFound;
import static org.libre.lingvo.utils.EntityUtil.getOneOrThrowNotFound;

/**
 * Created by igorek2312 on 29.10.16.
 */
@Service
@Transactional
public class TranslationServiceImpl implements TranslationService {
    private TranslationDao translationDao;

    private WordDao wordDao;

    private UserDao userDao;

    private TranslationDtoConverter translationDtoConverter;

    private TagDtoConverter tagDtoConverter;

    @Autowired
    public TranslationServiceImpl(TranslationDao translationDao, WordDao wordDao, UserDao userDao, TranslationDtoConverter translationDtoConverter, TagDtoConverter tagDtoConverter) {
        this.translationDao = translationDao;
        this.wordDao = wordDao;
        this.userDao = userDao;
        this.translationDtoConverter = translationDtoConverter;
        this.tagDtoConverter = tagDtoConverter;
    }

    private Supplier<Word> getWordSupplier(String text, String langKey) {
        return () -> {
            Word word = new Word();
            word.setText(text);
            word.setLangCode(langKey);
            wordDao.create(word);
            return word;
        };
    }

    private void checkIfUserIsOwnerOfTranslation(Long userId, Translation translation) {
        if (!(Objects.equals(translation.getUser().getId(), userId)))
            throw new CustomErrorException(CustomError.FORBIDDEN);
    }

    @NotForReadOnly
    @Override
    public CreatedResourceDto addUserTranslation(Long userId, InputTranslationDto dto) {
        Translation translation = new Translation();
        User user = userDao.find(userId)
                .orElseThrow(() -> {
                    CustomError error = CustomError.NO_ENTITY_WITH_SUCH_ID;
                    error.setDescriptionArgs(User.class.getName(), userId);
                    return new CustomErrorException(error);
                });
        translation.setUser(user);

        String sourceText = dto.getSourceText().trim();
        String resultText = dto.getResultText().trim();
        String sourceLangKey = dto.getSourceLangCode();
        String resultLangKey = dto.getResultLangCode();

        boolean exists = translationDao.existsSuchTranslation(
                userId,
                sourceText,
                sourceLangKey,
                resultText,
                resultLangKey,
                dto.getPartOfSpeech()
        );
        if (exists)
            throw new CustomErrorException(CustomError.USER_HAS_ALREADY_SUCH_TRANSLATION);


        Word sourceWord = wordDao.findByTextAndLangCode(sourceText, sourceLangKey)
                .orElseGet(getWordSupplier(sourceText, sourceLangKey));

        Word resultWord = wordDao.findByTextAndLangCode(resultText, resultLangKey)
                .orElseGet(getWordSupplier(resultText, resultLangKey));

        translation.setSourceWord(sourceWord);
        translation.setResultWord(resultWord);
        translation.setPartOfSpeech(dto.getPartOfSpeech());
        translation.setNote(dto.getNote());
        translation.setLastModificationDate(new Date());
        translation.setUser(user);

        translationDao.create(translation);
        return new CreatedResourceDto(translation.getId());
    }

    @Override
    public TranslationsDto checkForUserTranslations(
            Long userId,
            String sourceText,
            String sourceLangCode,
            String resultLangCode
    ) {
        List<TranslationListItemDto> translations = translationDao.findSuchTranslations(
                userId,
                sourceText.trim(),
                sourceLangCode,
                resultLangCode
        )
                .stream()
                .map(translation -> {
                    translation.incrementViews();
                    translationDao.update(translation);
                    return translationDtoConverter.convertToTranslationDto(translation);
                })
                .collect(Collectors.toList());

        TranslationsDto dto = new TranslationsDto();
        dto.setTranslations(translations);

        return dto;
    }

    @Override
    public TranslationsDto getUserTranslations(
            Long userId,
            Integer pageIndex,
            Integer maxRecords,
            String searchSubstring,
            PartOfSpeech partOfSpeech,
            String sourceLangCode,
            String resultLangCode,
            Boolean learned,
            List<Long> tagIds,
            SortingOptions sortOrder,
            TranslationSortFieldOptions sortField
    ) {
        List<TranslationListItemDto> translations = translationDao.findFilteredUserTranslations(
                userId,
                searchSubstring,
                partOfSpeech,
                sourceLangCode,
                resultLangCode,
                learned,
                tagIds,
                sortOrder == null ? SortingOptions.DESC : sortOrder,
                sortField == null ? TranslationSortFieldOptions.SORT_MODIFICATION_DATE : sortField,
                pageIndex,
                maxRecords)
                .stream()
                .map(translationDtoConverter::convertToTranslationDto)
                .collect(Collectors.toList());


        Long filteredRecords = translationDao.countFilteredUserTranslations(
                userId,
                searchSubstring,
                partOfSpeech,
                sourceLangCode,
                resultLangCode,
                learned,
                tagIds
        );

        Long totalRecords = translationDao.countTotalUserTranslations(userId);

        List<LangCodesPairDto> langCodes = translationDao.findLangCodesByUserId(userId);

        List<PartOfSpeech> partsOfSpeech = translationDao.findPartsOfSpeechByUserId(userId);

        TranslationsDto dto = new TranslationsDto();
        dto.setTranslations(translations);
        dto.setFilteredRecords(filteredRecords);
        dto.setTotalRecords(totalRecords);
        dto.setLangCodesPairs(langCodes);
        dto.setPartsOfSpeech(partsOfSpeech);
        return dto;
    }

    @Override
    public TranslationDetailDto getUserTranslationDetailDto(Long userId, Long translationId) {
        Translation translation = findOrThrowNotFound(translationDao, translationId);
        checkIfUserIsOwnerOfTranslation(userId, translation);
        return translationDtoConverter.convertToTranslationDetailDto(translation);
    }

    @Override
    public TranslationNoteDto getUserTranslationNote(Long userId, Long translationId) {
        Translation translation = findOrThrowNotFound(translationDao, translationId);
        checkIfUserIsOwnerOfTranslation(userId, translation);
        return new TranslationNoteDto(translation.getNote());
    }

    private boolean wordsIsNotSame(String newWordText, String langKey, Word word) {
        return !(word.getText().equals(newWordText) && word.getLangCode().equals(langKey));
    }

    private void safeWordDelete(Long translationId, Word word) {
        boolean exists = translationDao.existsOtherTranslationsDependedOnWord(translationId, word.getId());
        if (!exists)
            wordDao.delete(word);
    }

    private Word safeWordUpdate(Long translationId, String newWordText, String newWordLangKey, Word word) {
        if (wordsIsNotSame(newWordText, newWordLangKey, word)) {
            safeWordDelete(translationId, word);

            return wordDao.findByTextAndLangCode(newWordText, newWordLangKey)
                    .orElseGet(getWordSupplier(newWordText, newWordLangKey));
        }
        return word;
    }

    @NotForReadOnly
    @Override
    public void updateTranslation(User user, Long translationId, InputTranslationDto dto) {
        Translation translation = findOrThrowNotFound(translationDao, translationId);
        checkIfUserIsOwnerOfTranslation(user.getId(), translation);

        Word sourceWord = translation.getSourceWord();
        Word resultWord = translation.getResultWord();
        translation.setSourceWord(null);
        translation.setResultWord(null);
        translationDao.update(translation);

        Word updatedSourceWord = safeWordUpdate(
                translation.getId(),
                dto.getSourceText(),
                dto.getSourceLangCode(),
                sourceWord
        );

        Word updatedResultWord = safeWordUpdate(
                translation.getId(),
                dto.getResultText(),
                dto.getResultLangCode(),
                resultWord
        );

        translation.setSourceWord(updatedSourceWord);
        translation.setResultWord(updatedResultWord);
        translation.setPartOfSpeech(dto.getPartOfSpeech());
        translation.setLastModificationDate(new Date());
        translation.setLearned(dto.getLearned());

        translationDao.update(translation);
    }

    @NotForReadOnly
    @Override
    public void updateTranslationNote(User user, Long translationId, TranslationNoteDto dto) {
        Translation translation = getOneOrThrowNotFound(translationDao, translationId);
        checkIfUserIsOwnerOfTranslation(user.getId(), translation);

        translation.setNote(dto.getNote());
        translation.setLastModificationDate(new Date());
        translationDao.update(translation);
    }

    @NotForReadOnly
    @Override
    public void deleteUserTranslation(Long userId, Long translationId) {
        Translation translation = findOrThrowNotFound(translationDao, translationId);
        checkIfUserIsOwnerOfTranslation(userId, translation);
        safeWordDelete(translationId, translation.getSourceWord());
        safeWordDelete(translationId, translation.getResultWord());

        translationDao.delete(translation);
    }

    @NotForReadOnly
    @Override
    public void deleteUserTranslations(User user, List<Long> ids) {
        ids.forEach(
                id -> deleteUserTranslation(user.getId(), id)
        );
    }

    @Override
    public List<TagDto> getTranslationTags(long userId, long translationId) {
        return findOrThrowNotFound(translationDao, translationId)
                .getTranslationTags().stream()
                .map(tTag -> tTag.getPk().getTag())
                .map(tagDtoConverter::convertToTagDto)
                .collect(Collectors.toList());
    }
}
