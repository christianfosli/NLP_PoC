import spacy
from spacy.lang.en import English
from spacy.matcher import Matcher

def is_float(n):
    try:
        support_float_with_norwegian_format = n.replace(',','.')
        float_n = float(support_float_with_norwegian_format)
    except ValueError:
        return False
    else:
        return True

def identify_build_date_in_text(text):
    nlp = English()
    doc = nlp(text)
    matcher = Matcher(nlp.vocab)

    #
    # START - spaCy patterns
    #

    # WATER_VESSEL
    water_vessel_pattern = [{"LOWER": {"IN": ["fartøy"]}}]
    matcher.add("WATER_VESSEL", None, water_vessel_pattern)

    # DATE
    date_pattern = [
        {'IS_DIGIT': True},
        {"LOWER": {"IN": ["."]}},
        {"LOWER": {"IN": ["januar","februar","mars","april","mai","juni","juli","august","september","oktober","november","desember"]}},
        {'IS_DIGIT': True, 'LENGTH': 4}]
    matcher.add("DATE", None, date_pattern)

    # CONSTRUCT
    matcher.add("CONSTRUCT", None, [{"LOWER": {"IN": ["bygget"]}}])

    #
    # END - spaCy patterns
    #

    result = []

    for match_id, token_start, token_end in matcher(doc):

        match_id_as_string = nlp.vocab.strings[match_id]
        final_token_start = token_start
        final_token_end = token_end
        
        #
        # Detect DATE prefix
        #
        if match_id_as_string == "DATE" and token_start > 0:

            prev_word_1_token_number = token_start - 1
            prev_word_1_token = doc[prev_word_1_token_number]

            if prev_word_1_token.text in ("og", "til"):
                # DATE_SEPARATOR detected
                prev_word_1_char_span_start_number = prev_word_1_token.idx
                prev_word_1_char_span_end_number = prev_word_1_char_span_start_number + len(prev_word_1_token.text)
                identified_entity = {'start': prev_word_1_char_span_start_number, 'end': prev_word_1_char_span_end_number, 'label': "DATE_SEPARATOR"}
                result.append(identified_entity)

            elif prev_word_1_token.text in ("før", "etter", "mellom"):
                # DATE_PREFIX detected
                prev_word_1_char_span_start_number = prev_word_1_token.idx
                prev_word_1_char_span_end_number = prev_word_1_char_span_start_number + len(prev_word_1_token.text)
                identified_entity = {'start': prev_word_1_char_span_start_number, 'end': prev_word_1_char_span_end_number, 'label': "DATE_PREFIX"}
                result.append(identified_entity)

        #
        # convert token_span to char_span.
        # char_span is needed to display correctly withdisplacy.render().
        #
        span = doc[final_token_start:final_token_end]
        span_char_start = span[0].idx
        span_char_end = span[-1].idx + len(span[-1].text)

        # return result
        identified_entity = {'start': span_char_start, 'end': span_char_end, 'label': match_id_as_string}
        result.append(identified_entity)

    return result

def merge_spacy_entity_results_to_spacy_ner_format(spacy_ner_formated_text_line,spacy_ner_entities_to_be_merged_in_as_a_list):
    text = spacy_ner_formated_text_line['text']
    ents = spacy_ner_formated_text_line['ents']
    title = spacy_ner_formated_text_line['title']

    if ents:
        for ent in spacy_ner_entities_to_be_merged_in_as_a_list:
            ents.append(ent)
    else:
        ents = spacy_ner_entities_to_be_merged_in_as_a_list

    return {'text': text, 'ents': sorted(ents, key=lambda x: x['start']), 'title': title}

def identify_build_date_in_norwegian_spacy_lines(spacy_lines):

    result = []

    for spacy_line in spacy_lines:

        if spacy_line['text'] != "\n" and len(spacy_line['text']) > 0:

            identify_date_in_text_result_in_a_list = identify_build_date_in_text(spacy_line['text'])

            # this result might be empty.

            if identify_date_in_text_result_in_a_list:

                # we have results to merge in.
                # merge in the result using this function.
                new_spacy_line = merge_spacy_entity_results_to_spacy_ner_format(spacy_line,identify_date_in_text_result_in_a_list)

                result.append(new_spacy_line)
            else:
                result.append(spacy_line)
        else:
            result.append(spacy_line)

    return result