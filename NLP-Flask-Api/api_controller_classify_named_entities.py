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
                
                result_text_service_url = get_data_from_text_service_item_url(text_service_url)
                if "regulation_year" in result_text_service_url:
                    merged_line_result['regulation_year'] = result_text_service_url['regulation_year']
                if "regulation_month" in result_text_service_url:
                    merged_line_result['regulation_month'] = result_text_service_url['regulation_month']
                if "regulation_day" in result_text_service_url:
                    merged_line_result['regulation_day'] = result_text_service_url['regulation_day']
                if "regulation_id" in result_text_service_url:
                    merged_line_result['regulation_id'] = result_text_service_url['regulation_id']
                if "chapter_number" in result_text_service_url:
                    merged_line_result['chapter_number'] = result_text_service_url['chapter_number']
                if "section_number" in result_text_service_url:
                    merged_line_result['section_number'] = result_text_service_url['section_number']
                if "part_number" in result_text_service_url:
                    merged_line_result['part_number'] = result_text_service_url['part_number']
                if "sub_part_number" in result_text_service_url:
                    merged_line_result['sub_part_number'] = result_text_service_url['sub_part_number']
                if ents:
                    for ent in ents:
                        entity = ent['ent_text']
                        label = ent['label']
                        entities.append({'entity': entity, 'class label': label})
                merged_line_result['named entities'] = entities
                forward_result.append(merged_line_result)
    return forward_result
