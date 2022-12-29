package com.example.myapplication;

public class EditProfileController {
    private static final EditProfileModel profileModel = new EditProfileModel();

    public static void beforeUpdating(MyListener listener) {
        profileModel.getData(listener);
    }

    public static int updateProfile(String username, String email, String phoneNumber) {
        return profileModel.updateProfile(username, email, phoneNumber);
    }

}
