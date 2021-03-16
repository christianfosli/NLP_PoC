def transform_chapter_from_text_service_to_spacy_format(chapter_from_text_service):
    text_in_spacy_format = []
    for section_item in chapter_from_text_service['sections']:    
        for part_item in section_item['parts']: # before: subsections
            text_in_spacy_format.append({
                'text': part_item['content'],
                'ents': [],
                'title': part_item['url']})
            for subpart_item in part_item['subparts']: # before: sentences
                text_in_spacy_format.append({
                    'text': subpart_item['content'],
                    'ents': [],
                    'title': subpart_item['url']})
    return text_in_spacy_format

# this url reader will support both absolute and relative url's.
# Example 1: http://sdir-d-aks-core.norwayeast.cloudapp.azure.com/regulation/2013/11/22/1404/chapter/3/section/24/part/2/subpart/1
# Example 2: /regulation/2013/11/22/1404/chapter/3/section/24/part/2/subpart/1
def get_data_from_text_service_item_url(url):
    
    data = {}

    url_split_in_list = url.split('/')

    for split_index_number, split in enumerate(url_split_in_list):
        if not "regulation_year" in data:
            if split == "regulation": # before: regulations
                data['regulation_year'] = url_split_in_list[split_index_number+1] # 2013
                data['regulation_month'] = url_split_in_list[split_index_number+2] # 11
                data['regulation_day'] = url_split_in_list[split_index_number+3] # 22
                data['regulation_id'] = url_split_in_list[split_index_number+4] # 1404
        elif not "chapter_number" in data:
            if split == "chapter": # before: chapters
                data['chapter_number'] = url_split_in_list[split_index_number+1] # 3
        elif not "section_number" in data:
            if split == "section": # before: sections
                data['section_number'] = url_split_in_list[split_index_number+1] # 15
        elif not "part_number" in data:
            if split == "part": # before: subsections
                data['part_number'] = url_split_in_list[split_index_number+1] # 1
        elif not "sub_part_number" in data:
            if split == "subpart": # before: sentences
                data['sub_part_number'] = url_split_in_list[split_index_number+1] # 1

    return data