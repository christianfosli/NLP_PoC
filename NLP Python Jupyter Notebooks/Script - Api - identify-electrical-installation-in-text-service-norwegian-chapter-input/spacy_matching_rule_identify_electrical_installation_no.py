import spacy
from spacy.lang.nb import Norwegian
from spacy.matcher import Matcher

def is_float(n):
    try:
        support_float_with_norwegian_format = n.replace(',','.')
        float_n = float(support_float_with_norwegian_format)
    except ValueError:
        return False
    else:
        return True

def identify_electrical_installation_in_text(text):
    nlp = Norwegian()

    doc = nlp(text)

    matcher = Matcher(nlp.vocab)

    #
    # START - spaCy patterns
    #

    # WATER_VESSEL
    water_vessel_pattern = [{"LOWER": {"IN": ["fartøy"]}}]
    matcher.add("WATER_VESSEL", None, water_vessel_pattern)

    # ELECTRICAL_INSTALLATION
    electrical_installation_pattern_1 = [{"LOWER": {"IN": ["elektrisk"]}},{"LOWER": {"IN": ["anlegg"]}}]
    electrical_installation_pattern_2 = [{"LOWER": {"IN": ["elektriske"]}},{"LOWER": {"IN": ["anlegg"]}}]
    matcher.add("ELECTRICAL_INSTALLATION", None, electrical_installation_pattern_1, electrical_installation_pattern_2)

    # VOLTAGE_PREFIX 
    matcher.add(
        "VOLTAGE_PREFIX",
        None,
        [{"LOWER": {"IN": ["mindre"]}},{"LOWER": {"IN": ["enn"]}}],
        [{"LOWER": {"IN": ["inntil"]}}],
        [{"LOWER": {"IN": ["over"]}}])

    # VOLTAGE
    voltage_pattern = [
        {"TEXT": {"REGEX": "[0-9]+[,]+[0-9]|[0-9]"}},
        {"LOWER": {"IN": ["v"]}}]
    matcher.add("VOLTAGE", None, voltage_pattern)

    #
    # END - spaCy patterns
    #

    result = []

    for match_id, token_start, token_end in matcher(doc):

        match_id_as_string = nlp.vocab.strings[match_id]

        final_token_start = token_start
        final_token_end = token_end

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

def identify_electrical_installation_in_spacy_lines(spacy_lines):

    result = []

    for spacy_line in spacy_lines:

        if spacy_line['text'] != "\n" and len(spacy_line['text']) > 0:

            result_in_a_list = identify_electrical_installation_in_text(spacy_line['text'])

            # this result might be empty.

            if result_in_a_list:

                # we have results to merge in.
                # merge in the result using this function.
                new_spacy_line = merge_spacy_entity_results_to_spacy_ner_format(spacy_line,result_in_a_list)

                result.append(new_spacy_line)
            else:
                result.append(spacy_line)
        else:
            result.append(spacy_line)

    return result