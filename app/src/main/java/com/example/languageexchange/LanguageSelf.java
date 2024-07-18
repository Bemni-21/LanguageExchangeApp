package com.example.languageexchange;

public class LanguageSelf {
    private String sourceLanguage;
    private String targetLanguage;
    private String word;
    private String translatedWord;

    public LanguageSelf() {

    }

    public LanguageSelf(String sourceLanguage, String targetLanguage, String word, String translatedWord) {
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.word = word;
        this.translatedWord = translatedWord;
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTranslatedWord() {
        return translatedWord;
    }

    public void setTranslatedWord(String translatedWord) {
        this.translatedWord = translatedWord;
    }
}

