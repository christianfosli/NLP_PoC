import nltk
from collections import defaultdict
from nltk.tokenize import word_tokenize

def get_section_from_sentence_index_number(sentence_index_number, input_identify_section_span):
    for (section_as_string,span_start,span_end) in input_identify_section_span:
        if sentence_index_number >= span_start and sentence_index_number <= span_end:
            return section_as_string
    return "none"

def is_float(n):
    try:
        float_n = float(n)
    except ValueError:
        return False
    else:
        return True

def get_sentence_type_from_sentence_index_number(sentence_index_number, forward_sentence_type_in_a_list):

    forward_sentence_type_from_chapter_text = defaultdict(int)

    for row in forward_sentence_type_in_a_list:

        line_index_number = row[0]
        sentence_type = row[1]
        type_value = row[2]

        forward_sentence_type_from_chapter_text[int(line_index_number)] = (sentence_type,type_value)


    sentence_type = forward_sentence_type_from_chapter_text[sentence_index_number][0]
    type_value = forward_sentence_type_from_chapter_text[sentence_index_number][1]

    if sentence_type == 'sub-part':

        i = sentence_index_number - 1

        while i > 0:
            parent_sentence_type = forward_sentence_type_from_chapter_text[i][0]
            parent_type_value = forward_sentence_type_from_chapter_text[i][1]

            if parent_sentence_type == 'part':
                return (parent_type_value,type_value)

            i -= 1

        return ('none',type_value)

    elif sentence_type == 'part':
        return (type_value,'none')

    else:
        return ('none','none')

def find_word(file_lines,forward_sentence_type_in_a_list,forward_identify_section_span,word):

    result_for_csv_file_before = []

    for line_index_number, line_text in enumerate(file_lines):

        word_tokens = word_tokenize(line_text, language="english")

        sentence_word_count = len(word_tokens)

        if sentence_word_count > 0:

            nltk_concordance_index = nltk.ConcordanceIndex(word_tokens)

            for offset in nltk_concordance_index.offsets(word):

                identified_sentence_part_and_sub_part_as_tuple = get_sentence_type_from_sentence_index_number(line_index_number,forward_sentence_type_in_a_list)
                part_value = identified_sentence_part_and_sub_part_as_tuple[0]
                sub_part_value = identified_sentence_part_and_sub_part_as_tuple[1]

                if is_float(word_tokens[offset + 1] and word_tokens[offset + 3]):

                    paragraph_number = get_section_from_sentence_index_number(line_index_number,forward_identify_section_span)

                    relation = word_tokens[offset]
                    value_1 = word_tokens[offset + 1] + " " + word_tokens[offset + 2] + " " + word_tokens[offset + 3]
                    
                    if word_tokens[offset + 2] == "January" or word_tokens[offset + 2] == "February" or word_tokens[offset + 2] == "March" or word_tokens[offset + 2] == "April" or word_tokens[offset + 2] == "May" or word_tokens[offset + 2] == "June" or word_tokens[offset + 2] == "July" or word_tokens[offset + 2] == "August" or word_tokens[offset + 2] == "September" or word_tokens[offset + 2] == "October" or word_tokens[offset + 2] == "November" or word_tokens[offset + 2] == "December":

                        # Remove the word "Section " for the Java application
                        only_output_section_number = paragraph_number.replace("Section ","")

                        result_for_csv_file_before.append([
                            only_output_section_number,
                            part_value,
                            sub_part_value,
                            relation,
                            value_1
                            ])
            
    return list(result_for_csv_file_before)