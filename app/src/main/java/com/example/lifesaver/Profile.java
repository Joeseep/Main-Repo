package com.example.lifesaver;

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

import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {

    private TextView mProfileName, mProfileAddress, mProfilePhone;
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
        mProfileImage = findViewById(R.id.profile_image);
        Button mUploadProfileButton = findViewById(R.id.upload_profile);
        Button mEditButton = findViewById(R.id.edit_button);

        // Initialize Firebase components
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(mCurrentUser.getUid());
        mStorageReference = FirebaseStorage.getInstance().getReference("User profiles");

        // Load profile data from Firebase database
        loadProfileData();

        // Set click listeners for upload profile and edit buttons
        mUploadProfileButton.setOnClickListener(view -> chooseProfileImage());
        mEditButton.setOnClickListener(view -> showEditProfileDialog());
    }

    // Method to load profile data from Firebase database
    private void loadProfileData() {
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("fullName").getValue(String.class);
                String address = dataSnapshot.child("address").getValue(String.class);
                String phone = dataSnapshot.child("phoneNumber").getValue(String.class);
                String profilePictureUrl = dataSnapshot.child("profilePictureUrl").getValue(String.class);

                mProfileName.setText(name);
                mProfileAddress.setText(address);
                mProfilePhone.setText(phone);

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

        // Load current profile data into edit dialog
        nameEditText.setText(mProfileName.getText().toString());
        addressEditText.setText(mProfileAddress.getText().toString());
        phoneEditText.setText(mProfilePhone.getText().toString());

        Button saveButton = editDialog.findViewById(R.id.save_button);
        saveButton.setOnClickListener(view -> {
            // Get updated profile data from edit dialog
            String updatedName = nameEditText.getText().toString();
            String updatedAddress = addressEditText.getText().toString();
            String updatedPhone = phoneEditText.getText().toString();
            // Update profile data in Firebase database
            updateProfileData(updatedName, updatedAddress, updatedPhone);

            // Dismiss edit dialog
            editDialog.dismiss();
        });
        Button cancelButton = editDialog.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(view -> editDialog.dismiss());

        editDialog.show();
    }

    // Method to update profile data in Firebase database
    private void updateProfileData(String name, String address, String phone) {
        // Update profile data in Firebase database
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("fullName", name);
        updateData.put("address", address);
        updateData.put("phoneNumber", phone);
        mDatabaseReference.updateChildren(updateData)
                .addOnSuccessListener(aVoid -> Log.d("Profile", "Profile data updated successfully"))
                .addOnFailureListener(e -> Log.e("Profile", "Error updating profile data", e));
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
                                .addOnSuccessListener(aVoid -> Log.d("Profile", "Profile picture URL updated successfully"))
                                .addOnFailureListener(e -> Log.e("Profile", "Error updating profile picture URL", e));
                    });
                })
                .addOnFailureListener(e -> Log.e("Profile", "Error uploading profile image", e));
    }
}
