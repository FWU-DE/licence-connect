﻿from flask import Flask, jsonify, request, abort, Response
from lxml import etree
from enum import Enum
from dataclasses import dataclass

from arix_example_licences import get_example_licences
from arix_licence import ArixLicence

app = Flask(__name__)

class RequestType(Enum):
    Search = 1
    Login = 2
    Invalid = 3

@dataclass
class SearchRequest:
    fields: list[str]

@app.route('/<land>/<standortnummer>/<schulnummer>/<userid>', methods=['POST'])
@app.route('/<land>/<standortnummer>/<schulnummer>', methods=['POST'])
@app.route('/<land>/<standortnummer>', methods=['POST'])
@app.route('/<land>', methods=['POST'])
def endpoint(land = None, standortnummer = None, schulnummer = None, userid = None):
    xmlstatement = request.form.get("xmlstatement")
    if xmlstatement is None:
        abort(400)
    request_type, parsed_request = parse_request(xmlstatement)
    match request_type:
        case RequestType.Search:
            licences = get_example_licences(land, standortnummer, schulnummer, userid)
            xml = format_response(licences, parsed_request.fields)
            return Response(xml, mimetype='text/xml')
        case RequestType.Login:
            abort(501)
        case RequestType.Invalid:
            abort(400)

def format_response(licenses: list[ArixLicence], field_names: list[str]):
    result = etree.Element("result")
    for licence in licenses:
        licence_element = etree.SubElement(result, "r")
        licence_element.set("identifier", licence.id)
        for field_name in field_names:
            if (field_value := getattr(licence, field_name)) is not None:
                field_element = etree.SubElement(licence_element, "f")
                field_element.set("n", field_name)
                field_element.text = field_value
    return etree.tostring(result)

def parse_request(xml_string: str):
    root = etree.fromstring(xml_string)
    match root.tag:
        case "search":
            return RequestType.Search, SearchRequest(root.get("fields").replace(" ", "").split(","))
        case "login":
            return RequestType.Login, None
        case _:
            return RequestType.Invalid, None


if __name__ == '__main__':
    app.run(debug=True, port=1234, host="0.0.0.0")