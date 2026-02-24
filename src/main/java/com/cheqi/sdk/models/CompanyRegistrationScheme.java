package com.cheqi.sdk.models;

public enum CompanyRegistrationScheme {
    AUSTRIA("Austria", "Firmenbuch", "Firmenbuchnummer"),
    BELGIUM("Belgium", "Crossroads Bank for Enterprises", "BCE / KBO number"),
    BULGARIA("Bulgaria", "Bulgarian Commercial Register", "UIC"),
    CROATIA("Croatia", "Court Register Croatia", "MBS"),
    CYPRUS("Cyprus", "Cyprus Department of Registrar of Companies", "HE number"),
    CZECH_REPUBLIC("Czech Republic", "Czech Commercial Register", "IČO"),
    DENMARK("Denmark", "CVR Register", "CVR"),
    ESTONIA("Estonia", "Estonian Business Register", "Registry code"),
    FINLAND("Finland", "Finnish Trade Register", "Y-tunnus"),
    FRANCE("France", "Registre du Commerce et des Sociétés", "SIREN / SIRET"),
    GERMANY("Germany", "Handelsregister", "Handelsregisternummer"),
    GREECE("Greece", "General Commercial Registry", "GEMI"),
    HUNGARY("Hungary", "Hungarian Company Register", "Cégjegyzékszám"),
    IRELAND("Ireland", "Companies Registration Office Ireland", "CRO number"),
    ITALY("Italy", "Registro delle Imprese", "Numero REA"),
    LATVIA("Latvia", "Latvian Enterprise Register", "Registration number"),
    LITHUANIA("Lithuania", "Lithuanian Register of Legal Entities", "Company code"),
    LUXEMBOURG("Luxembourg", "Registre de Commerce et des Sociétés Luxembourg", "RCS"),
    MALTA("Malta", "Malta Business Registry", "Company number"),
    NETHERLANDS("Netherlands", "Kamer van Koophandel", "KvK nummer"),
    POLAND("Poland", "National Court Register Poland", "KRS"),
    PORTUGAL("Portugal", "Instituto dos Registos e do Notariado", "NIPC"),
    ROMANIA("Romania", "Romanian Trade Register", "CUI"),
    SLOVAKIA("Slovakia", "Slovak Commercial Register", "IČO"),
    SLOVENIA("Slovenia", "AJPES", "Matična številka"),
    SPAIN("Spain", "Registro Mercantil", "CIF / NIF"),
    SWEDEN("Sweden", "Bolagsverket", "Organisationsnummer"),
    UNITED_STATES("United States", "Internal Revenue Service", "EIN");

    private final String country;
    private final String officialBusinessRegister;
    private final String commonName;

    CompanyRegistrationScheme(String country, String officialBusinessRegister, String commonName) {
        this.country = country;
        this.officialBusinessRegister = officialBusinessRegister;
        this.commonName = commonName;
    }

    public String getCountry() {
        return country;
    }

    public String getOfficialBusinessRegister() {
        return officialBusinessRegister;
    }

    public String getCommonName() {
        return commonName;
    }
}
