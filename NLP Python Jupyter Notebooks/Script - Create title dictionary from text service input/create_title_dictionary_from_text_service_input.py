def fetch_titles_from_text_service_input(chapter_title_as_string,text_service_chapter_input_sections):

    result = {}
    result["chapter_title"] = chapter_title_as_string

    section_title_in_dictionary = {}
    for section_item in text_service_chapter_input_sections:
        section_title = section_item['title']
        for subsection_item in section_item['parts']:
            section_title_in_dictionary[subsection_item['url']] = section_title
            for sentence_item in subsection_item['subparts']:
                section_title_in_dictionary[sentence_item['url']] = section_title

    result["section_title_in_dictionary"] = section_title_in_dictionary
    return result