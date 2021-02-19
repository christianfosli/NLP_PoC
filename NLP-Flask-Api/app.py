from flask import Flask, request, jsonify
from transform_text_service_input_to_spacy_format import transform_chapter_from_text_service_to_spacy_format

#
# spaCy matching rule modules
#

# vessel_length_overall
from spacy_matching_rule_identify_vessel_length_overall_no import identify_length_overall_in_spacy_lines
# electrical_installation
from spacy_matching_rule_identify_electrical_installation_no import identify_electrical_installation_in_spacy_lines
# build_date
from spacy_matching_rule_identify_build_date_no import identify_build_date_in_norwegian_spacy_lines
from spacy_matching_rule_identify_build_date_en import identify_build_date_in_english_spacy_lines
# alternative_reference
from spacy_matching_rule_identify_alternative_reference_no import identify_alternative_reference_in_norwegian_spacy_lines

#
# API controllers
#

# vessel_length_overall
from api_controller_identify_vessel_length_overall import create_api_response_for_post_identify_vessel_length_overall_in_text_service_norwegian_chapter_input
# electrical_installation
from api_controller_identify_electrical_installation_in_text_service_norwegian_chapter_input import create_api_response_for_post_identify_electrical_installation_in_text_service_norwegian_chapter_input
# build_date
from api_controller_identify_build_date_in_text_service_norwegian_chapter_input import create_api_response_for_post_identify_build_date_in_text_service_norwegian_chapter_input
from api_controller_identify_build_date_in_text_service_english_chapter_input import create_api_response_for_post_identify_build_date_in_text_service_english_chapter_input
# alternative_reference
from api_controller_identify_alternative_reference_in_text_service_norwegian_chapter_input import create_api_response_for_post_identify_alternative_reference_in_text_service_norwegian_chapter_input

app = Flask(__name__)

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1130954877/healthz
@app.route('/healthz')
def health():
    return "OK"

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

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1225359396/identify-build-date-in-text-service-norwegian-chapter-input
@app.route("/identify-build-date-in-text-service-norwegian-chapter-input", methods=["POST"])
def post_identify_build_date_in_text_service_norwegian_chapter_input():
    input_chapter_text_as_json_in_text_service_format = request.json
    forward_text_transformed_to_spacy_format = transform_chapter_from_text_service_to_spacy_format(input_chapter_text_as_json_in_text_service_format)
    forward_result_with_date_in_norwegian = identify_build_date_in_norwegian_spacy_lines(forward_text_transformed_to_spacy_format)
    forward_filtered_result_with_only_the_things_we_are_looking_for = [spacy_line for spacy_line in forward_result_with_date_in_norwegian if any("WATER_VESSEL" == x['label'] for x in spacy_line['ents']) and any("CONSTRUCT" == x['label'] for x in spacy_line['ents']) and any("DATE" == x['label'] for x in spacy_line['ents'])]
    forward_api_response = create_api_response_for_post_identify_build_date_in_text_service_norwegian_chapter_input(forward_filtered_result_with_only_the_things_we_are_looking_for)
    return jsonify({"identified_build_dates": forward_api_response})

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1233223681/identify-build-date-in-text-service-english-chapter-input
@app.route("/identify-build-date-in-text-service-english-chapter-input", methods=["POST"])
def post_identify_build_date_in_text_service_english_chapter_input():
    input_chapter_text_as_json_in_text_service_format = request.json
    forward_text_transformed_to_spacy_format = transform_chapter_from_text_service_to_spacy_format(input_chapter_text_as_json_in_text_service_format)
    forward_result_with_date_in_english = identify_build_date_in_english_spacy_lines(forward_text_transformed_to_spacy_format)
    forward_filtered_result_with_only_the_things_we_are_looking_for = [spacy_line for spacy_line in forward_result_with_date_in_english if any("WATER_VESSEL" == x['label'] for x in spacy_line['ents']) and any("CONSTRUCT" == x['label'] for x in spacy_line['ents']) and any("DATE" == x['label'] for x in spacy_line['ents'])]
    forward_api_response = create_api_response_for_post_identify_build_date_in_text_service_english_chapter_input(forward_filtered_result_with_only_the_things_we_are_looking_for)
    return jsonify({"identified_build_dates": forward_api_response})

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1264615425/identify-alternative-reference-in-text-service-norwegian-chapter-input
@app.route("/identify-alternative-reference-in-text-service-norwegian-chapter-input", methods=["POST"])
def post_identify_alternative_reference_in_text_service_norwegian_chapter_input():
    input_chapter_text_as_json_in_text_service_format = request.json
    forward_text_transformed_to_spacy_format = transform_chapter_from_text_service_to_spacy_format(input_chapter_text_as_json_in_text_service_format)
    forward_result_with_date_in_norwegian = identify_alternative_reference_in_norwegian_spacy_lines(forward_text_transformed_to_spacy_format)
    forward_filtered_result_with_only_the_things_we_are_looking_for = [spacy_line for spacy_line in forward_result_with_date_in_norwegian if any("ALTERNATIVE_REFERENCE" == x['label'] for x in spacy_line['ents'])]
    forward_api_response = create_api_response_for_post_identify_alternative_reference_in_text_service_norwegian_chapter_input(forward_filtered_result_with_only_the_things_we_are_looking_for)
    return jsonify({"identified_alternative-reference": forward_api_response})

if __name__ == '__main__':
    #app.run()
    app.run(debug=True)