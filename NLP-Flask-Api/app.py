from flask import Flask, request, jsonify
from identify_date import identify_date_in_spacy_lines
from identify_section_span import list_section_span_from_file_lines
from identify_sentence_type import list_sentence_type_from_file_lines
from identify_chapter_and_section import list_chapter_and_section_from_text_lines
from identify_build_date import find_word

app = Flask(__name__)

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1130954877/healthz
@app.route('/healthz')
def health():
    return "OK"

#
# Deactivating this because it does the same as /identify-date-in-spacy-lines
# only it only takes one string input.
# TODO: Remove later
#
#@app.route("/identify-date-in-text", methods=["POST"])
#def post_identify_date_in_text():
#    input_text = request.form["text"]
#    escaped_input_text = bytes(input_text, "utf-8").decode("unicode_escape")
#    result_in_a_list = identify_date_in_text(escaped_input_text)
#    return { "spacy-ner-tags-as-json": result_in_a_list}

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1127743570/identify-date-in-spacy-lines
@app.route("/identify-date-in-spacy-lines", methods=["POST"])
def post_identify_date_in_spacy_lines():
    input_spacy_lines_as_json = request.json
    input_spacy_lines = input_spacy_lines_as_json['spacy-lines-as-json']
    result_in_a_list = identify_date_in_spacy_lines(input_spacy_lines)
    return jsonify({"spacy-lines-as-json": result_in_a_list})

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1144291329/identify-section-span-in-chapter-text
@app.route("/identify-section-span-in-chapter-text", methods=["POST"])
def post_identify_section_span_in_chapter_text():
    input_chapter_text_as_json = request.json
    input_chapter_text_in_a_list = input_chapter_text_as_json['chapter_text_in_a_list']
    response_in_a_list = list_section_span_from_file_lines(input_chapter_text_in_a_list)
    response_converted_to_json = []
    for item in response_in_a_list:
        data = {}
        data['section_number'] = int(item[0])
        data['section_start_at_sentence'] = item[1]
        data['section_end_at_sentence'] = item[2]
        response_converted_to_json.append(data)
    return jsonify({"identified_section_span": response_converted_to_json})

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1145995269/identify-sentence-type-in-chapter-text
@app.route("/identify-sentence-type-in-chapter-text", methods=["POST"])
def post_identify_sentence_type_in_chapter_text():
    input_chapter_text_as_json = request.json
    input_chapter_text_in_a_list = input_chapter_text_as_json['chapter_text_in_a_list']
    response_in_a_list = list_sentence_type_from_file_lines(input_chapter_text_in_a_list)
    response_converted_to_json = []
    for item in response_in_a_list:
        data = {}
        data['text_line_index_number'] = int(item[0])
        data['sentence_type'] = item[1]
        data['sentence_type_value'] = item[2]
        response_converted_to_json.append(data)
    return jsonify({"identified_sentence_type": response_converted_to_json})

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1145896992/identify-chapter-and-section-in-chapter-text
@app.route("/identify-chapter-and-section-in-chapter-text", methods=["POST"])
def post_identify_chapter_and_section_in_chapter_text():
    input_chapter_text_as_json = request.json
    input_chapter_text_in_a_list = input_chapter_text_as_json['chapter_text_in_a_list']
    response_in_a_list = list_chapter_and_section_from_text_lines(input_chapter_text_in_a_list)
    response_converted_to_json = []
    for item in response_in_a_list:
        data = {}
        data['text_line_index_number'] = int(item[0])
        data['sentence_type'] = item[1]
        data['sentence_type_value'] = item[2]
        data['sentence_type_text'] = item[3]
        response_converted_to_json.append(data)
    return jsonify({"identified_sentence_type": response_converted_to_json})

# Documentation: 
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

if __name__ == '__main__':
    #app.run()
    app.run(debug=True)
