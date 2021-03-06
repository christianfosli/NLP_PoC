from flask import Flask, request, jsonify
from os import environ
from transform_text_service_input_to_spacy_format import transform_chapter_from_text_service_to_spacy_format

#
# spaCy matching rule modules
#

from spacy_matching_rule_identify_vessel_length_overall_no import identify_length_overall_in_spacy_lines
from spacy_matching_rule_identify_electrical_installation_no import identify_electrical_installation_in_spacy_lines
from spacy_matching_rule_identify_build_date_no import identify_build_date_in_norwegian_spacy_lines
from spacy_matching_rule_identify_build_date_en import identify_build_date_in_english_spacy_lines
from spacy_matching_rule_identify_alternative_reference_no import identify_alternative_reference_in_norwegian_spacy_lines
from spacy_matching_rule_identify_PASSENGER_no import identify_PASSENGER_in_norwegian_spacy_lines
from spacy_matching_rule_identify_GROSS_TONNAGE_no import identify_GROSS_TONNAGE_in_norwegian_spacy_lines
from spacy_matching_rule_identify_VESSEL_no import identify_VESSEL_in_norwegian_spacy_lines
from spacy_matching_rule_identify_FLASHPOINT_no import identify_FLASHPOINT_in_norwegian_spacy_lines
from spacy_matching_rule_identify_VESSEL_TYPE_no import identify_VESSEL_TYPE_in_norwegian_spacy_lines
from spacy_matching_rule_identify_MOBILE_UNIT_no import identify_MOBILE_UNIT_in_norwegian_spacy_lines
from spacy_matching_rule_identify_CARGO_no import identify_CARGO_in_norwegian_spacy_lines
from spacy_matching_rule_identify_TRADE_AREA_no import identify_TRADE_AREA_in_norwegian_spacy_lines
from spacy_matching_rule_identify_RADIO_AREA_no import identify_RADIO_AREA_in_norwegian_spacy_lines
from spacy_matching_rule_identify_CONVERSION_no import identify_CONVERSION_in_norwegian_spacy_lines
from spacy_matching_rule_identify_PROTECTED_no import identify_PROTECTED_in_norwegian_spacy_lines
from spacy_matching_rule_identify_LOAD_INSTALLATION_no import identify_LOAD_INSTALLATION_in_norwegian_spacy_lines
from spacy_matching_rule_identify_PROPULSION_POWER_no import identify_PROPULSION_POWER_in_norwegian_spacy_lines
from spacy_matching_rule_identify_KEEL_LAID_no import identify_KEEL_LAID_in_norwegian_spacy_lines
from spacy_named_entity_recognizer import identify_named_entities_in_spacy_lines

#
# API controllers
#

from api_controller_identify_vessel_length_overall import create_api_response_for_post_identify_vessel_length_overall_in_text_service_norwegian_chapter_input
from api_controller_identify_electrical_installation_in_text_service_norwegian_chapter_input import create_api_response_for_post_identify_electrical_installation_in_text_service_norwegian_chapter_input
from api_controller_identify_build_date_in_text_service_norwegian_chapter_input import create_api_response_for_post_identify_build_date_in_text_service_norwegian_chapter_input
from api_controller_identify_build_date_in_text_service_english_chapter_input import create_api_response_for_post_identify_build_date_in_text_service_english_chapter_input
from api_controller_identify_alternative_reference_in_text_service_norwegian_chapter_input import create_api_response_for_post_identify_alternative_reference_in_text_service_norwegian_chapter_input
from api_controller_identify_PASSENGER_in_text_service_norwegian_chapter import create_api_response_for_post_identify_PASSENGER_in_text_service_norwegian_chapter
from api_controller_identify_GROSS_TONNAGE_in_text_service_norwegian_chapter import create_api_response_for_post_identify_GROSS_TONNAGE_in_text_service_norwegian_chapter
from api_controller_identify_VESSEL_in_text_service_norwegian_chapter import create_api_response_for_post_identify_VESSEL_in_text_service_norwegian_chapter
from api_controller_identify_FLASHPOINT_in_text_service_norwegian_chapter import create_api_response_for_post_identify_FLASHPOINT_in_text_service_norwegian_chapter
from api_controller_identify_VESSEL_TYPE_in_text_service_norwegian_chapter import create_api_response_for_post_identify_VESSEL_TYPE_in_text_service_norwegian_chapter
from api_controller_identify_MOBILE_UNIT_in_text_service_norwegian_chapter import create_api_response_for_post_identify_MOBILE_UNIT_in_text_service_norwegian_chapter
from api_controller_identify_CARGO_in_text_service_norwegian_chapter import create_api_response_for_post_identify_CARGO_in_text_service_norwegian_chapter
from api_controller_identify_TRADE_AREA_in_text_service_norwegian_chapter import create_api_response_for_post_identify_TRADE_AREA_in_text_service_norwegian_chapter
from api_controller_identify_RADIO_AREA_in_text_service_norwegian_chapter import create_api_response_for_post_identify_RADIO_AREA_in_text_service_norwegian_chapter
from api_controller_identify_CONVERSION_in_text_service_norwegian_chapter import create_api_response_for_post_identify_CONVERSION_in_text_service_norwegian_chapter
from api_controller_identify_PROTECTED_in_text_service_norwegian_chapter import create_api_response_for_post_identify_PROTECTED_in_text_service_norwegian_chapter
from api_controller_identify_LOAD_INSTALLATION_in_text_service_norwegian_chapter import create_api_response_for_post_identify_LOAD_INSTALLATION_in_text_service_norwegian_chapter
from api_controller_identify_PROPULSION_POWER_in_text_service_norwegian_chapter import create_api_response_for_post_identify_PROPULSION_POWER_in_text_service_norwegian_chapter
from api_controller_identify_KEEL_LAID_in_text_service_norwegian_chapter import create_api_response_for_post_identify_KEEL_LAID_in_text_service_norwegian_chapter

#entity classifier
from api_controller_identify_language_in_request import identify_request_language
from api_controller_classify_named_entities import create_api_response_for_post_identify_named_entities

#
# Other helpers
#

from create_title_dictionary_from_text_service_input import fetch_titles_from_text_service_input

#
# API
#

app = Flask(__name__)

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1130954877/healthz
@app.route('/healthz')
def health():
    return "OK"

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1382907905/NLP+rule-based+matching+options
@app.route("/nlp-rule-based-matching-options", methods=["GET"])
def get_nlp_rule_based_matching_options():
    options = [
        {
            "title": "vessel length overall",
            "language": "no",
            "url": "identify-VESSEL-LENGTH-OVERALL-in-text-service-norwegian-chapter"
        },
        {
            "title": "electrical installation",
            "language": "no",
            "url": "identify-ELECTRICAL-INSTALLATION-in-text-service-norwegian-chapter"
        },
        {
            "title": "build date",
            "language": "no",
            "url": "identify-BUILD-DATE-in-text-service-norwegian-chapter"
        },
        {
            "title": "build date",
            "language": "en",
            "url": "identify-BUILD-DATE-in-text-service-english-chapter"
        },
        {
            "title": "alternative reference",
            "language": "no",
            "url": "identify-ALTERNATIVE-REFERENCE-in-text-service-norwegian-chapter"
        },
        {
            "title": "passenger",
            "language": "no",
            "url": "identify-PASSENGER-in-text-service-norwegian-chapter"
        },
        {
            "title": "gross tonnage",
            "language": "no",
            "url": "identify-GROSS-TONNAGE-in-text-service-norwegian-chapter"
        },
        {
            "title": "vessel",
            "language": "no",
            "url": "identify-VESSEL-in-text-service-norwegian-chapter"
        },
        {
            "title": "flashpoint",
            "language": "no",
            "url": "identify-FLASHPOINT-in-text-service-norwegian-chapter"
        },
        {
            "title": "vessel type",
            "language": "no",
            "url": "identify-VESSEL-TYPE-in-text-service-norwegian-chapter"
        },
        {
            "title": "mobile unit",
            "language": "no",
            "url": "identify-MOBILE-UNIT-in-text-service-norwegian-chapter"
        },
        {
            "title": "cargo",
            "language": "no",
            "url": "identify-CARGO-in-text-service-norwegian-chapter"
        },
        {
            "title": "trade area",
            "language": "no",
            "url": "identify-TRADE-AREA-in-text-service-norwegian-chapter"
        },
        {
            "title": "radio area",
            "language": "no",
            "url": "identify-RADIO-AREA-in-text-service-norwegian-chapter"
        },
        {
            "title": "conversion",
            "language": "no",
            "url": "identify-CONVERSION-in-text-service-norwegian-chapter"
        },
        {
            "title": "protected",
            "language": "no",
            "url": "identify-PROTECTED-in-text-service-norwegian-chapter"
        },
        {
            "title": "load installation",
            "language": "no",
            "url": "identify-LOAD-INSTALLATION-in-text-service-norwegian-chapter"
        },
        {
            "title": "propulsion power",
            "language": "no",
            "url": "identify-PROPULSION-POWER-in-text-service-norwegian-chapter"
        },
        {
            "title": "keel laid",
            "language": "no",
            "url": "identify-KEEL-LAID-in-text-service-norwegian-chapter"
        }
    ]
    return jsonify(options)

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1172897823/vessel+length+overall+-+no
@app.route("/identify-VESSEL-LENGTH-OVERALL-in-text-service-norwegian-chapter", methods=["POST"])
def post_identify_vessel_length_overall_in_text_service_norwegian_chapter_input():
    input_chapter_text_as_json_in_text_service_format = request.json
    title_dictionary = fetch_titles_from_text_service_input(input_chapter_text_as_json_in_text_service_format['title'],input_chapter_text_as_json_in_text_service_format['sections'])
    forward_text_transformed_to_spacy_format = transform_chapter_from_text_service_to_spacy_format(input_chapter_text_as_json_in_text_service_format)
    forward_result_with_length_overall = identify_length_overall_in_spacy_lines(forward_text_transformed_to_spacy_format)
    forward_filtered_result_with_only_the_things_we_are_looking_for = [spacy_line for spacy_line in forward_result_with_length_overall if any("LENGTH" == x['label'] for x in spacy_line['ents']) and any("WATER_VESSEL" == x['label'] for x in spacy_line['ents'])]
    forward_api_response = create_api_response_for_post_identify_vessel_length_overall_in_text_service_norwegian_chapter_input(title_dictionary,forward_filtered_result_with_only_the_things_we_are_looking_for)
    return jsonify({"identified_VESSEL_LENGTH_OVERALL": forward_api_response})

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1210286081/electrical+installation+-+no
@app.route("/identify-ELECTRICAL-INSTALLATION-in-text-service-norwegian-chapter", methods=["POST"])
def post_identify_electrical_installation_in_text_service_norwegian_chapter_input():
    input_chapter_text_as_json_in_text_service_format = request.json
    title_dictionary = fetch_titles_from_text_service_input(input_chapter_text_as_json_in_text_service_format['title'],input_chapter_text_as_json_in_text_service_format['sections'])
    forward_text_transformed_to_spacy_format = transform_chapter_from_text_service_to_spacy_format(input_chapter_text_as_json_in_text_service_format)
    forward_result_with_electrical_installation = identify_electrical_installation_in_spacy_lines(forward_text_transformed_to_spacy_format)
    forward_filtered_result_with_only_the_things_we_are_looking_for = [spacy_line for spacy_line in forward_result_with_electrical_installation if any("VOLTAGE" == x['label'] for x in spacy_line['ents']) and any("WATER_VESSEL" == x['label'] for x in spacy_line['ents']) and any("ELECTRICAL_INSTALLATION" == x['label'] for x in spacy_line['ents'])]
    forward_api_response = create_api_response_for_post_identify_electrical_installation_in_text_service_norwegian_chapter_input(title_dictionary,forward_filtered_result_with_only_the_things_we_are_looking_for)
    return jsonify({"identified_ELECTRICAL_INSTALLATION": forward_api_response})

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1225359396/build+date+-+no
@app.route("/identify-BUILD-DATE-in-text-service-norwegian-chapter", methods=["POST"])
def post_identify_build_date_in_text_service_norwegian_chapter_input():
    input_chapter_text_as_json_in_text_service_format = request.json
    title_dictionary = fetch_titles_from_text_service_input(input_chapter_text_as_json_in_text_service_format['title'],input_chapter_text_as_json_in_text_service_format['sections'])
    forward_text_transformed_to_spacy_format = transform_chapter_from_text_service_to_spacy_format(input_chapter_text_as_json_in_text_service_format)
    forward_result_with_date_in_norwegian = identify_build_date_in_norwegian_spacy_lines(forward_text_transformed_to_spacy_format)
    forward_filtered_result_with_only_the_things_we_are_looking_for = [spacy_line for spacy_line in forward_result_with_date_in_norwegian if any("WATER_VESSEL" == x['label'] for x in spacy_line['ents']) and any("CONSTRUCT" == x['label'] for x in spacy_line['ents']) and any("DATE" == x['label'] for x in spacy_line['ents'])]
    forward_api_response = create_api_response_for_post_identify_build_date_in_text_service_norwegian_chapter_input(title_dictionary,forward_filtered_result_with_only_the_things_we_are_looking_for)
    return jsonify({"identified_BUILD_DATE": forward_api_response})

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1233223681/build+date+-+en
@app.route("/identify-BUILD-DATE-in-text-service-english-chapter", methods=["POST"])
def post_identify_build_date_in_text_service_english_chapter_input():
    input_chapter_text_as_json_in_text_service_format = request.json
    title_dictionary = fetch_titles_from_text_service_input(input_chapter_text_as_json_in_text_service_format['title'],input_chapter_text_as_json_in_text_service_format['sections'])
    forward_text_transformed_to_spacy_format = transform_chapter_from_text_service_to_spacy_format(input_chapter_text_as_json_in_text_service_format)
    forward_result_with_date_in_english = identify_build_date_in_english_spacy_lines(forward_text_transformed_to_spacy_format)
    forward_filtered_result_with_only_the_things_we_are_looking_for = [spacy_line for spacy_line in forward_result_with_date_in_english if any("WATER_VESSEL" == x['label'] for x in spacy_line['ents']) and any("CONSTRUCT" == x['label'] for x in spacy_line['ents']) and any("DATE" == x['label'] for x in spacy_line['ents'])]
    forward_api_response = create_api_response_for_post_identify_build_date_in_text_service_english_chapter_input(title_dictionary,forward_filtered_result_with_only_the_things_we_are_looking_for)
    return jsonify({"identified_BUILD_DATE": forward_api_response})

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1264615425/alternative+reference+-+no
@app.route("/identify-ALTERNATIVE-REFERENCE-in-text-service-norwegian-chapter", methods=["POST"])
def post_identify_alternative_reference_in_text_service_norwegian_chapter_input():
    input_chapter_text_as_json_in_text_service_format = request.json
    title_dictionary = fetch_titles_from_text_service_input(input_chapter_text_as_json_in_text_service_format['title'],input_chapter_text_as_json_in_text_service_format['sections'])
    forward_text_transformed_to_spacy_format = transform_chapter_from_text_service_to_spacy_format(input_chapter_text_as_json_in_text_service_format)
    forward_result_with_date_in_norwegian = identify_alternative_reference_in_norwegian_spacy_lines(forward_text_transformed_to_spacy_format)
    forward_filtered_result_with_only_the_things_we_are_looking_for = [spacy_line for spacy_line in forward_result_with_date_in_norwegian if any("ALTERNATIVE_REFERENCE" == x['label'] for x in spacy_line['ents'])]
    forward_api_response = create_api_response_for_post_identify_alternative_reference_in_text_service_norwegian_chapter_input(title_dictionary,forward_filtered_result_with_only_the_things_we_are_looking_for)
    return jsonify({"identified_ALTERNATIVE_REFERENCE": forward_api_response})

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1275494401/passenger+-+no
@app.route("/identify-PASSENGER-in-text-service-norwegian-chapter", methods=["POST"])
def post_identify_PASSENGER_in_text_service_norwegian_chapter():
    input_chapter_text_as_json_in_text_service_format = request.json
    title_dictionary = fetch_titles_from_text_service_input(input_chapter_text_as_json_in_text_service_format['title'],input_chapter_text_as_json_in_text_service_format['sections'])
    forward_text_transformed_to_spacy_format = transform_chapter_from_text_service_to_spacy_format(input_chapter_text_as_json_in_text_service_format)
    forward_result_with_date_in_norwegian = identify_PASSENGER_in_norwegian_spacy_lines(forward_text_transformed_to_spacy_format)
    forward_filtered_result_with_only_the_things_we_are_looking_for = [spacy_line for spacy_line in forward_result_with_date_in_norwegian if any("PASSENGER" == x['label'] for x in spacy_line['ents'])]
    forward_api_response = create_api_response_for_post_identify_PASSENGER_in_text_service_norwegian_chapter(title_dictionary,forward_filtered_result_with_only_the_things_we_are_looking_for)
    return jsonify({"identified_PASSENGER": forward_api_response})

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1279164417/gross+tonnage+-+no
@app.route("/identify-GROSS-TONNAGE-in-text-service-norwegian-chapter", methods=["POST"])
def post_identify_GROSS_TONNAGE_in_text_service_norwegian_chapter():
    input_chapter_text_as_json_in_text_service_format = request.json
    title_dictionary = fetch_titles_from_text_service_input(input_chapter_text_as_json_in_text_service_format['title'],input_chapter_text_as_json_in_text_service_format['sections'])
    forward_text_transformed_to_spacy_format = transform_chapter_from_text_service_to_spacy_format(input_chapter_text_as_json_in_text_service_format)
    forward_result_with_date_in_norwegian = identify_GROSS_TONNAGE_in_norwegian_spacy_lines(forward_text_transformed_to_spacy_format)
    forward_filtered_result_with_only_the_things_we_are_looking_for = [spacy_line for spacy_line in forward_result_with_date_in_norwegian if any("GROSS_TONNAGE" == x['label'] for x in spacy_line['ents'])]
    forward_api_response = create_api_response_for_post_identify_GROSS_TONNAGE_in_text_service_norwegian_chapter(title_dictionary,forward_filtered_result_with_only_the_things_we_are_looking_for)
    return jsonify({"identified_GROSS_TONNAGE": forward_api_response})

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1279197195/vessel+-+no
@app.route("/identify-VESSEL-in-text-service-norwegian-chapter", methods=["POST"])
def post_identify_VESSEL_in_text_service_norwegian_chapter():
    input_chapter_text_as_json_in_text_service_format = request.json
    title_dictionary = fetch_titles_from_text_service_input(input_chapter_text_as_json_in_text_service_format['title'],input_chapter_text_as_json_in_text_service_format['sections'])
    forward_text_transformed_to_spacy_format = transform_chapter_from_text_service_to_spacy_format(input_chapter_text_as_json_in_text_service_format)
    forward_result_with_date_in_norwegian = identify_VESSEL_in_norwegian_spacy_lines(forward_text_transformed_to_spacy_format)
    forward_filtered_result_with_only_the_things_we_are_looking_for = [spacy_line for spacy_line in forward_result_with_date_in_norwegian if any("VESSEL" == x['label'] for x in spacy_line['ents'])]
    forward_api_response = create_api_response_for_post_identify_VESSEL_in_text_service_norwegian_chapter(title_dictionary,forward_filtered_result_with_only_the_things_we_are_looking_for)
    return jsonify({"identified_VESSEL": forward_api_response})

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1282113555/flashpoint+-+no
@app.route("/identify-FLASHPOINT-in-text-service-norwegian-chapter", methods=["POST"])
def post_identify_FLASHPOINT_in_text_service_norwegian_chapter():
    input_chapter_text_as_json_in_text_service_format = request.json
    title_dictionary = fetch_titles_from_text_service_input(input_chapter_text_as_json_in_text_service_format['title'],input_chapter_text_as_json_in_text_service_format['sections'])
    forward_text_transformed_to_spacy_format = transform_chapter_from_text_service_to_spacy_format(input_chapter_text_as_json_in_text_service_format)
    forward_result_with_date_in_norwegian = identify_FLASHPOINT_in_norwegian_spacy_lines(forward_text_transformed_to_spacy_format)
    forward_filtered_result_with_only_the_things_we_are_looking_for = [spacy_line for spacy_line in forward_result_with_date_in_norwegian if any("FLASHPOINT" == x['label'] for x in spacy_line['ents'])]
    forward_api_response = create_api_response_for_post_identify_FLASHPOINT_in_text_service_norwegian_chapter(title_dictionary,forward_filtered_result_with_only_the_things_we_are_looking_for)
    return jsonify({"identified_FLASHPOINT": forward_api_response})

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1286078537/vessel+type+-+no
@app.route("/identify-VESSEL-TYPE-in-text-service-norwegian-chapter", methods=["POST"])
def post_identify_VESSEL_TYPE_in_text_service_norwegian_chapter():
    input_chapter_text_as_json_in_text_service_format = request.json
    title_dictionary = fetch_titles_from_text_service_input(input_chapter_text_as_json_in_text_service_format['title'],input_chapter_text_as_json_in_text_service_format['sections'])
    forward_text_transformed_to_spacy_format = transform_chapter_from_text_service_to_spacy_format(input_chapter_text_as_json_in_text_service_format)
    forward_result_with_date_in_norwegian = identify_VESSEL_TYPE_in_norwegian_spacy_lines(forward_text_transformed_to_spacy_format)
    forward_filtered_result_with_only_the_things_we_are_looking_for = [spacy_line for spacy_line in forward_result_with_date_in_norwegian if any("VESSEL_TYPE" == x['label'] for x in spacy_line['ents'])]
    forward_api_response = create_api_response_for_post_identify_VESSEL_TYPE_in_text_service_norwegian_chapter(title_dictionary,forward_filtered_result_with_only_the_things_we_are_looking_for)
    return jsonify({"identified_VESSEL_TYPE": forward_api_response})

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1290469377/mobile+unit+-+no
@app.route("/identify-MOBILE-UNIT-in-text-service-norwegian-chapter", methods=["POST"])
def post_identify_MOBILE_UNIT_in_text_service_norwegian_chapter():
    input_chapter_text_as_json_in_text_service_format = request.json
    title_dictionary = fetch_titles_from_text_service_input(input_chapter_text_as_json_in_text_service_format['title'],input_chapter_text_as_json_in_text_service_format['sections'])
    forward_text_transformed_to_spacy_format = transform_chapter_from_text_service_to_spacy_format(input_chapter_text_as_json_in_text_service_format)
    forward_result_with_date_in_norwegian = identify_MOBILE_UNIT_in_norwegian_spacy_lines(forward_text_transformed_to_spacy_format)
    forward_filtered_result_with_only_the_things_we_are_looking_for = [spacy_line for spacy_line in forward_result_with_date_in_norwegian if any("MOBILE_UNIT" == x['label'] for x in spacy_line['ents'])]
    forward_api_response = create_api_response_for_post_identify_MOBILE_UNIT_in_text_service_norwegian_chapter(title_dictionary,forward_filtered_result_with_only_the_things_we_are_looking_for)
    return jsonify({"identified_MOBILE_UNIT": forward_api_response})

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1290108951/cargo+-+no
@app.route("/identify-CARGO-in-text-service-norwegian-chapter", methods=["POST"])
def post_identify_CARGO_in_text_service_norwegian_chapter():
    input_chapter_text_as_json_in_text_service_format = request.json
    title_dictionary = fetch_titles_from_text_service_input(input_chapter_text_as_json_in_text_service_format['title'],input_chapter_text_as_json_in_text_service_format['sections'])
    forward_text_transformed_to_spacy_format = transform_chapter_from_text_service_to_spacy_format(input_chapter_text_as_json_in_text_service_format)
    forward_result_with_date_in_norwegian = identify_CARGO_in_norwegian_spacy_lines(forward_text_transformed_to_spacy_format)
    forward_filtered_result_with_only_the_things_we_are_looking_for = [spacy_line for spacy_line in forward_result_with_date_in_norwegian if any("CARGO" == x['label'] for x in spacy_line['ents'])]
    forward_api_response = create_api_response_for_post_identify_CARGO_in_text_service_norwegian_chapter(title_dictionary,forward_filtered_result_with_only_the_things_we_are_looking_for)
    return jsonify({"identified_CARGO": forward_api_response})

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1290600456/trade+area+-+no
@app.route("/identify-TRADE-AREA-in-text-service-norwegian-chapter", methods=["POST"])
def post_identify_TRADE_AREA_in_text_service_norwegian_chapter():
    input_chapter_text_as_json_in_text_service_format = request.json
    title_dictionary = fetch_titles_from_text_service_input(input_chapter_text_as_json_in_text_service_format['title'],input_chapter_text_as_json_in_text_service_format['sections'])
    forward_text_transformed_to_spacy_format = transform_chapter_from_text_service_to_spacy_format(input_chapter_text_as_json_in_text_service_format)
    forward_result_with_date_in_norwegian = identify_TRADE_AREA_in_norwegian_spacy_lines(forward_text_transformed_to_spacy_format)
    forward_filtered_result_with_only_the_things_we_are_looking_for = [spacy_line for spacy_line in forward_result_with_date_in_norwegian if any("TRADE_AREA" == x['label'] for x in spacy_line['ents'])]
    forward_api_response = create_api_response_for_post_identify_TRADE_AREA_in_text_service_norwegian_chapter(title_dictionary,forward_filtered_result_with_only_the_things_we_are_looking_for)
    return jsonify({"identified_TRADE_AREA": forward_api_response})

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1288536162/radio+area+-+no
@app.route("/identify-RADIO-AREA-in-text-service-norwegian-chapter", methods=["POST"])
def post_identify_RADIO_AREA_in_text_service_norwegian_chapter():
    input_chapter_text_as_json_in_text_service_format = request.json
    title_dictionary = fetch_titles_from_text_service_input(input_chapter_text_as_json_in_text_service_format['title'],input_chapter_text_as_json_in_text_service_format['sections'])
    forward_text_transformed_to_spacy_format = transform_chapter_from_text_service_to_spacy_format(input_chapter_text_as_json_in_text_service_format)
    forward_result_with_date_in_norwegian = identify_RADIO_AREA_in_norwegian_spacy_lines(forward_text_transformed_to_spacy_format)
    forward_filtered_result_with_only_the_things_we_are_looking_for = [spacy_line for spacy_line in forward_result_with_date_in_norwegian if any("RADIO_AREA" == x['label'] for x in spacy_line['ents']) and any("RADIO_AREA_TYPE" == x['label'] for x in spacy_line['ents'])]
    forward_api_response = create_api_response_for_post_identify_RADIO_AREA_in_text_service_norwegian_chapter(title_dictionary,forward_filtered_result_with_only_the_things_we_are_looking_for)
    return jsonify({"identified_RADIO_AREA": forward_api_response})

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1300561935/conversion+-+no
@app.route("/identify-CONVERSION-in-text-service-norwegian-chapter", methods=["POST"])
def post_identify_CONVERSION_in_text_service_norwegian_chapter():
    input_chapter_text_as_json_in_text_service_format = request.json
    title_dictionary = fetch_titles_from_text_service_input(input_chapter_text_as_json_in_text_service_format['title'],input_chapter_text_as_json_in_text_service_format['sections'])
    forward_text_transformed_to_spacy_format = transform_chapter_from_text_service_to_spacy_format(input_chapter_text_as_json_in_text_service_format)
    forward_result_with_date_in_norwegian = identify_CONVERSION_in_norwegian_spacy_lines(forward_text_transformed_to_spacy_format)
    forward_filtered_result_with_only_the_things_we_are_looking_for = [spacy_line for spacy_line in forward_result_with_date_in_norwegian if any("CONVERSION" == x['label'] for x in spacy_line['ents'])]
    forward_api_response = create_api_response_for_post_identify_CONVERSION_in_text_service_norwegian_chapter(title_dictionary,forward_filtered_result_with_only_the_things_we_are_looking_for)
    return jsonify({"identified_CONVERSION": forward_api_response})

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1300922388/protected+-+no
@app.route("/identify-PROTECTED-in-text-service-norwegian-chapter", methods=["POST"])
def post_identify_PROTECTED_in_text_service_norwegian_chapter():
    input_chapter_text_as_json_in_text_service_format = request.json
    title_dictionary = fetch_titles_from_text_service_input(input_chapter_text_as_json_in_text_service_format['title'],input_chapter_text_as_json_in_text_service_format['sections'])
    forward_text_transformed_to_spacy_format = transform_chapter_from_text_service_to_spacy_format(input_chapter_text_as_json_in_text_service_format)
    forward_result_with_date_in_norwegian = identify_PROTECTED_in_norwegian_spacy_lines(forward_text_transformed_to_spacy_format)
    forward_filtered_result_with_only_the_things_we_are_looking_for = [spacy_line for spacy_line in forward_result_with_date_in_norwegian if any("PROTECTED" == x['label'] for x in spacy_line['ents'])]
    forward_api_response = create_api_response_for_post_identify_PROTECTED_in_text_service_norwegian_chapter(title_dictionary,forward_filtered_result_with_only_the_things_we_are_looking_for)
    return jsonify({"identified_PROTECTED": forward_api_response})

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1305083905/load+installation+-+no
@app.route("/identify-LOAD-INSTALLATION-in-text-service-norwegian-chapter", methods=["POST"])
def post_identify_LOAD_INSTALLATION_in_text_service_norwegian_chapter():
    input_chapter_text_as_json_in_text_service_format = request.json
    title_dictionary = fetch_titles_from_text_service_input(input_chapter_text_as_json_in_text_service_format['title'],input_chapter_text_as_json_in_text_service_format['sections'])
    forward_text_transformed_to_spacy_format = transform_chapter_from_text_service_to_spacy_format(input_chapter_text_as_json_in_text_service_format)
    forward_result_with_date_in_norwegian = identify_LOAD_INSTALLATION_in_norwegian_spacy_lines(forward_text_transformed_to_spacy_format)
    forward_filtered_result_with_only_the_things_we_are_looking_for = [spacy_line for spacy_line in forward_result_with_date_in_norwegian if any("LOAD_INSTALLATION" == x['label'] for x in spacy_line['ents'])]
    forward_api_response = create_api_response_for_post_identify_LOAD_INSTALLATION_in_text_service_norwegian_chapter(title_dictionary,forward_filtered_result_with_only_the_things_we_are_looking_for)
    return jsonify({"identified_LOAD_INSTALLATION": forward_api_response})

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1305378821/propulsion+power+-+no
@app.route("/identify-PROPULSION-POWER-in-text-service-norwegian-chapter", methods=["POST"])
def post_identify_PROPULSION_POWER_in_text_service_norwegian_chapter():
    input_chapter_text_as_json_in_text_service_format = request.json
    title_dictionary = fetch_titles_from_text_service_input(input_chapter_text_as_json_in_text_service_format['title'],input_chapter_text_as_json_in_text_service_format['sections'])
    forward_text_transformed_to_spacy_format = transform_chapter_from_text_service_to_spacy_format(input_chapter_text_as_json_in_text_service_format)
    forward_result_with_date_in_norwegian = identify_PROPULSION_POWER_in_norwegian_spacy_lines(forward_text_transformed_to_spacy_format)
    forward_filtered_result_with_only_the_things_we_are_looking_for = [spacy_line for spacy_line in forward_result_with_date_in_norwegian if any("PROPULSION_POWER" == x['label'] for x in spacy_line['ents']) and any("PROPULSION_POWER_FACT" == x['label'] for x in spacy_line['ents'])]
    forward_api_response = create_api_response_for_post_identify_PROPULSION_POWER_in_text_service_norwegian_chapter(title_dictionary,forward_filtered_result_with_only_the_things_we_are_looking_for)
    return jsonify({"identified_PROPULSION_POWER": forward_api_response})

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1307967497/keel+laid+-+no
@app.route("/identify-KEEL-LAID-in-text-service-norwegian-chapter", methods=["POST"])
def post_identify_KEEL_LAID_in_text_service_norwegian_chapter():
    input_chapter_text_as_json_in_text_service_format = request.json
    title_dictionary = fetch_titles_from_text_service_input(input_chapter_text_as_json_in_text_service_format['title'],input_chapter_text_as_json_in_text_service_format['sections'])
    forward_text_transformed_to_spacy_format = transform_chapter_from_text_service_to_spacy_format(input_chapter_text_as_json_in_text_service_format)
    forward_result_with_date_in_norwegian = identify_KEEL_LAID_in_norwegian_spacy_lines(forward_text_transformed_to_spacy_format)
    forward_filtered_result_with_only_the_things_we_are_looking_for = [spacy_line for spacy_line in forward_result_with_date_in_norwegian if any("KEEL_LAID" == x['label'] for x in spacy_line['ents']) and any("DATE" == x['label'] for x in spacy_line['ents'])]
    forward_api_response = create_api_response_for_post_identify_KEEL_LAID_in_text_service_norwegian_chapter(title_dictionary,forward_filtered_result_with_only_the_things_we_are_looking_for)
    return jsonify({"identified_KEEL_LAID": forward_api_response})

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/
@app.route("/classify_named_entities", methods=["POST"])
# Use only underscore
def post_classify_named_entities_input():
    input_chapter_text_as_json_in_text_service_format = request.json
    language_in_input_chapter_text_as_json_in_text_service_format = identify_request_language(input_chapter_text_as_json_in_text_service_format)
    forward_text_transformed_to_spacy_format = transform_chapter_from_text_service_to_spacy_format(input_chapter_text_as_json_in_text_service_format)
    forward_result_with_entities = identify_named_entities_in_spacy_lines(forward_text_transformed_to_spacy_format, language_in_input_chapter_text_as_json_in_text_service_format)
    forward_api_response = create_api_response_for_post_identify_named_entities(forward_result_with_entities, language_in_input_chapter_text_as_json_in_text_service_format)
    return jsonify({"identified_named_entities": forward_api_response})

if __name__ == '__main__':
    #app.run()
    app.run(debug=True)
