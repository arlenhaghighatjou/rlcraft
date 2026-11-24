package net.minecraft.util;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class CryptManager
{
	private CryptManager()
	{
	}

	public static SecretKey createNewSharedKey()
	{
		try { KeyGenerator generator = KeyGenerator.getInstance("AES"); generator.init(128); return generator.generateKey(); }
		catch (GeneralSecurityException ex) { throw new RuntimeException(ex); }
	}

	public static KeyPair generateKeyPair()
	{
		try { KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA"); generator.initialize(1024); return generator.generateKeyPair(); }
		catch (GeneralSecurityException ex) { throw new RuntimeException(ex); }
	}

	public static byte[] getServerIdHash(String serverId, PublicKey publicKey, SecretKey secretKey)
	{
		try
		{
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.update(serverId.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
			digest.update(secretKey.getEncoded());
			digest.update(publicKey.getEncoded());
			return digest.digest();
		}
		catch (GeneralSecurityException ex) { throw new RuntimeException(ex); }
	}

	public static PublicKey decodePublicKey(byte[] encoded)
	{
		try { return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(encoded)); }
		catch (GeneralSecurityException ex) { throw new RuntimeException(ex); }
	}

	public static SecretKey decryptSharedKey(PrivateKey key, byte[] encrypted)
	{
		return new SecretKeySpec(decryptData(key, encrypted), "AES");
	}

	public static byte[] encryptData(Key key, byte[] data) { return cipherOperation(Cipher.ENCRYPT_MODE, key, data); }
	public static byte[] decryptData(Key key, byte[] data) { return cipherOperation(Cipher.DECRYPT_MODE, key, data); }

	private static byte[] cipherOperation(int mode, Key key, byte[] data)
	{
		try { Cipher cipher = Cipher.getInstance(key.getAlgorithm()); cipher.init(mode, key); return cipher.doFinal(data); }
		catch (GeneralSecurityException ex) { throw new RuntimeException(ex); }
	}

	public static Cipher createNetCipherInstance(int mode, Key key)
	{
		try { Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding"); cipher.init(mode, key, new IvParameterSpec(key.getEncoded())); return cipher; }
		catch (GeneralSecurityException ex) { throw new RuntimeException(ex); }
	}
}
