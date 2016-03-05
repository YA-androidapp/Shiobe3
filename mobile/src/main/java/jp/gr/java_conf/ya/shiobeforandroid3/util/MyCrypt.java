package jp.gr.java_conf.ya.shiobeforandroid3.util; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import jp.gr.java_conf.ya.shiobeforandroid3.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Base64;
import android.widget.Toast;

public final class MyCrypt {
	public static final String decrypt(final Context context, final String key, final String encrypted_str) {
		try {
			final byte[] decrypted = Base64.decode(encrypted_str, Base64.DEFAULT);
			final SecretKeySpec sksSpec = new SecretKeySpec(key.getBytes(), "Blowfish");
			final Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.DECRYPT_MODE, sksSpec);
			return new String(cipher.doFinal(decrypted));
		} catch (final IllegalArgumentException e) {
			WriteLog.write(context, e);
		} catch (final NoSuchAlgorithmException e) {
			WriteLog.write(context, e);
		} catch (final NoSuchPaddingException e) {
			WriteLog.write(context, e);
		} catch (final InvalidKeyException e) {
			WriteLog.write(context, e);
		} catch (final IllegalBlockSizeException e) {
			WriteLog.write(context, e);
		} catch (final BadPaddingException e) {
			Toast.makeText(context, context.getText(R.string.bad_padding_exception), Toast.LENGTH_SHORT).show();
			WriteLog.write(context, e);
		} catch (final Exception e) {
			WriteLog.write(context, e);
		}
		return "";
	}

	@SuppressLint("TrulyRandom")
	public static final String encrypt(final Context context, final String key, final String text) {
		try {
			final SecretKeySpec sksSpec = new SecretKeySpec(key.getBytes(), "Blowfish");
			final Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, sksSpec);
			final byte[] encrypted = cipher.doFinal(text.getBytes());
			final String encrypted_str = Base64.encodeToString(encrypted, Base64.DEFAULT);
			return encrypted_str;
		} catch (final IllegalArgumentException e) {
			WriteLog.write(context, e);
		} catch (final NoSuchAlgorithmException e) {
			WriteLog.write(context, e);
		} catch (final NoSuchPaddingException e) {
			WriteLog.write(context, e);
		} catch (final InvalidKeyException e) {
			WriteLog.write(context, e);
		} catch (final IllegalBlockSizeException e) {
			WriteLog.write(context, e);
		} catch (final BadPaddingException e) {
			WriteLog.write(context, e);
		} catch (final Exception e) {
			WriteLog.write(context, e);
		}
		return "";
	}
}
