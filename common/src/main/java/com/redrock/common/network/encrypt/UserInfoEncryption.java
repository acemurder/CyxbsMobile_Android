package com.redrock.common.network.encrypt;

import android.util.Base64;
import android.util.Log;

import com.redrock.common.ContextProvider;
import com.redrock.common.config.Config;
import com.redrock.common.config.Const;
import com.redrock.common.utils.SPUtils;

import java.io.UnsupportedEncodingException;

/**
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */

public class UserInfoEncryption {

    private Encryptor encryptor;
    private boolean isSupportEncrypt = true;

    public UserInfoEncryption() {
        encryptor = new SerialAESEncryptor();
        try {
            encryptor.encrypt("abc".getBytes("UTF-8"));
        } catch (Exception e) {
            Log.e("CSET_UIE", "not support", e);
            isSupportEncrypt = false;
        }
        synchronized (UserInfoEncryption.class) {
            int currentVersion = (int) SPUtils.get(ContextProvider.getContext(), Config.SP_KEY_ENCRYPT_VERSION_USER);
            if (currentVersion < Config.USER_INFO_ENCRYPT_VERSION) {
                onUpdate(currentVersion, Config.USER_INFO_ENCRYPT_VERSION);
            }
        }
    }

    public String encrypt(String json) {
        if (json == null) {
            json = "";
        }
        if (!isSupportEncrypt) {
            return json;
        }
        try {
            return Base64.encodeToString(encryptor.encrypt(json.getBytes("UTF-8")), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }

    public String decrypt(String base64Encrypted) {
        if (base64Encrypted == null || base64Encrypted.equals("")) {
            return "";
        }
        if (!isSupportEncrypt) {
            return base64Encrypted;
        }
        try {
            return new String(encryptor.decrypt(Base64.decode(base64Encrypted, Base64.DEFAULT)), "UTF-8");
        } catch (DecryptFailureException e) {
            Log.e("CSET_UIE", "decrypt failure", e);
            return "";
        } catch (UnsupportedEncodingException e) {
            Log.e("CSET_UIE", "decrypt failure", e);
            return "";
        }
    }

    /**
     * if you update the encrypt method in the future, please update here for compatibility
     *
     * @param i  old version
     * @param ii new version
     */
    public void onUpdate(int i, int ii) {
        Log.d("CSET_UIE", "onUpdate: " + i + ", " + ii);
        if (i == 0 && ii == 1) {
            String unEncryptedJson = (String) SPUtils.get(ContextProvider.getContext(), Const.SP_KEY_USER, "");
            if (!"".equals(unEncryptedJson)) {
                String encryptedJson = encrypt(unEncryptedJson);
                SPUtils.set(ContextProvider.getContext(), Const.SP_KEY_USER, encryptedJson);
            }
        }

        SPUtils.set(ContextProvider.getContext(), Config.SP_KEY_ENCRYPT_VERSION_USER, ii);
    }


}
