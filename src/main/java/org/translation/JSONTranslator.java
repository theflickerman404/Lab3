package org.translation;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An implementation of the Translator interface which reads in the translation
 * data from a JSON file. The data is read in once each time an instance of this class is constructed.
 */
public class JSONTranslator implements Translator {

    private Map<String, ArrayList<String>> countryCodesToLanguages;
    private Map<String, Map<String, String>> countryCodesToLangaugeCodesToTranslations;
    private List<String> countryCodes;

    /**
     * Constructs a JSONTranslator using data from the sample.json resources file.
     */
    public JSONTranslator() {
        this("sample.json");
    }

    /**
     * Constructs a JSONTranslator populated using data from the specified resources file.
     * @param filename the name of the file in resources to load the data from
     * @throws RuntimeException if the resource file can't be loaded properly
     */
    public JSONTranslator(String filename) {
        this.countryCodes = new ArrayList<>();
        this.countryCodesToLanguages = new HashMap<>();
        this.countryCodesToLangaugeCodesToTranslations = new HashMap<>();

        // read the file to get the data to populate things...
        try {

            String jsonString = Files.readString(Paths.get(getClass().getClassLoader().getResource(filename).toURI()));

            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String alpha3 = jsonObject.getString("alpha3");
                this.countryCodes.add(alpha3);

                // country code: alpha 3, language code alpha 2
                Iterator<String> keys = jsonObject.keys();

                ArrayList<String> languages = new ArrayList<>();
                Map<String, String> languagesToTranslation = new HashMap<>();

                while (keys.hasNext()) {
                    String key = keys.next();
                    if (!("id".equals(key) || "alpha2".equals(key) || "alpha3".equals(key))) {
                        languages.add(key);
                        languagesToTranslation.put(key, jsonObject.getString(key));
                    }
                }
                this.countryCodesToLanguages.put(alpha3, languages);
                this.countryCodesToLangaugeCodesToTranslations.put(alpha3, languagesToTranslation);
            }

        }
        catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<String> getCountryLanguages(String country) {
        List<String> languages = new ArrayList<>();
        for (String languageCode : this.countryCodesToLanguages.get(country)) {
            languages.add(languageCode);
        }
        return languages;
    }

    @Override
    public List<String> getCountries() {
        ArrayList<String> countries = new ArrayList<>();
        for (String country : this.countryCodes) {
            countries.add(country);
        }
        return countries;
    }

    @Override
    public String translate(String country, String language) {
        return this.countryCodesToLangaugeCodesToTranslations.get(country).get(language);
    }
}
