from nltk.tokenize import word_tokenize

def does_the_string_contain_any_numbers(inputString):
    return any(char.isdigit() for char in inputString)

def list_sentence_type_from_file_lines(file_lines):

    forward_result = []

    for line_index_number, line_text in enumerate(file_lines):

        word_tokens = word_tokenize(line_text, language="english")

        sentence_word_count = len(word_tokens)

        if sentence_word_count > 3 and word_tokens[0] == '(' and does_the_string_contain_any_numbers(word_tokens[1]) and word_tokens[2] == ')':
            sentence_type = 'part'
            type_value = word_tokens[1] # part number
            forward_result.append((str(line_index_number),sentence_type,type_value))

        elif sentence_word_count > 3 and word_tokens[0] == 'Section' and does_the_string_contain_any_numbers(word_tokens[1]) and word_tokens[2] == '.':
            sentence_type = 'headline-section'
            type_value = word_tokens[1] # section number
            forward_result.append((str(line_index_number),sentence_type,type_value))

        elif sentence_word_count > 2 and word_tokens[1] == ')':
            sentence_type = 'sub-part'
            type_value = word_tokens[0] # sub-part character
            forward_result.append((str(line_index_number),sentence_type,type_value))

        elif sentence_word_count > 2 and line_index_number == 0 and does_the_string_contain_any_numbers(word_tokens[0]):
            sentence_type = 'headline-chapter'
            type_value = word_tokens[0] # chapter number
            forward_result.append((str(line_index_number),sentence_type,type_value))

        else:
            sentence_type = 'none'
            type_value = 'none'
            forward_result.append((str(line_index_number),sentence_type,type_value))

    return forward_result