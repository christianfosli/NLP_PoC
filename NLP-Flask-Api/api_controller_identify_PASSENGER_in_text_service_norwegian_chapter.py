import spacy
from spacy.lang.en import English
from transform_text_service_input_to_spacy_format import get_data_from_text_service_item_url

def create_api_response_for_post_identify_PASSENGER_in_text_service_norwegian_chapter(forward_filtered_result_with_only_the_things_we_are_looking_for):

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
            
            #print(ent_text + " - " + ent_label + " (" + str(ent_token_span_start) + ":" + str(ent_token_span_end) + ")")

            #
            # Statment concluder
            #

            if ent_label == "PASSENGER":

                if "eller færre" in ent_text:
                    detection_with_url_metadata = dict(metadata_from_url)
                    detection_with_url_metadata["passenger_context"] = "eller færre"
                    detection_with_url_metadata["passenger_value_1"] = ent_doc[0].text
                    detection_with_url_metadata["measurement_text"] = ent_doc[words_in_doc_count-1].text
                    forward_result.append(detection_with_url_metadata)
                elif ent_text.startswith("mer enn"):
                    detection_with_url_metadata = dict(metadata_from_url)
                    detection_with_url_metadata["passenger_context"] = "mer enn"
                    detection_with_url_metadata["passenger_value_1"] = ent_doc[2].text
                    detection_with_url_metadata["measurement_text"] = ent_doc[words_in_doc_count-1].text
                    forward_result.append(detection_with_url_metadata)

    return forward_result