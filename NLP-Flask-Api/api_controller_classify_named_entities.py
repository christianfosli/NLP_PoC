import spacy
from transform_text_service_input_to_spacy_format import get_data_from_text_service_item_url
def create_api_response_for_post_identify_named_entities(forward_filtered_result):
    forward_result = []

    for part in forward_filtered_result:
            for line in part:
                merged_line_result = {}
                text_service_url = line['title']
                ents = line['ents']
                entities = []
                
                if ents:
                    for ent in ents:
                        entity = ent['ent_text']
                        label = ent['label']
                        entities.append({'entity': entity, 'class label': label})
                    if len(entities) != 0:
                        merged_line_result['named entities'] = entities
                        forward_result.append(merged_line_result)
    return forward_result
