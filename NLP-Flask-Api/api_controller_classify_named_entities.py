import spacy
from pathlib import Path
from google_trans_new import google_translator  
from translate_norwegian_entities import translate_norwegian_entity, return_english_label
from transform_text_service_input_to_spacy_format import get_data_from_text_service_item_url
def create_api_response_for_post_identify_named_entities(forward_filtered_result, language):
    forward_result = []
    if language == 'en':
        for part in forward_filtered_result:
                for line in part:
                    ents = line['ents']
                    if ents:
                        for ent in ents:
                            entity = ent['ent_text']
                            label = ent['label']
                            forward_result.append({'class label en': label, 'entity en': entity})
    else:
        for part in forward_filtered_result:
            for line in part:
                ents = line['ents']
                if ents:
                    for ent in ents:
                        entity = ent['ent_text']
                        label = ent['label']
                        entity_eng = translate_norwegian_entity(entity)
                        label_eng = return_english_label(label)
                        forward_result.append({'class label no': label_eng, 'class label en': label, 'entity no': entity, 'entity en': entity_eng})
        

    return forward_result


'''
def translate_norwegian_entity(norwegian_entity):
    from google_trans_new import google_translator  
    translator = google_translator()  
    try:
        entity = translator.translate(norwegian_entity, lang_src='no', lang_tgt='en')
    except AttributeError:
        from translate import Translator
        translator= Translator(from_lang="norwegian",to_lang="english")
        entity = translator.translate(norwegian_entity)

    return(entity)  


label_list = {'SKOTT':'BULKHEAD',
'LAST':'CARGO',
'KOMMUNIKASJONSSYSTEM':'COMMUNICATION SYSTEM',
'KONSTRUKSJON':'CONSTRUCTION',
'KONTROLLSYSTEM':'CONTROL SYSTEM',
'SERTIFIKAT':'CERTIFICATE',
'SELSKAP':'COMPANY',
'KOMPETANSE':'COMPETENCY',
'KONTRAKT':'CONTRACT',
'KURS':'COURSE',
'ØVELSE':'DRILL',
'ELEKSTRISK INNSTALLASJON':'ELECTRICAL INSTALLATION',
'NØD':'EMERGENCY',
'MOTOR':'ENGINE',
'UTSTYR':'EQUIPMENT',
'UTDANNINGSINSTITUSJON':'EDUCATIONAL ESTABLISHMENT',
'BRENSEL':'FUEL',
'GEOGRAFISK LOKASJON':'GEOGRAPHICAL LOCATION',
'STATLIG INSTITUTT':'GOVERNMENT INSTITUTE',
'INFOMASJON':'INFORMATION',
'ISOLASJON':'INSULATION',
'JURIDISK KONSEPT':'LEGAL CONCEPT',
'JURIDISK DOKUMENT':'LEGAL DOCUMENT',
'REDNINGSMIDLER':'LIFE-SAVING APPLIANCE',
'LOSS':'LOADING',
'MASKINERI':'MACHINERY',
'MATERIALE':'MATERIAL',
'FLYTTBARE INNSTALLASJONER':'MOBILE OFFSHORE UNIT',
'PLASSERING PÅ SKIP':'PLACEMENT ON SHIP',
'STILLING':'POSITION',
'PROSEDYRE':'PROCEDURE',
'ENERGIKILDE':'POWER SOURCE',
'PUMPE':'PUMP',
'STABILITET':'STABILITY',
'STYREANRETNING':'STEERING',
'SUBSTANS':'SUBSTANCE',
'FARTSOMRÅDE':'TRADE AREA',
'VERKTØY':'TOOL',
'ENHET':'UNIT',
'VENTIL':'VALVE',
'VENTILASJON':'VENTILATION',
'TYPE FARTØY':'VESSEL TYPE',
'VÆRFORHOLD':'WEATHER CONDITION'}


def return_english_label(norwegian_label):
    norwegian_label = norwegian_label.upper()
    label = 'Unknown label: ' + norwegian_label
    if norwegian_label in label_list:
        label = label_list[norwegian_label]
    return(label)'''