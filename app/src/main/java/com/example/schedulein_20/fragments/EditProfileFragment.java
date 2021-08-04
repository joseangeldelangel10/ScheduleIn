package com.example.schedulein_20.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.schedulein_20.R;
import com.example.schedulein_20.parseDatabaseComms.UserSession;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.media.ThumbnailUtils;
import java.lang.Math;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment {
    private final String TAG = "EditProfileFragment";
    protected static final String AUTHORITY_PROVIDER =  "com.codepath.fileprovider";
    public final static int PICK_PHOTO_CODE = 1046;
    private ImageView userProfilePic;
    private TextView userName;
    private TextView userSurname;
    private TextView userUsername;
    private TextView userEmail;
    private CheckBox userSIWGoogle;
    private RelativeLayout updatePassword;
    private Button updateProfile;
    private ImageButton startEditing;
    private ImageButton updateProfilePic;
    File newPhotoFile;
    ParseUser currentUser;
    Context context;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static EditProfileFragment newInstance(String param1, String param2) {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentUser = ParseUser.getCurrentUser();
        context = getContext();
        newPhotoFile = null;
        try {
            newPhotoFile = currentUser.getParseFile("profilePic").getFile();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        userProfilePic = view.findViewById(R.id.editProfileProfilePicture);
        userName = view.findViewById(R.id.editProfileName);
        userSurname = view.findViewById(R.id.editProfileSurname);
        userUsername = view.findViewById(R.id.editProfileUsername);
        userEmail = view.findViewById(R.id.editProfileEmail);
        userSIWGoogle = view.findViewById(R.id.editProfileSIWGoogleCheckBox);
        updatePassword = view.findViewById(R.id.editProfileUpdatePasswordRL);
        updateProfile = view.findViewById(R.id.editFragmentUpdateProfile);
        startEditing = view.findViewById(R.id.editProfileStartEditingButton);
        updateProfilePic = view.findViewById(R.id.editProfileChangePicture);

        updateProfile.setVisibility(View.GONE);
        disablePickPhotoButton();
        //updateProfile.setLayoutParams(new RelativeLayout.LayoutParams(220, 0));

        File profilePic = null;
        try {
            profilePic = currentUser.getParseFile("profilePic").getFile();
            Log.e(TAG, "file retrivered: ");
        } catch (ParseException e) {
            Log.e(TAG, "couldn't load image file");
            e.printStackTrace();
        }

        Glide.with(context).load(profilePic).placeholder(R.drawable.profile_picture_placeholder).into(userProfilePic);
        userSIWGoogle.setChecked(currentUser.getBoolean("googleUser"));
        userName.setText(currentUser.getString("name"));
        userSurname.setText(currentUser.getString("surname"));
        userUsername.setText(currentUser.getUsername());
        userEmail.setText(currentUser.getEmail());

        if(!currentUser.getBoolean("googleUser")) {
            startEditing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enableEditing();
                }
            });
        }else { disableStartEditingButton(); }

        updateProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(v);
            }
        });

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "updating profile...", Toast.LENGTH_SHORT).show();
                String newName = userName.getText().toString();
                String newSurname = userSurname.getText().toString();
                String newUsername = userUsername.getText().toString();
                String newEmail = userEmail.getText().toString();
                UserSession.updateCurrentUser(context, newName, newSurname, newUsername, newEmail, newPhotoFile, userUpdatedCallback());

            }
        });

    }

    private SaveCallback userUpdatedCallback() {
        return new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Toast.makeText(context, "user updated successfully!", Toast.LENGTH_SHORT).show();
                    Fragment fragment = new UserProfileFragment();
                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.host_frame, fragment)
                            .commit();
                }else {
                    Toast.makeText(context, "there was a problem updating the user", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "error updating user \n", e);
                }
            }
        };
    }

    private void enableEditing(){
        userName.setEnabled(true);
        userSurname.setEnabled(true);
        userUsername.setEnabled(true);
        userEmail.setEnabled(true);
        updateProfile.setVisibility(View.VISIBLE);
        enablePickPhotoButton();
        disableStartEditingButton();
        //updateProfile.setLayoutParams(new RelativeLayout.LayoutParams(220, RelativeLayout.LayoutParams.WRAP_CONTENT));
    }

    private void enablePickPhotoButton() {
        updateProfilePic.setEnabled(true);
        updateProfilePic.setBackground(new ColorDrawable(getResources().getColor(R.color.secondary)));
    }

    private void disablePickPhotoButton() {
        updateProfilePic.setEnabled(false);
        updateProfilePic.setBackground(new ColorDrawable(getResources().getColor(R.color.gray)));
    }

    private void disableStartEditingButton() {
        startEditing.setEnabled(false);
        startEditing.setBackground(new ColorDrawable(getResources().getColor(R.color.gray)));
    }





    // Trigger gallery selection for a photo
    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    private File bitmap2File(Bitmap photoBitmap){
        //create a file to write bitmap data
        File f = new File(context.getCacheDir(), "ProfilePic.png");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Convert bitmap to byte array
        Bitmap bitmap = photoBitmap;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(bitmapdata);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return f;
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(context.getContentResolver(), photoUri);
            }
            image = cropCenter(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public static Bitmap cropCenter(Bitmap bmp) {
        int dimension = Math.min(bmp.getWidth(), bmp.getHeight());
        return ThumbnailUtils.extractThumbnail(bmp, dimension, dimension);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();

            // Load the image located at photoUri into selectedImage
            Bitmap selectedImage = loadFromUri(photoUri);
            File photoFromBitmap = bitmap2File(selectedImage);
            if (photoFromBitmap != null) {
                newPhotoFile = photoFromBitmap;
            }

            // Load the selected image into a preview
            ImageView ivPreview = userProfilePic;
            ivPreview.setImageBitmap(selectedImage);
        }
    }


}
