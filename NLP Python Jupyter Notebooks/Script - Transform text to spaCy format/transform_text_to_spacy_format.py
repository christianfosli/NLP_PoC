def text_file_to_list_of_lines(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        return f.readlines()

def transform_list_of_lines_to_spacy_ner_list(list_of_lines):

    last_line_index_number = len(list_of_lines) - 1
    
    text_in_spacy_format = []

    for line_index_number, line_text in enumerate(list_of_lines):
        
        text_in_spacy_format.append({'text': line_text,'ents': [],'title': line_index_number})

    return text_in_spacy_format