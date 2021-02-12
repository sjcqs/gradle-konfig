package fr.sjcqs.parser

class UnsupportedMixedTypes(key: String = "") :
    ParsingException("Mixin types are not supported $key")