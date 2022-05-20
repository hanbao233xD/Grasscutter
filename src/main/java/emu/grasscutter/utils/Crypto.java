package emu.grasscutter.utils;

import java.security.SecureRandom;
import java.util.Base64;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.net.proto.GetPlayerTokenRspOuterClass.GetPlayerTokenRsp;
import emu.grasscutter.net.proto.QueryCurrRegionHttpRspOuterClass.QueryCurrRegionHttpRsp;

import static emu.grasscutter.Configuration.*;

public final class Crypto {
	private static final SecureRandom secureRandom = new SecureRandom();

	public static byte[] DISPATCH_KEY;
	public static byte[] DISPATCH_SEED;

	public static byte[] ENCRYPT_KEY;
	public static long ENCRYPT_SEED = Long.parseUnsignedLong("11468049314633205968");
	public static byte[] ENCRYPT_SEED_BUFFER = new byte[0];
	
	public static void loadKeys() {
		DISPATCH_KEY = FileUtils.read(KEY("dispatchKey.bin"));
		DISPATCH_SEED = FileUtils.read(KEY("dispatchSeed.bin"));

		ENCRYPT_KEY = FileUtils.read(KEY("secretKey.bin"));
		ENCRYPT_SEED_BUFFER = FileUtils.read(KEY("secretKeyBuffer.bin"));
	}
	
	public static void xor(byte[] packet, byte[] key) {
		try {
			for (int i = 0; i < packet.length; i++) {
				packet[i] ^= key[i % key.length];
			}
		} catch (Exception e) {
			Grasscutter.getLogger().error("Crypto error.", e);
		}
	}
	
	public static void extractSecretKeyBuffer(byte[] data) {
		try {
			GetPlayerTokenRsp p = GetPlayerTokenRsp.parseFrom(data);
			FileUtils.write(KEY("/secretKeyBuffer.bin"), p.getSecretKeyBytes().toByteArray());
			Grasscutter.getLogger().info("Secret Key: " + p.getSecretKey());
		} catch (Exception e) {
			Grasscutter.getLogger().error("Crypto error.", e);
		}
	}
	
	public static void extractDispatchSeed(String data) {
		try {
			QueryCurrRegionHttpRsp p = QueryCurrRegionHttpRsp.parseFrom(Base64.getDecoder().decode(data));
			FileUtils.write(KEY("/dispatchSeed.bin"), p.getRegionInfo().getSecretKey().toByteArray());
		} catch (Exception e) {
			Grasscutter.getLogger().error("Crypto error.", e);
		}
	}
	
	public static byte[] createSessionKey(int length) {
		byte[] bytes = new byte[length];
		secureRandom.nextBytes(bytes);
		return bytes;
	}
}
