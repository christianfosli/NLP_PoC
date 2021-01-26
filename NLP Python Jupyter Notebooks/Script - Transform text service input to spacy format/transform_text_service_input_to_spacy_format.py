def transform_chapter_from_text_service_to_spacy_format(chapter_from_text_service):
    text_in_spacy_format = []
    for section_item in chapter_from_text_service['sections']:    
        for subsection_item in section_item['subsections']:
            for sentence_item in subsection_item['sentences']:
                text_in_spacy_format.append({
                    'text': sentence_item['content'],
                    'ents': [],
                    'title': sentence_item['url']})
    return text_in_spacy_format

def get_data_from_text_service_item_url(url):
    
    url_split_in_list = url.split('/')
    url_split_length = len(url_split_in_list) - 1

    data = {}

    if url_split_length < 3:
        return data

    data['api_domain'] = url_split_in_list[2] # sdir-d-aks-core.norwayeast.cloudapp.azure.com
    data['api_version'] = url_split_in_list[3] # v1

    if url_split_length < 8:
        return data

    data['regularion_variable_name'] = url_split_in_list[4] # regulations
    data['regularion_year'] = url_split_in_list[5] # 2013
    data['regularion_month'] = url_split_in_list[6] # 11
    data['regularion_day'] = url_split_in_list[7] # 22
    data['regularion_id'] = url_split_in_list[8] # 1404

    if url_split_length < 10:
        return data

    data['chapter_variable_name'] = url_split_in_list[9] # chapters
    data['chapter_number'] = url_split_in_list[10] # 3

    if url_split_length < 12:
        return data

    data['section_variable_name'] = url_split_in_list[11] # sections
    data['section_number'] = url_split_in_list[12] # 15

    if url_split_length < 14:
        return data

    data['part_variable_name'] = url_split_in_list[13] # subsections
    data['part_number'] = url_split_in_list[14] # 1

    if url_split_length < 16:
        return data

    data['sub_part_variable_name'] = url_split_in_list[15] # sentences
    data['sub_part_number'] = url_split_in_list[16] # 2

    return data