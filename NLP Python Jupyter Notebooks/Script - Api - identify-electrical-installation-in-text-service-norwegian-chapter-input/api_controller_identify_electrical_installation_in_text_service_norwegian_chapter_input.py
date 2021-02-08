import spacy
from spacy.lang.nb import Norwegian
from transform_text_service_input_to_spacy_format import get_data_from_text_service_item_url

def create_api_response_for_post_identify_electrical_installation_in_text_service_norwegian_chapter_input(forward_filtered_result_with_only_the_things_we_are_looking_for):

    nlp = Norwegian()

    forward_result = []

    for line in forward_filtered_result_with_only_the_things_we_are_looking_for:

        # A line is to be interpreted as one sentence.

        # We look for statments about something that starts with WATER_VESSEL and ends with VOLTAGE.

        # Detection of multiple statments in one line is supported.

        # Data
        text_service_url = line['title']
        text = line['text']
        ents = line['ents']

        # Metadata from URL
        metadata_from_url = {}
        result_text_service_url = get_data_from_text_service_item_url(text_service_url)
        if "regularion_year" in result_text_service_url:
            metadata_from_url['regularion_year'] = result_text_service_url['regularion_year']
        if "regularion_month" in result_text_service_url:
            metadata_from_url['regularion_month'] = result_text_service_url['regularion_month']
        if "regularion_day" in result_text_service_url:
            metadata_from_url['regularion_day'] = result_text_service_url['regularion_day']
        if "regularion_id" in result_text_service_url:
            metadata_from_url['regularion_id'] = result_text_service_url['regularion_id']
        if "chapter_number" in result_text_service_url:
            metadata_from_url['chapter_number'] = result_text_service_url['chapter_number']
        if "section_number" in result_text_service_url:
            metadata_from_url['section_number'] = result_text_service_url['section_number']
        if "part_number" in result_text_service_url:
            metadata_from_url['part_number'] = result_text_service_url['part_number']
        if "sub_part_number" in result_text_service_url:
            metadata_from_url['sub_part_number'] = result_text_service_url['sub_part_number']

        #This will be reset on detection of START ent and END ent.
        temp_detection_dictionary = {}
        temp_term_between_start_and_end_detected = False

        for ent_id, ent in enumerate(ents):

            ent_label = ent['label']
            ent_start = ent['start']
            ent_end = ent['end']
            ent_text = text[ent_start:ent_end]
            ent_doc = nlp(ent_text)
            words_in_doc_count = len(ent_doc)

            if ent_label == "WATER_VESSEL":
                # This is the START of a statment.
                # Therefore resetting detetection dictionary: 
                temp_detection_dictionary = {}
                temp_term_between_start_and_end_detected = False

            elif ent_label == "ELECTRICAL_INSTALLATION":
                temp_term_between_start_and_end_detected = True

            elif ent_label == "VOLTAGE_PREFIX":
                # Example: mindre enn
                temp_detection_dictionary['voltage_prefix'] = ent_doc.text

            elif ent_label == "VOLTAGE" and words_in_doc_count == 2:
                # This is the END of a statment.

                if temp_term_between_start_and_end_detected == True:
                    # Statment is complete.
                    # Adding result to output list.

                    # Example: 10,67 meter
                    temp_detection_dictionary['voltage_value'] = ent_doc[0].text
                    temp_detection_dictionary['measurement_text'] = ent_doc[1].text

                    detection_with_url_metadata = temp_detection_dictionary | metadata_from_url
                    forward_result.append(detection_with_url_metadata)

                # Resetting detetection dictionary.
                temp_detection_dictionary = {}
                temp_term_between_start_and_end_detected = False

    return forward_result