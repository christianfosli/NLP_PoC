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

def is_int(n):
    try:
        float_n = float(n)
        int_n = int(float_n)
    except ValueError:
        return False
    else:
        return float_n == int_n

def identify_VESSEL_TYPE_in_text(text):
    nlp = English()
    doc = nlp(text)
    matcher = Matcher(nlp.vocab)

    #
    # START - spaCy patterns
    #

    matcher.add(
        "VESSEL_TYPE",
        [
            [
                {"TEXT": {"REGEX": "^([a-zA-Z]+)(skip|fartøy|båt)$"}}
            ],
            [
                {"LOWER": {"IN": ["lekter"]}}
            ],
            [
                {"LOWER": {"IN": ["flyttbare"]}},
                {"LOWER": {"IN": ["innretninger"]}}
            ]
        ])

    #
    # END - spaCy patterns
    #

    result = []

    for match_id, token_start, token_end in matcher(doc):

        match_id_as_string = nlp.vocab.strings[match_id]
        final_token_start = token_start
        final_token_end = token_end

        spacy_pattern_detection = doc[token_start:token_end]
        spacy_pattern_detection_as_lower_text = spacy_pattern_detection.text.lower()
        
        #
        # Expand VESSEL_TYPE?
        #

        if match_id_as_string == "VESSEL_TYPE" and token_start > 0:
            if spacy_pattern_detection_as_lower_text == "fiskefartøy" and token_start > 1:
                prev_word_1_token_number = token_start - 1
                prev_word_1_token = doc[prev_word_1_token_number]
                prev_word_2_token_number = token_start - 2
                prev_word_2_token = doc[prev_word_2_token_number]
                if (prev_word_2_token.text.lower() == "fangst-" and
                    prev_word_1_token.text.lower() == "og"):
                    # Example: fangst- og fiskefartøy
                    # Expanding.
                    final_token_start = prev_word_2_token_number
            else:
                if token_start > 0:

                    prev_word_1_token_number = token_start - 1
                    prev_word_1_token = doc[prev_word_1_token_number]

                    if (prev_word_1_token.text.lower() == "hurtiggående" or 
                        prev_word_1_token.text.lower() == "kjemikalie"):
                        # Example: hurtiggående passasjerskip / hurtiggående passasjerfartøy
                        # Expanding.
                        final_token_start = prev_word_1_token_number

                    if token_start > 2:
                        prev_word_2_token_number = token_start - 2
                        prev_word_2_token = doc[prev_word_2_token_number]

                        prev_word_3_token_number = token_start - 3
                        prev_word_3_token = doc[prev_word_3_token_number]

                        if (prev_word_3_token.text.lower() == "olje" and
                            prev_word_2_token.text.lower() == "-"):

                            # Example: olje-kjemikalie tankerskip
                            # Expanding.
                            final_token_start = prev_word_3_token_number

                    if (prev_word_1_token.text.lower() == "-" and token_start > 1):
                        prev_word_2_token_number = token_start - 2
                        prev_word_2_token = doc[prev_word_2_token_number]
                        if prev_word_2_token.text.lower() == "roro":
                            # Example: roro-passasjerskip
                            # Expanding.
                            final_token_start = prev_word_2_token_number

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

def identify_VESSEL_TYPE_in_norwegian_spacy_lines(spacy_lines):

    result = []

    for spacy_line in spacy_lines:

        if spacy_line['text'] != "\n" and len(spacy_line['text']) > 0:

            nlp_result_list = identify_VESSEL_TYPE_in_text(spacy_line['text'])

            # this result might be empty.

            if nlp_result_list:

                # we have results to merge in.
                # merge in the result using this function.
                new_spacy_line = merge_spacy_entity_results_to_spacy_ner_format(spacy_line,nlp_result_list)

                result.append(new_spacy_line)
            else:
                result.append(spacy_line)
        else:
            result.append(spacy_line)

    return result