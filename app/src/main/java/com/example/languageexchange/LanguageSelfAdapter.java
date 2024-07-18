package com.example.languageexchange;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class LanguageSelfAdapter extends ArrayAdapter<LanguageSelf> {
    private Context mContext;
    private int mResource;
    private List<LanguageSelf> languageSelfList;

    public LanguageSelfAdapter(@NonNull Context context, int resource, @NonNull List<LanguageSelf> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
        this.languageSelfList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResource, parent, false);
        }

        LanguageSelf languageSelf = getItem(position);

        TextView sourceLanguageTv = convertView.findViewById(R.id.sourceLanguageTv);
        TextView wordTv = convertView.findViewById(R.id.wordTv);
        TextView targetLanguageTv = convertView.findViewById(R.id.targetLanguageTv);
        TextView translationTv = convertView.findViewById(R.id.translationTv);

        if (languageSelf != null) {
            sourceLanguageTv.setText(languageSelf.getSourceLanguage() + ":");
            wordTv.setText(languageSelf.getWord());
            targetLanguageTv.setText(languageSelf.getTargetLanguage() + ":");
            translationTv.setText(languageSelf.getTranslatedWord());
        }

        return convertView;
    }
}
