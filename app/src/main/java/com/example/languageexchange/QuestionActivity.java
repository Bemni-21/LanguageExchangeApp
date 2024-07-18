package com.example.languageexchange;



import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class QuestionActivity extends AppCompatActivity {

    private EditText questionEditText;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question);

        questionEditText = findViewById(R.id.question);
        submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = questionEditText.getText().toString().trim();

                if (question.isEmpty()) {
                    Toast.makeText(QuestionActivity.this, "Please enter a question", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(QuestionActivity.this, "Your question is submitted successfully", Toast.LENGTH_SHORT).show();

                    questionEditText.setText("");
                }
            }
        });
    }
}
