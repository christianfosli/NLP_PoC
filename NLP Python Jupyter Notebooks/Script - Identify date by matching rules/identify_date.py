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