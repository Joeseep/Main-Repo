package com.example.lifesaver;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {

    private TextView mProfileName, mProfileAddress, mProfilePhone, mEmail ,mDoB, mBloodType, mAllergies, mMedication, mCondition, mPrevSurgery, mEmergencyContact, mInsurance;
    private ImageView mProfileImage;

    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;

    private static final int REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize UI components
        mProfileName = findViewById(R.id.profile_name);
        mProfileAddress = findViewById(R.id.profile_address);
        mProfilePhone = findViewById(R.id.profile_phone);
        mEmail = findViewById(R.id.profile_email);
        mProfileImage = findViewById(R.id.profile_image);
        mDoB = findViewById(R.id.dob_text);
        mAllergies = findViewById(R.id.allergies_text);
        mBloodType = findViewById(R.id.blood_type_text);
        mInsurance = findViewById(R.id.insurance_info_text);
        mCondition = findViewById(R.id.chronic_conditions_text);
        mMedication = findViewById(R.id.current_medications_text);
        mPrevSurgery = findViewById(R.id.prev_surgeries_text);
        mEmergencyContact = findViewById(R.id.emergency_contact_text);


        Button mUploadProfileButton = findViewById(R.id.upload_profile);
        Button mEditButton = findViewById(R.id.edit_button);
        Button mShareButton = findViewById(R.id.share);

        // Initialize Firebase components
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(mCurrentUser.getUid());
        mStorageReference = FirebaseStorage.getInstance().getReference("User profiles");

        // Load profile data from Firebase database
        loadProfileData();

        // Set click listeners for upload profile and edit buttons
        mUploadProfileButton.setOnClickListener(view -> chooseProfileImage());
        mEditButton.setOnClickListener(view -> showEditProfileDialog());
        mShareButton.setOnClickListener(v -> mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("fullName").getValue(String.class);
                String address = dataSnapshot.child("address").getValue(String.class);
                String phone = dataSnapshot.child("phoneNumber").getValue(String.class);
                String email = dataSnapshot.child("email").getValue(String.class);
                String DoB = dataSnapshot.child("dateOfBirth").getValue(String.class);
                String blood_type = dataSnapshot.child("bloodType").getValue(String.class);
                String allergies = dataSnapshot.child("allergies").getValue(String.class);
                String medication = dataSnapshot.child("medications").getValue(String.class);
                String condition = dataSnapshot.child("medicalCondition").getValue(String.class);
                String prev_surgery = dataSnapshot.child("medicalProcedures").getValue(String.class);
                String emergency_contact = dataSnapshot.child("emergencyContact").getValue(String.class);
                String insurance_info = dataSnapshot.child("insuranceInfo").getValue(String.class);

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My Medical Information");
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        "Name: " + name + "\n" +
                                "Address: " + address + "\n" +
                                "Phone: " + phone + "\n" +
                                "Email: " + email + "\n" +
                                "Date of Birth: " + DoB + "\n" +
                                "Blood Type: " + blood_type + "\n" +
                                "Allergies: " + allergies + "\n" +
                                "Medications: " + medication + "\n" +
                                "Medical Condition: " + condition + "\n" +
                                "Previous Surgeries: " + prev_surgery + "\n" +
                                "Emergency Contact: " + emergency_contact + "\n" +
                                "Insurance Information: " + insurance_info + "\n" );
                startActivity(Intent.createChooser(shareIntent, "Share using"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Cancelled",Toast.LENGTH_SHORT).show();
            }
        }));


    }

    // Method to load profile data from Firebase database
    private void loadProfileData() {
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("fullName").getValue(String.class);
                String address = dataSnapshot.child("address").getValue(String.class);
                String phone = dataSnapshot.child("phoneNumber").getValue(String.class);
                String email = dataSnapshot.child("email").getValue(String.class);
                String DoB = dataSnapshot.child("dateOfBirth").getValue(String.class);
                String blood_type = dataSnapshot.child("bloodType").getValue(String.class);
                String allergies = dataSnapshot.child("allergies").getValue(String.class);
                String medication = dataSnapshot.child("medications").getValue(String.class);
                String condition = dataSnapshot.child("medicalCondition").getValue(String.class);
                String prev_surgery = dataSnapshot.child("medicalProcedures").getValue(String.class);
                String emergency_contact = dataSnapshot.child("emergencyContact").getValue(String.class);
                String insurance_info = dataSnapshot.child("insuranceInfo").getValue(String.class);
                String profilePictureUrl = dataSnapshot.child("profilePictureUrl").getValue(String.class);

                mProfileName.setText(name);
                mProfileAddress.setText(address);
                mProfilePhone.setText(phone);
                mAllergies.setText(allergies != null ? allergies : "N/A");
                mEmail.setText(email != null ? email : "N/A");
                mDoB.setText(DoB != null ? DoB : "N/A");
                mMedication.setText(medication != null ? medication : "N/A");
                mBloodType.setText(blood_type != null ? blood_type : "N/A");
                mCondition.setText(condition != null ? condition : "N/A");
                mPrevSurgery.setText(prev_surgery != null ? prev_surgery : "N/A");
                mEmergencyContact.setText(emergency_contact != null ? emergency_contact : "N/A");
                mInsurance.setText(insurance_info != null ? insurance_info : "N/A");


                if (profilePictureUrl != null && !profilePictureUrl.equals("default")) {
                    Picasso.get().load(profilePictureUrl).into(mProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Profile", "Error loading profile data", databaseError.toException());
            }
        });
    }
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    // Method to choose profile image
    private final ActivityResultLauncher<String> getContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
                        // Retrieve the current profile picture URL from Firebase database
                        mDatabaseReference.child("profilePictureUrl").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String currentUrl = snapshot.getValue(String.class);
                                String DEFAULT_PICTURE_URL = "https://firebasestorage.googleapis.com/v0/b/athena-688cb.appspot.com/o/User%20profiles%2Fno_profile.jpg?alt=media&token=2a443f47-ec72-42ef-9c2d-da8cfcf2563d";
                                if (currentUrl != null && !currentUrl.equals(DEFAULT_PICTURE_URL)) {
                                    // Delete the old profile picture if it is not the default picture
                                    StorageReference oldFileReference = FirebaseStorage.getInstance().getReferenceFromUrl(currentUrl);
                                    oldFileReference.delete().addOnSuccessListener(unused -> uploadNewProfilePicture(uri)).addOnFailureListener(e -> {
                                        Toast.makeText(Profile.this, "Failed to delete old profile picture", Toast.LENGTH_SHORT).show();
                                        Log.e("Profile", "Error deleting old profile picture", e);
                                    });
                                } else {
                                    uploadNewProfilePicture(uri);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(Profile.this, "Failed to retrieve current profile picture URL", Toast.LENGTH_SHORT).show();
                                Log.e("Profile", "Error retrieving current profile picture URL", error.toException());
                            }
                        });
                    }
                }
            });

    private void uploadNewProfilePicture(Uri uri) {
        // Upload the selected image to Firebase storage
        StorageReference fileReference = mStorageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        UploadTask uploadTask = fileReference.putFile(uri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Get the download URL of the uploaded image
            Task<Uri> downloadUrlTask = taskSnapshot.getStorage().getDownloadUrl();
            downloadUrlTask.addOnSuccessListener(downloadUri -> {
                // Update the user's profile picture URL in Firebase database
                mDatabaseReference.child("profilePictureUrl").setValue(downloadUri.toString())
                        .addOnSuccessListener(aVoid -> Toast.makeText(Profile.this, "Profile picture updated", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> {
                            Toast.makeText(Profile.this, "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                            Log.e("Profile", "Error updating profile picture URL", e);
                        });
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(Profile.this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
            Log.e("Profile", "Error uploading profile picture", e);
        });
    }


    private void chooseProfileImage() {
        getContent.launch("image/*");
    }


    // Method to show edit profile dialog
    private void showEditProfileDialog() {
        final Dialog editDialog = new Dialog(this);
        editDialog.setContentView(R.layout.dialog_edit_profile);
        editDialog.setTitle("Edit Profile");

        EditText nameEditText = editDialog.findViewById(R.id.edit_name);
        EditText addressEditText = editDialog.findViewById(R.id.edit_address);
        EditText phoneEditText = editDialog.findViewById(R.id.edit_phone);
        EditText dobEditText = editDialog.findViewById(R.id.edit_dob);
        EditText bloodTypeEditText = editDialog.findViewById(R.id.edit_blood_type);
        EditText allergiesEditText = editDialog.findViewById(R.id.edit_allergies);
        EditText medicationsEditText = editDialog.findViewById(R.id.edit_medications);
        EditText conditionsEditText = editDialog.findViewById(R.id.edit_conditions);
        EditText proceduresEditText = editDialog.findViewById(R.id.edit_procedures);
        EditText emergencyContactEditText = editDialog.findViewById(R.id.edit_emergency_contact);
        EditText insuranceInfoEditText = editDialog.findViewById(R.id.edit_insurance_info);

        // Add a DatePickerDialog for the date of birth EditText
        dobEditText.setOnClickListener(view -> {
            // Get the current date
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create a DatePickerDialog with the current date as the default
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (datePicker, yearSelected, monthSelected, daySelected) -> {
                        // Update the date of birth EditText with the selected date
                        String selectedDate = (monthSelected + 1) + "/" + daySelected + "/" + yearSelected;
                        dobEditText.setText(selectedDate);
                    }, year, month, day);

            // Show the DatePickerDialog
            datePickerDialog.show();
        });

        // Add a click listener to the blood type EditText
        bloodTypeEditText.setOnClickListener(view -> {
            // Create a list of blood types
            String[] bloodTypes = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

            // Create a dialog box with the blood type options
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Blood Type");
            builder.setItems(bloodTypes, (dialog, which) -> {
                // Update the blood type EditText with the selected blood type
                String selectedBloodType = bloodTypes[which];
                bloodTypeEditText.setText(selectedBloodType);
            });

            // Show the dialog box
            builder.show();
        });




        // Load current profile data into edit dialog
        String name = mProfileName.getText().toString();
        String address = mProfileAddress.getText().toString();
        String phone = mProfilePhone.getText().toString();
        String dob = mDoB.getText().toString();
        String bloodType = mBloodType.getText().toString();
        String allergies = mAllergies.getText().toString();
        String medications = mMedication.getText().toString();
        String condition = mCondition.getText().toString();
        String procedures = mPrevSurgery.getText().toString();
        String emergencyContact = mEmergencyContact.getText().toString();
        String insuranceInfo = mInsurance.getText().toString();

        nameEditText.setText(name.equals("N/A") ? "" : name);
        addressEditText.setText(address.equals("N/A") ? "" : address);
        phoneEditText.setText(phone.equals("N/A") ? "" : phone);
        dobEditText.setText(dob.equals("N/A") ? "" : dob);
        bloodTypeEditText.setText(bloodType.equals("N/A") ? "" : bloodType);
        allergiesEditText.setText(allergies.equals("N/A") ? "" : allergies);
        medicationsEditText.setText(medications.equals("N/A") ? "" : medications);
        conditionsEditText.setText(condition.equals("N/A") ? "" : condition);
        proceduresEditText.setText(procedures.equals("N/A") ? "" : procedures);
        emergencyContactEditText.setText(emergencyContact.equals("N/A") ? "" : emergencyContact);
        insuranceInfoEditText.setText(insuranceInfo.equals("N/A") ? "" : insuranceInfo);



        Button saveButton = editDialog.findViewById(R.id.save_button);
        saveButton.setOnClickListener(view -> {
            // Get updated profile data from edit dialog
            String updatedName = nameEditText.getText().toString();
            String updatedAddress = addressEditText.getText().toString();
            String updatedPhone = phoneEditText.getText().toString();
            String updatedDoB = dobEditText.getText().toString();
            String updatedBloodType = bloodTypeEditText.getText().toString();
            String updatedAllergies = allergiesEditText.getText().toString();
            String updatedMeds = medicationsEditText.getText().toString();
            String updatedCondition = conditionsEditText.getText().toString();
            String updatedProcedures = proceduresEditText.getText().toString();
            String updatedEmergencyContact = emergencyContactEditText.getText().toString();
            String updatedInsuranceInfo = insuranceInfoEditText.getText().toString();

            // Update profile data in Firebase database
            updateProfileData(updatedName, updatedAddress, updatedPhone, updatedDoB, updatedBloodType,
                    updatedAllergies, updatedMeds, updatedCondition, updatedProcedures,
                    updatedEmergencyContact, updatedInsuranceInfo);

            // Dismiss edit dialog
            editDialog.dismiss();
            recreate();
        });
        Button cancelButton = editDialog.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(view -> editDialog.dismiss());

        editDialog.show();
    }

    // Method to update profile data in Firebase database
    private void updateProfileData(String name, String address, String phone, String updatedDoB, String updatedBloodType, String updatedAllergies, String updatedMeds, String updatedCondition, String updatedProcedures, String updatedEmergencyContact, String updatedInsuranceInfo) {
        // Update profile data in Firebase database
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("fullName", name);
        updateData.put("address", address);
        updateData.put("phoneNumber", phone);
        updateData.put("dateOfBirth", updatedDoB);
        updateData.put("bloodType", updatedBloodType);
        updateData.put("allergies", updatedAllergies);
        updateData.put("medications", updatedMeds);
        updateData.put("medicalCondition", updatedCondition);
        updateData.put("medicalProcedures", updatedProcedures);
        updateData.put("emergencyContact", updatedEmergencyContact);
        updateData.put("insuranceInfo", updatedInsuranceInfo);

        mDatabaseReference.updateChildren(updateData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Profile.this, "Profile data updated successfully",Toast.LENGTH_SHORT).show();
                    Log.d("Profile", "Profile data updated successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("Profile", "Error updating profile data", e);
                    Toast.makeText(Profile.this, "Error updating profile data", Toast.LENGTH_SHORT).show();
                });
    }


    // Handle result of choose profile image intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            uploadProfileImage(selectedImageUri);
        }
    }

    // Method to upload profile image to Firebase storage
    private void uploadProfileImage(Uri imageUri) {
        StorageReference profileImageRef = mStorageReference.child(mCurrentUser.getUid() + ".jpg");
        profileImageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get profile image download URL
                    profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Update profile picture URL in Firebase database
                        mDatabaseReference.child("profilePictureUrl").setValue(uri.toString())
                                .addOnSuccessListener(aVoid ->Toast.makeText(Profile.this, "Profile picture uploaded successfully",Toast.LENGTH_SHORT).show())
                                .addOnSuccessListener(aVoid -> Log.d("Profile", "Profile picture URL updated successfully"))
                                .addOnFailureListener(e -> Log.e("Profile", "Error updating profile picture URL", e))
                                .addOnFailureListener(e -> Toast.makeText(Profile.this, "Error uploading profile picture", Toast.LENGTH_SHORT).show());
                    });
                })
                .addOnFailureListener(e -> Log.e("Profile", "Error uploading profile image", e));
    }
}
