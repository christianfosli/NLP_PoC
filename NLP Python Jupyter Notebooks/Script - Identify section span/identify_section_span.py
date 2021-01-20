from nltk.tokenize import word_tokenize

def does_the_string_contain_any_numbers(inputString):
    return any(char.isdigit() for char in inputString)

def list_section_span_from_file_lines(forward_file_lines):

    forward_sentences_as_word_list = []

    for sentence_as_a_string in forward_file_lines:
        tokens = word_tokenize(sentence_as_a_string)
        forward_sentences_as_word_list.append(tokens)

    forward_list_with_section_span_by_sentences = []

    for sentence_index_number,sentence_as_list in enumerate(forward_sentences_as_word_list):

        if sentence_as_list != [] and sentence_as_list[0] == "Section":

            if does_the_string_contain_any_numbers(sentence_as_list[1]) and sentence_as_list[2] == ".":
                forward_list_with_section_span_by_sentences.append((sentence_index_number,int(sentence_as_list[1])))
                
    last_list_index_number = len(forward_list_with_section_span_by_sentences) -1
    forward_result_to_csv = []

    for list_index_number,(sentence_index_number,section_number) in enumerate(forward_list_with_section_span_by_sentences):
        
        span_section = str(section_number)
        span_start = sentence_index_number
        span_end = span_start

        # if this is the last element
        if list_index_number >= last_list_index_number:
            span_end = len(forward_sentences_as_word_list) - 1
        else:
            next_element_index_number = list_index_number + 1
            next_element = forward_list_with_section_span_by_sentences[next_element_index_number]
            span_end = next_element[0] - 1
            
        forward_result_to_csv.append((span_section,span_start,span_end))
        
    return forward_result_to_csv