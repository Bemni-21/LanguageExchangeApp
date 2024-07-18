package com.example.languageexchange;

public class LanguagePair {
    private String id;
    private String userId;
    private String sourceLanguage;
    private String targetLanguage;
    private String word;
    private String translatedWord;

    public LanguagePair() {

    }

    public LanguagePair(String id, String userId, String sourceLanguage, String targetLanguage, String word, String translatedWord) {
        this.id = id;
        this.userId = userId;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.word = word;
        this.translatedWord = translatedWord;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
