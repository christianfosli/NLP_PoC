import spacy
from spacy.lang.en import English
from transform_text_service_input_to_spacy_format import get_data_from_text_service_item_url

def create_api_response_for_post_identify_RADIO_AREA_in_text_service_norwegian_chapter(title_dictionary,forward_filtered_result_with_only_the_things_we_are_looking_for):

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

        # add chapter_title and section_title
        if "chapter_title" in title_dictionary:
            metadata_from_url['chapter_title'] = title_dictionary['chapter_title']
        if "section_title_in_dictionary" in title_dictionary:
            section_title_dictionary = title_dictionary['section_title_in_dictionary']
            if text_service_url in section_title_dictionary:
                metadata_from_url['section_title'] = section_title_dictionary[text_service_url]

        # For each ent in line
        for ent_index_number, ent in enumerate(ents):

            ent_label = ent['label']
            ent_start = ent['start']
            ent_end = ent['end']

            ent_text = text[ent_start:ent_end] # same as: doc[ent_token_span.start:ent_token_span.end]
            ent_doc = nlp(ent_text)
            words_in_doc_count = len(ent_doc)

            ent_token_span = doc.char_span(ent_start,ent_end)
            ent_token_span_start = ent_token_span.start
            ent_token_span_end = ent_token_span.end
            
            #
            # Statment concluder
            #

            if ent_label == "RADIO_AREA_TYPE":

                detection_with_url_metadata = dict(metadata_from_url)
                detection_with_url_metadata["radio_area_type_text"] = ent_text
                forward_result.append(detection_with_url_metadata)

    return forward_result