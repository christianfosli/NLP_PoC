import spacy
from spacy.lang.nb import Norwegian
from transform_text_service_input_to_spacy_format import get_data_from_text_service_item_url

def create_api_response_for_post_identify_vessel_length_overall_in_text_service_norwegian_chapter_input(forward_filtered_result_with_only_the_things_we_are_looking_for):

    nlp = Norwegian()

    forward_result = []

    for line in forward_filtered_result_with_only_the_things_we_are_looking_for:

        text_service_url = line['title']
        text = line['text']
        ents = line['ents']

        # result
        result_length_dictionary = {}
        result_length_prefix_dictionary = {}
        result_text_service_url = get_data_from_text_service_item_url(text_service_url)

        for ent in ents:

            ent_label = ent['label']
            ent_start = ent['start']
            ent_end = ent['end']
            ent_text = text[ent_start:ent_end]

            ent_doc = nlp(ent_text)
            words_in_doc_count = len(ent_doc)

            data = {}

            if ent_label == "LENGTH" and words_in_doc_count == 4:
                # Example: 10,67 og 15 meter
                data['length_value_1'] = ent_doc[0].text
                data['length_separator'] = ent_doc[1].text
                data['length_value_2'] = ent_doc[2].text
                data['measurement'] = ent_doc[3].text

                if not result_length_dictionary:
                    result_length_dictionary = data

            elif ent_label == "LENGTH" and words_in_doc_count == 2:
                # Example: 10,67 meter
                data['length_value'] = ent_doc[0].text
                data['measurement'] = ent_doc[1].text

                if not result_length_dictionary:
                    result_length_dictionary = data

            elif ent_label == "LENGTH_PREFIX":
                # Example: mindre enn
                data['length_prefix'] = ent_doc.text

                if not result_length_prefix_dictionary:
                    result_length_prefix_dictionary = data

        merged_line_result = result_length_dictionary | result_length_prefix_dictionary

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

        forward_result.append(merged_line_result)

    return forward_result