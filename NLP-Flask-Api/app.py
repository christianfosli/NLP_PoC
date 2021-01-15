from flask import Flask, request
from identify_date import identify_date_in_text


app = Flask(__name__)


@app.route('/healthz')
def health():
    return "ok"

@app.route("/identify-date-in-text", methods=["POST"])
def post_identify_date_in_text():
    input_text = request.form["text"]
    escaped_input_text = bytes(input_text, "utf-8").decode("unicode_escape")
    result_in_a_list = identify_date_in_text(escaped_input_text)
    return { "result": result_in_a_list}

if __name__ == '__main__':
    #app.run()
    app.run(debug=True)
