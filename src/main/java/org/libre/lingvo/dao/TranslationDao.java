package org.libre.lingvo.dao;

import org.libre.lingvo.dto.LangCodesPairDto;
import org.libre.lingvo.entities.Translation;
import org.libre.lingvo.reference.PartOfSpeech;
import org.libre.lingvo.reference.SortingOptions;
import org.libre.lingvo.reference.TranslationSortFieldOptions;

import java.util.List;

/**
 * Created by igorek2312 on 29.10.16.
 */
public interface TranslationDao extends GenericDao<Translation, Long> {
    List<Translation> findFilteredUserTranslations(
            Long userId,
            String searchSubstring,
            PartOfSpeech partOfSpeech,
            String sourceLangCode,
            String resultLangCode,
            Boolean learned, List<Long> tagIds, SortingOptions sortingOption, TranslationSortFieldOptions sortFieldOption, Integer pageIndex, Integer maxRecords
    );

    Long countFilteredUserTranslations(
            Long userId,
            String searchSubstring,
            PartOfSpeech partOfSpeech,
            String sourceLangCode,
            String resultLangCode,
            Boolean learned, List<Long> tagIds);

    Long countTotalUserTranslations(Long userId);

    boolean existsSuchTranslation(
            Long userId,
            String sourceText,
            String sourceLangKey,
            String resultText,
            String resultLangKey,
            PartOfSpeech partOfSpeech
    );

    List<Translation> findSuchTranslations(
            Long userId,
            String sourceText,
            String sourceLangKey,
            String resultLangKey
    );

    boolean existsOtherTranslationsDependedOnWord(Long translationId, Long wordId);

    List<LangCodesPairDto> findLangCodesByUserId(Long userId);

    List<PartOfSpeech> findPartsOfSpeechByUserId(Long userId);
}
