import spacy
from transform_text_service_input_to_spacy_format import get_data_from_text_service_item_url
from pathlib import Path

path = Path('NER_models_in_use/')

def find_named_entities(spacy_line, path, language):
    #import model for English
    if language == 'en':
        model_path = path / 'en_model_in_use'
        nlp = spacy.load(model_path)
    #import model for Norwegian
    else:
        model_path = path / 'no_model_in_use'
        nlp = spacy.load(model_path)
    result = []
    to_return = []
    doc = nlp(spacy_line['text'])
    for ent in doc.ents:
        entity = ent.label_
        ent_text = ent.text
        start_char = ent.start_char
        end_char = ent.end_char
        spacy_object = {'ent_text': ent_text, 'label': entity, 'start': start_char, 'end': end_char}
        result.append(spacy_object)
    result_object = {'text': spacy_line['text'], 'ents': result, 'title': spacy_line['title']}
    to_return.append(result_object)
    return to_return


def identify_named_entities_in_spacy_lines(spacy_lines, language):
    result = []
    for spacy_line in spacy_lines:

        if spacy_line['text'] != "\n" and len(spacy_line['text']) > 0:
            entities = find_named_entities(spacy_line, path, language)

            if entities:
                result.append(entities)
            else:
                result.append(spacy_line)
        else:
            result.append(spacy_line)        
    return(result)            
   
