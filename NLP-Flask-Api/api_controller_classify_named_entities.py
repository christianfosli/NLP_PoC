import spacy
from pathlib import Path
from google_trans_new import google_translator  
from translate_norwegian_entities import translate_norwegian_entity, return_english_label
from transform_text_service_input_to_spacy_format import get_data_from_text_service_item_url
def create_api_response_for_post_identify_named_entities(forward_filtered_result, language):
    forward_result = []
    if language == 'en':
        for part in forward_filtered_result:
                for line in part:
                    ents = line['ents']
                    if ents:
                        for ent in ents:
                            entity = ent['ent_text']
                            label = ent['label']
                            forward_result.append({'class label en': label, 'entity en': entity})
    else:
        for part in forward_filtered_result:
            for line in part:
                ents = line['ents']
                if ents:
                    for ent in ents:
                        entity = ent['ent_text']
                        label = ent['label']
                        entity_eng = translate_norwegian_entity(entity)
                        label_eng = return_english_label(label)
                        forward_result.append({'class label no': label, 'class label en': label_eng, 'entity no': entity, 'entity en': entity_eng})
        

    return forward_result
