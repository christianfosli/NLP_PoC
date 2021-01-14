import spacy
from spacy.lang.en import English
from spacy.matcher import Matcher

def identify_date_in_text(text):
    nlp = English()

    doc = nlp(text)

    matcher = Matcher(nlp.vocab)

    date_pattern = [{'IS_DIGIT': True},
    {"LOWER": {"IN": ["january","february","march","april","may","june","july","august","september","october","november","december"]}},
    {'IS_DIGIT': True, 'LENGTH': 4}]

    matcher.add("DATE", None, date_pattern)

    result = []

    for match_id, token_start, token_end in matcher(doc):

        # convert token_span to char_span.
        # char_span is needed to display correctly withdisplacy.render().
        span = doc[token_start:token_end]
        span_char_start = span[0].idx
        span_char_end = span[-1].idx + len(span[-1].text)

        match_id_as_string = nlp.vocab.strings[match_id]

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

def identify_date_in_spacy_lines(spacy_lines):

    result = []

    for spacy_line in spacy_lines:

        if spacy_line['text'] != "\n" and len(spacy_line['text']) > 0:

            identify_date_in_text_result_in_a_list = identify_date_in_text(spacy_line['text'])

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