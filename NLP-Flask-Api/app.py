from flask import Flask, request, jsonify
#from identify_date import identify_date_in_text
from identify_date import identify_date_in_spacy_lines

app = Flask(__name__)

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

@app.route("/identify-date-in-spacy-lines", methods=["POST"])
def post_identify_date_in_spacy_lines():
    input_spacy_lines_as_json = request.json
    input_spacy_lines = input_spacy_lines_as_json['spacy-lines-as-json']
    result_in_a_list = identify_date_in_spacy_lines(input_spacy_lines)
    return jsonify({"spacy-lines-as-json": result_in_a_list})

if __name__ == '__main__':
    #app.run()
    app.run(debug=True)
