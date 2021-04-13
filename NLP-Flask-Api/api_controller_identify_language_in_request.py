#will determine if the language of the text from the request is in Norwegian og English.
# Assumes one language for each request. 
import json

def identify_request_language(data):
    if data['sections'][0]['parts'][0]['language'] == 'en':
        language = 'en'
        return(language)
    #If the language is set to Nynorsk, the current solution will use the model for Bokmål.
    elif data['sections'][0]['parts'][0]['language'] == 'nn':
        language = 'nb'
    #Default language is set to Norwegian Bokmål
    else:
        language = 'nb'
        return(language)

