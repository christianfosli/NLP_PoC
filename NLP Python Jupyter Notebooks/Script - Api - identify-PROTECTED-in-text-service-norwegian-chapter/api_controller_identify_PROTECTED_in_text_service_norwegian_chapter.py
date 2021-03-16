import spacy
from spacy.lang.en import English
from transform_text_service_input_to_spacy_format import get_data_from_text_service_item_url

def create_api_response_for_post_identify_PROTECTED_in_text_service_norwegian_chapter(forward_filtered_result_with_only_the_things_we_are_looking_for):

    nlp = English()
    forward_result = []

    for line in forward_filtered_result_with_only_the_things_we_are_looking_for:

        # Get NLP data from line
        text_service_url = line['title']
        text = line['text']
        ents = line['ents'] # discovered enteties in the line
        last_index_number_of_ents = len(ents)-1
        doc = nlp(text)

        # Get metadata from URL
        result_text_service_url = get_data_from_text_service_item_url(text_service_url)
        metadata_from_url = {}
        if "regulation_year" in result_text_service_url:
            metadata_from_url['regulation_year'] = result_text_service_url['regulation_year']
        if "regulation_month" in result_text_service_url:
            metadata_from_url['regulation_month'] = result_text_service_url['regulation_month']
        if "regulation_day" in result_text_service_url:
            metadata_from_url['regulation_day'] = result_text_service_url['regulation_day']
        if "regulation_id" in result_text_service_url:
            metadata_from_url['regulation_id'] = result_text_service_url['regulation_id']
        if "chapter_number" in result_text_service_url:
            metadata_from_url['chapter_number'] = result_text_service_url['chapter_number']
        if "section_number" in result_text_service_url:
            metadata_from_url['section_number'] = result_text_service_url['section_number']
        if "part_number" in result_text_service_url:
            metadata_from_url['part_number'] = result_text_service_url['part_number']
        if "sub_part_number" in result_text_service_url:
            metadata_from_url['sub_part_number'] = result_text_service_url['sub_part_number']

        detection_with_url_metadata = dict(metadata_from_url)
        detection_with_url_metadata["protected"] = True
        forward_result.append(detection_with_url_metadata)

    return forward_result