from pathlib import Path
from google_trans_new import google_translator


def translate_norwegian_entity(norwegian_entity):
    from google_trans_new import google_translator  
    translator = google_translator()  
    try:
        entity = translator.translate(norwegian_entity, lang_src='no', lang_tgt='en')
    except AttributeError:
        import translatepy
        translator = translatepy.Translator()
        translator.translate(norwegian_entity, "English")


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
    return(label)