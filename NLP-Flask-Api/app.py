from flask import Flask
from identify_date import identify_date_in_text


app = Flask(__name__)


@app.route('/healthz')
def health():
    result_in_a_list = identify_date_in_text("(1) This chapter applies to vessels constructed after 2 January 1988.\n")
    return { "result": result_in_a_list}


if __name__ == '__main__':
    #app.run()
    app.run(debug=True)
