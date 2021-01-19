from flask import Flask, request, jsonify
#from identify_date import identify_date_in_text
from identify_date import identify_date_in_spacy_lines
from identify_section_span import list_section_span_from_file_lines

app = Flask(__name__)

# Documentation: https://sdir.atlassian.net/wiki/spaces/SDIR/pages/1130954877/healthz
@app.route('/healthz')
def health():
    return "ok"

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

if __name__ == '__main__':
    #app.run()
    app.run(debug=True)
