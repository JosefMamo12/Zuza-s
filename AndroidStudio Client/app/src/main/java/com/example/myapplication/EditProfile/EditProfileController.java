package com.example.myapplication.EditProfile;

import java.util.HashMap;

public class EditProfileController {
    private static final EditProfileModel profileModel = new EditProfileModel();

    public static void beforeUpdating(EditProfileCallBack listener, HashMap<Integer, String> myHash) {
        profileModel.getData(listener, myHash);
    }


    public static int updateProfile(String username, String email, String phoneNumber) {
        return profileModel.updateProfile(username, email, phoneNumber);
    }


    public static boolean checkBeforeAfterProfile(String pName, String pAge, String pPhone, String fullName, String age, String phoneNumber) {
        return profileModel.beforeAfterCheck(pName,pAge,pPhone,fullName,age,phoneNumber);

    }
}
