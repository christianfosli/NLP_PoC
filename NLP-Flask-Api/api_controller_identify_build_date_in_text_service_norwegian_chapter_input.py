import spacy
from spacy.lang.en import English
from transform_text_service_input_to_spacy_format import get_data_from_text_service_item_url

def create_api_response_for_post_identify_build_date_in_text_service_norwegian_chapter_input(title_dictionary,forward_filtered_result_with_only_the_things_we_are_looking_for):

    nlp = English()

    forward_result = []

    ### TEMP AREA
    temp_detection_dictionary = {}
    temp_check_before_reset = {}
    ###

    for line in forward_filtered_result_with_only_the_things_we_are_looking_for:

        # new line and temp reset
        temp_detection_dictionary.clear()
        temp_check_before_reset.clear()

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
            
            #print(ent_text + " - " + ent_label + " (" + str(ent_token_span_start) + ":" + str(ent_token_span_end) + ")")

            #
            # Statment builder
            #

            if ent_label == "WATER_VESSEL":
                if "START_detected" not in temp_detection_dictionary:
                    temp_detection_dictionary["START_detected"] = True
                else: # restart with new term
                    temp_check_before_reset = dict(temp_detection_dictionary)
                    temp_detection_dictionary.clear()
                    temp_detection_dictionary["START_detected"] = True
                    
            elif ent_label == "CONSTRUCT":
                if ("START_detected" in temp_detection_dictionary and
                    "CONSTRUCT_detected" not in temp_detection_dictionary):
                    temp_detection_dictionary["CONSTRUCT_detected"] = True
                else: # reset
                    temp_check_before_reset = dict(temp_detection_dictionary)
                    temp_detection_dictionary.clear()
                    
            elif ent_label == "DATE_PREFIX":
                if ("START_detected" in temp_detection_dictionary and
                    "CONSTRUCT_detected" in temp_detection_dictionary and
                    "DATE_PREFIX_value" not in temp_detection_dictionary and
                    "DATE_value_1" not in temp_detection_dictionary and
                    "DATE_SEPARATOR_value" not in temp_detection_dictionary and
                    "DATE_value_2" not in temp_detection_dictionary):
                    temp_detection_dictionary["DATE_PREFIX_value"] = ent_text
                else: # reset
                    temp_check_before_reset = dict(temp_detection_dictionary)
                    temp_detection_dictionary.clear()

            elif ent_label == "DATE":
                if ("START_detected" in temp_detection_dictionary and
                    "CONSTRUCT_detected" in temp_detection_dictionary and
                    "DATE_value_1" not in temp_detection_dictionary and
                    "DATE_value_1_token_end" not in temp_detection_dictionary):
                    temp_detection_dictionary["DATE_value_1"] = ent_text
                    temp_detection_dictionary["DATE_value_1_token_end"] = ent_token_span_end
    
                elif ("START_detected" in temp_detection_dictionary and
                    "CONSTRUCT_detected" in temp_detection_dictionary and
                    "DATE_value_1" in temp_detection_dictionary and
                    "DATE_SEPARATOR_value" in temp_detection_dictionary and
                    "DATE_value_2" not in temp_detection_dictionary):
                    temp_detection_dictionary['DATE_value_2'] = ent_text
                    # because this is the last value in a statment:
                    temp_check_before_reset = dict(temp_detection_dictionary)
                    temp_detection_dictionary.clear()
                else: # reset
                    temp_check_before_reset = dict(temp_detection_dictionary)
                    temp_detection_dictionary.clear()

            elif ent_label == "DATE_SEPARATOR":
                if ("START_detected" in temp_detection_dictionary and
                    "CONSTRUCT_detected" in temp_detection_dictionary and
                    "DATE_value_1" in temp_detection_dictionary and
                    "DATE_value_1_token_end" in temp_detection_dictionary and
                    "DATE_SEPARATOR_value" not in temp_detection_dictionary):
                    # Q: Is the separator the next term after value 1?
                    if temp_detection_dictionary["DATE_value_1_token_end"] == ent_token_span_start:
                        # A: Yes, this separator is the first word after value 1
                        temp_detection_dictionary["DATE_SEPARATOR_value"] = ent_text
                    else: # reset
                        # A: No. Reject value and reset.
                        temp_check_before_reset = dict(temp_detection_dictionary)
                        temp_detection_dictionary.clear()
                else: # reset
                    temp_check_before_reset = dict(temp_detection_dictionary)
                    temp_detection_dictionary.clear()

            #
            # Statment concluder
            # Q: Do we have what we need to build a statment?
            #

            # The statment builder have restarted.
            # Check what we have for a statment before continuing.
            if len(temp_check_before_reset) > 0:
                # If we have a double value statement
                if ("START_detected" in temp_check_before_reset and
                    "CONSTRUCT_detected" in temp_check_before_reset and
                    "DATE_value_1" in temp_check_before_reset and
                    "DATE_SEPARATOR_value" in temp_check_before_reset and
                    "DATE_value_2" in temp_check_before_reset):
                    detection_with_url_metadata = dict(metadata_from_url)
                    if "DATE_PREFIX_value" in temp_check_before_reset:
                        detection_with_url_metadata["date_context"] = temp_check_before_reset["DATE_PREFIX_value"]
                    detection_with_url_metadata["date_value_1"] = temp_check_before_reset["DATE_value_1"]
                    detection_with_url_metadata["date_separator"] = temp_check_before_reset["DATE_SEPARATOR_value"]
                    detection_with_url_metadata["date_value_2"] = temp_check_before_reset["DATE_value_2"]
                    forward_result.append(detection_with_url_metadata)
                # If we have a single value statment
                elif ("START_detected" in temp_check_before_reset and
                    "CONSTRUCT_detected" in temp_check_before_reset and
                    "DATE_value_1" in temp_check_before_reset):
                    detection_with_url_metadata = dict(metadata_from_url)
                    if "DATE_PREFIX_value" in temp_check_before_reset:
                        detection_with_url_metadata["date_context"] = temp_check_before_reset["DATE_PREFIX_value"]
                    detection_with_url_metadata["date_value_1"] = temp_check_before_reset["DATE_value_1"]
                    forward_result.append(detection_with_url_metadata)
                temp_check_before_reset.clear()

            # Conclude on current detections
            if ("START_detected" in temp_detection_dictionary and
                "CONSTRUCT_detected" in temp_detection_dictionary and
                "DATE_value_1" in temp_detection_dictionary and
                "DATE_SEPARATOR_value" in temp_detection_dictionary and
                "DATE_value_2" in temp_detection_dictionary):
                # we have a full statment.
                # add and reset.
                detection_with_url_metadata = dict(metadata_from_url)
                if "DATE_PREFIX_value" in temp_detection_dictionary:
                    detection_with_url_metadata["date_context"] = temp_detection_dictionary["DATE_PREFIX_value"]
                detection_with_url_metadata["date_value_1"] = temp_detection_dictionary["DATE_value_1"]
                detection_with_url_metadata["date_separator"] = temp_detection_dictionary["DATE_SEPARATOR_value"]
                detection_with_url_metadata["date_value_2"] = temp_detection_dictionary["DATE_value_2"]
                forward_result.append(detection_with_url_metadata)
                temp_detection_dictionary.clear()

            else:
                # get next ent
                next_ent_index_number = ent_index_number + 1
                next_ent_label = ""
                if next_ent_index_number <= last_index_number_of_ents:
                    next_ent = ents[next_ent_index_number]
                    next_ent_label = next_ent["label"]
                # Q: Do we have enough for a new statment?
                if ("START_detected" in temp_detection_dictionary and
                    "CONSTRUCT_detected" in temp_detection_dictionary and
                    "DATE_value_1" in temp_detection_dictionary):
                    # A: Yes, we have enough for a new statment.
                    # Is the next ent relevant?
                    if ("DATE_SEPARATOR_value" not in temp_detection_dictionary and
                        next_ent_label == "DATE_SEPARATOR"):
                        continue # we want the next ent
                    elif ("DATE_SEPARATOR_value" in temp_detection_dictionary and
                        "DATE_value_2" not in temp_detection_dictionary):
                        continue # we know that the next value is a date
                    else: # add the statment and move on
                        detection_with_url_metadata = dict(metadata_from_url)
                        if "DATE_PREFIX_value" in temp_detection_dictionary:
                            detection_with_url_metadata["date_context"] = temp_detection_dictionary["DATE_PREFIX_value"]
                        detection_with_url_metadata["date_value_1"] = temp_detection_dictionary["DATE_value_1"]
                        forward_result.append(detection_with_url_metadata)
                        temp_detection_dictionary.clear()

    return forward_result