FROM python:3.9-slim-buster

WORKDIR /usr/src/app

COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

RUN python -m nltk.downloader punkt
# RUN python -m nltk.downloader wordnet

COPY . .
EXPOSE 5000
CMD ["/usr/local/bin/flask", "run", "--host=0.0.0.0"]
