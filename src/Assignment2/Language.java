package Assignment2;

/**
 * Enum to hold the language and names
 */
public enum Language {

    Basque("eu"), Catalan("ca"), Galician("gl"), Spanish("es"), English("en"), Protugese("pt");

    String val;
    Language(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }


}
