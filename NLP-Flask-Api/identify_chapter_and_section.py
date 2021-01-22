from nltk.tokenize import word_tokenize

def does_the_string_contain_any_numbers(inputString):
    return any(char.isdigit() for char in inputString)

def list_chapter_and_section_from_text_lines(file_lines):

    forward_result = []

    for line_index_number, line_text in enumerate(file_lines):

        word_tokens = word_tokenize(line_text, language="english")

        sentence_word_count = len(word_tokens)

        if sentence_word_count > 3 and word_tokens[0] == 'Section' and does_the_string_contain_any_numbers(word_tokens[1]) and word_tokens[2] == '.':
            sentence_type = 'headline-section'
            type_value = word_tokens[1] # section number
            headline_text_as_list = word_tokens[3:sentence_word_count]
            headline_text = " ".join(headline_text_as_list)
            forward_result.append((str(line_index_number),sentence_type,type_value,headline_text))

        elif sentence_word_count > 2 and line_index_number == 0 and does_the_string_contain_any_numbers(word_tokens[0]):
            sentence_type = 'headline-chapter'
            type_value = word_tokens[0] # chapter number
            headline_text_as_list = word_tokens[2:sentence_word_count]
            headline_text = " ".join(headline_text_as_list)
            forward_result.append((str(line_index_number),sentence_type,type_value,headline_text))

    return forward_result