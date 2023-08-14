package com.example.mymobileapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Registration extends AppCompatActivity implements View.OnClickListener {

    private EditText edt_username, edt_email, edt_password, spinnerErros;
    private Button signupBtn;
    private TextView alredyHaveAcc, existUsers, invalidMsgUsername;
    ProgressDialog progressDialog;

    private String selectedDistrict, selectedDivision;
    private Spinner divisionSpinner, districtSpinner;
    private ArrayAdapter<CharSequence> stateAdapter, districtAdapter;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

       

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        edt_username = findViewById(R.id.usernameId);
        edt_email = findViewById(R.id.emailId);
        edt_password = findViewById(R.id.passwordId);
        existUsers = findViewById(R.id.err);


        signupBtn = findViewById(R.id.signUpButton);
        alredyHaveAcc = findViewById(R.id.already_account);

        signupBtn.setOnClickListener(this);
        alredyHaveAcc.setOnClickListener(this);




        //Unique username checking
        DatabaseReference usersRefs = FirebaseDatabase.getInstance().getReference().child("Users");
        invalidMsgUsername = findViewById(R.id.invalidUserMsgTextView);

        edt_username.addTextChangedListener(new TextWatcher() {
            private Handler handler = new Handler();
            private Runnable runnable;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                handler.removeCallbacks(runnable);
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        String username = charSequence.toString().trim();

                        if (!username.isEmpty()) {
                            // Check if username is already registered in the database
                            usersRefs.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        invalidMsgUsername.setText("This username is already taken.");
                                    } else {
                                        invalidMsgUsername.setText("");
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Handle errors
                                    invalidMsgUsername.setText(databaseError.getMessage());
                                }
                            });
                        } else {
                            invalidMsgUsername.setText("Invalid username");
                        }
                    }
                };

                handler.postDelayed(runnable, 500); // Schedule the new runnable
            }

            @Override
            public void afterTextChanged(Editable editable) {

         }
});





        //Everything about Spinner
        divisionSpinner = findViewById(R.id.spinner_bd_division);
        stateAdapter = ArrayAdapter.createFromResource(this, R.array.array_division, R.layout.layout_spinner);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        divisionSpinner.setAdapter(stateAdapter);

        spinnerErros = findViewById(R.id.spinnerError);

        divisionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                districtSpinner = findViewById(R.id.spinner_bd_districts);

                selectedDivision = divisionSpinner.getSelectedItem().toString();

                int parentID = parent.getId();
                if (parentID == R.id.spinner_bd_division){
                    switch (selectedDivision){
                        case "Select Division": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_default_districts, R.layout.layout_spinner);
                            break;
                        case "Dhaka": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_dhaka_districts, R.layout.layout_spinner);
                            break;
                        case "Chittagong": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_chittagong_districts, R.layout.layout_spinner);
                            break;
                        case "Sylhet": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_sylhet_districts, R.layout.layout_spinner);
                            break;
                        case "Rajshahi": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_rajshahi_districts, R.layout.layout_spinner);
                            break;
                        case "Rangpur": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_rangpur_districts, R.layout.layout_spinner);
                            break;
                        case "Barisal": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_barisal_districts, R.layout.layout_spinner);
                            break;
                        case "Khulna": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_khulna_districts, R.layout.layout_spinner);
                            break;
                        case "Mymensingh": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_mymensingh_districts, R.layout.layout_spinner);
                            break;
                        default:  break;
                    }
                    districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    districtSpinner.setAdapter(districtAdapter);

                    districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedDistrict = districtSpinner.getSelectedItem().toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.signUpButton:
                registerUser();
                break;
            case R.id.already_account:
                nextActivity();
                break;
        }
    }

    private void registerUser(){
        String username = edt_username.getText().toString().trim();
        String email = edt_email.getText().toString().trim();
        String password = edt_password.getText().toString().trim();


        if(username.isEmpty()){
            edt_username.setError("username required");
            edt_username.requestFocus();
            return;
        }

        else if(email.isEmpty()){
            edt_email.setError("email required");
            edt_email.requestFocus();
            return;
        }

        else if (selectedDivision.equals("Select Division")) {
            spinnerErros.setError("Division is required!");
            spinnerErros.requestFocus();
            return;

        } else if (selectedDistrict.equals("Select District")) {
            spinnerErros.setError("District is required!");
            spinnerErros.requestFocus();
            return;
        }


        else if(password.isEmpty()){
            edt_password.setError("password required");
            edt_password.requestFocus();
            return;
        }
        

        else if(password.length() < 6){
            edt_password.setError("password should be at least 6 charcter");
            edt_password.requestFocus();
            return;
        }

        else{

            progressDialog.setMessage("Wait while registration");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            String divPlusDis = (selectedDivision+", "+selectedDistrict).toString();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        UserClass userClass = new UserClass(username,divPlusDis,email,password);

                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(userClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            progressDialog.dismiss();
                                            Toast.makeText(Registration.this,"Successfully Registered",Toast.LENGTH_SHORT).show();
                                            nextActivity();
                                        }

                                        else{
                                            progressDialog.dismiss();
                                            Toast.makeText(Registration.this,"Fail",Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                    }

                    else{
                        progressDialog.dismiss();

                        String s = ""+task.getException();
                        if(s.length() == 116){
                            existUsers.setText("Username already exist");
                        }
                        else{
                            Toast.makeText(Registration.this,""+task.getException(),Toast.LENGTH_SHORT).show();
                        }

                    }

                }
            });
        }


    }
    private void nextActivity(){
        Intent i = new Intent(Registration.this,Login.class);
        startActivity(i);
    }
    }
