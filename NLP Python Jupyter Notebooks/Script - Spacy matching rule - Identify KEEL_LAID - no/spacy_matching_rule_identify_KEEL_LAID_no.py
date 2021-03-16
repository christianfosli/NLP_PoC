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

def identify_KEEL_LAID_in_text(text):
    nlp = English()
    doc = nlp(text)
    matcher = Matcher(nlp.vocab)
    tokens_in_doc_count = len(doc)

    #
    # START - spaCy patterns
    #

    matcher.add(
        "KEEL_LAID",
        [
            [
                {"LOWER": {"IN": ["kjølstrukk","kjølstrukket"]}}
            ]
        ])

    matcher.add("DATE", None, [{'IS_DIGIT': True, 'LENGTH': 4}])

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
        # Expand?
        #

        if match_id_as_string == "DATE" and token_start > 0:

            # At this point, DATE is just a year string. Example: 2021

            prev_word_1_token_number = token_start - 1
            prev_word_1_token = doc[prev_word_1_token_number]
            if prev_word_1_token.text in ("januar","februar","mars","april","mai","juni","juli","august","september","oktober","november","desember"):
                final_token_start = prev_word_1_token_number # expanding

                # Expand more?
                prev_word_2_token_number = token_start - 2
                prev_word_2_token = doc[prev_word_2_token_number]
                prev_word_3_token_number = token_start - 3
                prev_word_3_token = doc[prev_word_3_token_number]

                if prev_word_2_token.text == "." and is_int(prev_word_3_token.text):
                    final_token_start = prev_word_3_token_number # expanding

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

                    #
                    # Identify prefix or suffix
                    #

                    if final_token_start > 0:

                        prev_word_1_token_number = final_token_start - 1
                        prev_word_1_token = doc[prev_word_1_token_number]

                        if prev_word_1_token.text.lower() == "før":

                            # Prefix detected.

                            #
                            # convert token_span to char_span.
                            # char_span is needed to display correctly withdisplacy.render().
                            #
                            span = doc[prev_word_1_token_number:final_token_start]
                            span_char_start = span[0].idx
                            span_char_end = span[-1].idx + len(span[-1].text)

                            # return result
                            identified_entity = {'start': span_char_start, 'end': span_char_end, 'label': "DATE_PREFIX"}
                            result.append(identified_entity)


                    if ((final_token_end + 1) < tokens_in_doc_count):

                        next_word_1_token_number = final_token_end
                        next_word_1_token = doc[next_word_1_token_number]
                        next_word_2_token_number = final_token_end + 1
                        next_word_2_token = doc[next_word_2_token_number]

                        if (next_word_1_token.text.lower() == "eller" and
                            next_word_2_token.text.lower() == "senere"):

                                # Suffix detected.

                                #
                                # convert token_span to char_span.
                                # char_span is needed to display correctly withdisplacy.render().
                                #
                                span = doc[next_word_1_token_number:(next_word_1_token_number + 2)]
                                span_char_start = span[0].idx
                                span_char_end = span[-1].idx + len(span[-1].text)

                                # return result
                                identified_entity = {'start': span_char_start, 'end': span_char_end, 'label': "DATE_SUFFIX"}
                                result.append(identified_entity)

        elif match_id_as_string == "KEEL_LAID":

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

def identify_KEEL_LAID_in_norwegian_spacy_lines(spacy_lines):

    result = []

    for spacy_line in spacy_lines:

        if spacy_line['text'] != "\n" and len(spacy_line['text']) > 0:

            nlp_result_list = identify_KEEL_LAID_in_text(spacy_line['text'])

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