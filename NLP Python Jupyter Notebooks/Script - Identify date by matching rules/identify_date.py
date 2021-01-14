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

    for match_id, start, end in matcher(doc):

        match_id_as_string = nlp.vocab.strings[match_id]
        identified_entity = {'start': int(start), 'end': int(end), 'label': match_id_as_string}
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