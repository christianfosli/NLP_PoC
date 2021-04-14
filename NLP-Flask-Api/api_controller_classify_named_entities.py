import spacy
from transform_text_service_input_to_spacy_format import get_data_from_text_service_item_url
def create_api_response_for_post_identify_named_entities(forward_filtered_result):
    forward_result = []

    for part in forward_filtered_result:
            for line in part:
                ents = line['ents']
                if ents:
                    for ent in ents:
                        entity = ent['ent_text']
                        label = ent['label']
                        forward_result.append({'class label': label, 'entity': entity})

    return forward_result
