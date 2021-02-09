from flask import Flask, request, jsonify
from identify_section_span import list_section_span_from_file_lines
from identify_sentence_type import list_sentence_type_from_file_lines
from identify_build_date import find_word
from transform_text_service_input_to_spacy_format import transform_chapter_from_text_service_to_spacy_format

# spacy matching rule
from spacy_matching_rule_identify_vessel_length_overall_no import identify_length_overall_in_spacy_lines
from spacy_matching_rule_identify_electrical_installation_no import identify_electrical_installation_in_spacy_lines

# api controller
from api_controller_identify_vessel_length_overall import create_api_response_for_post_identify_vessel_length_overall_in_text_service_norwegian_chapter_input
from api_controller_identify_electrical_installation_in_text_service_norwegian_chapter_input import create_api_response_for_post_identify_electrical_installation_in_text_service_norwegian_chapter_input

app = Flask(__name__)

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1130954877/healthz
@app.route('/healthz')
def health():
    return "OK"

#TODO refactor this to get input from text service api
# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1151828046/identify-build-date-in-chapter-text
@app.route("/identify-build-date-in-chapter-text", methods=["POST"])
def post_identify_build_date_in_chapter_text():
    input_chapter_text_as_json = request.json
    input_chapter_text_in_a_list = input_chapter_text_as_json['chapter_text_in_a_list']

    # help from other components
    forward_identify_section_span = list_section_span_from_file_lines(input_chapter_text_in_a_list)
    forward_sentence_type_in_a_list = list_sentence_type_from_file_lines(input_chapter_text_in_a_list)

    # component identify_build_date
    find_before = find_word(input_chapter_text_in_a_list,forward_sentence_type_in_a_list,forward_identify_section_span,"before")
    find_after = find_word(input_chapter_text_in_a_list,forward_sentence_type_in_a_list,forward_identify_section_span,"after")

    response_converted_to_json = []

    for result_item in find_before:
        data = {}
        data['section_number'] = result_item[0]
        data['part_value'] = result_item[1]
        data['sub_part_value'] = result_item[2]
        data['relation_term'] = result_item[3]
        data['date'] = result_item[4]
        response_converted_to_json.append(data)

    for result_item in find_after:
        data = {}
        data['section_number'] = result_item[0]
        data['part_value'] = result_item[1]
        data['sub_part_value'] = result_item[2]
        data['relation_term'] = result_item[3]
        data['date'] = result_item[4]
        response_converted_to_json.append(data)

    return jsonify({"identified_build_date": response_converted_to_json})

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1172897823/identify-vessel-length-overall-in-text-service-norwegian-chapter-input
@app.route("/identify-vessel-length-overall-in-text-service-norwegian-chapter-input", methods=["POST"])
def post_identify_vessel_length_overall_in_text_service_norwegian_chapter_input():
    input_chapter_text_as_json_in_text_service_format = request.json
    forward_text_transformed_to_spacy_format = transform_chapter_from_text_service_to_spacy_format(input_chapter_text_as_json_in_text_service_format)
    forward_result_with_length_overall = identify_length_overall_in_spacy_lines(forward_text_transformed_to_spacy_format)
    forward_filtered_result_with_only_the_things_we_are_looking_for = [spacy_line for spacy_line in forward_result_with_length_overall if any("LENGTH" == x['label'] for x in spacy_line['ents']) and any("WATER_VESSEL" == x['label'] for x in spacy_line['ents'])]
    forward_api_response = create_api_response_for_post_identify_vessel_length_overall_in_text_service_norwegian_chapter_input(forward_filtered_result_with_only_the_things_we_are_looking_for)
    return jsonify({"identified_vessel_length_overall": forward_api_response})

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1210286081/identify-electrical-installation-in-text-service-norwegian-chapter-input
@app.route("/identify-electrical-installation-in-text-service-norwegian-chapter-input", methods=["POST"])
def post_identify_electrical_installation_in_text_service_norwegian_chapter_input():
    input_chapter_text_as_json_in_text_service_format = request.json
    forward_text_transformed_to_spacy_format = transform_chapter_from_text_service_to_spacy_format(input_chapter_text_as_json_in_text_service_format)
    forward_result_with_electrical_installation = identify_electrical_installation_in_spacy_lines(forward_text_transformed_to_spacy_format)
    forward_filtered_result_with_only_the_things_we_are_looking_for = [spacy_line for spacy_line in forward_result_with_electrical_installation if any("VOLTAGE" == x['label'] for x in spacy_line['ents']) and any("WATER_VESSEL" == x['label'] for x in spacy_line['ents']) and any("ELECTRICAL_INSTALLATION" == x['label'] for x in spacy_line['ents'])]
    forward_api_response = create_api_response_for_post_identify_electrical_installation_in_text_service_norwegian_chapter_input(forward_filtered_result_with_only_the_things_we_are_looking_for)
    return jsonify({"identified_electrical_installations": forward_api_response})

if __name__ == '__main__':
    #app.run()
    app.run(debug=True)