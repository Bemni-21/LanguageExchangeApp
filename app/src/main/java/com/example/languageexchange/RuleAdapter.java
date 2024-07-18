package com.example.languageexchange;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RuleAdapter extends ArrayAdapter<String> {

    public RuleAdapter(Context context, String[] rules) {
        super(context, 0, rules);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String rule = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.each_rules, parent, false);
        }
        TextView ruleTextView = convertView.findViewById(R.id.rule);
        ruleTextView.setText(rule);
        return convertView;
    }
}
