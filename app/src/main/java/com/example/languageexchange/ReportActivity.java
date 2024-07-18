package com.example.languageexchange;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends Activity {

    int preSelectedIndex = -1;
    private List<UserModel> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_user);

        ListView listView = findViewById(R.id.listview);
        Button reportButton = findViewById(R.id.button_report);

        users = new ArrayList<>();
        users.add(new UserModel(false, "Inappropriate or Offensive Language"));
        users.add(new UserModel(false, "Harassment or Bullying"));
        users.add(new UserModel(false, "Fake Profile"));
        users.add(new UserModel(false, "Inappropriate Content"));
        users.add(new UserModel(false, "Misleading Information"));
        users.add(new UserModel(false, "Spam"));
        users.add(new UserModel(false, "Scamming"));
        users.add(new UserModel(false, "Racism or discrimination"));
        users.add(new UserModel(false, "Other"));

        final CustomAdapter adapter = new CustomAdapter(this, users);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UserModel model = users.get(i);
                model.setSelected(!model.isSelected());
                users.set(i, model);
                adapter.updateRecords(users);
            }
        });

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAnyUserSelected()) {
                    Toast.makeText(ReportActivity.this, "User reported successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ReportActivity.this, "Please choose a reason to report", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isAnyUserSelected() {
        for (UserModel user : users) {
            if (user.isSelected()) {
                return true;
            }
        }
        return false;
    }
}
