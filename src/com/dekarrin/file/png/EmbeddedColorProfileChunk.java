package com.dekarrin.file.png;

/**
 * Chunk for an embedded ICC color profile.
 */
public class EmbeddedColorProfileChunk extends Chunk implements AncillaryChunk {
	
	/**
	 * The name of the ICC Profile.
	 */
	private String profileName;
	
	/**
	 * The compression method of the profile.
	 */
	private byte compressionMethod;
	
	/**
	 * The compressed profile.
	 */
	private byte[] compressedProfile;
	
	/**
	 * The uncompressed profile.
	 */
	private byte[] profile;
	
	/**
	 * Creates a new EmbeddedColorProfileChunk.
	 *
	 * @param data
	 * The chunk data.
	 *
	 * @param crc
	 * The chunk CRC.
	 */
	public EmbeddedColorProfileChunk(byte[] data, int crc) {
		super(new byte[]{105, 67, 67, 80}, data, crc); // iCCP
		parseData();
	}
	
	/**
	 * Gets the profile name.
	 *
	 * @returns
	 * The name.
	 */
	public String getProfileName() {
		return profileName;
	}
	
	/**
	 * Gets the profile compression method.
	 *
	 * @returns
	 * The compression method.
	 */
	public byte getCompressionMethod() {
		return compressionMethod;
	}
	
	/**
	 * Gets the compressed profile.
	 *
	 * @returns
	 * The compressed profile.
	 */
	public byte[] getCompressedProfile() {
		return compressedProfile;
	}
	
	/**
	 * Gets the uncompressed profile.
	 *
	 * @returns
	 * The uncompressed profile.
	 */
	public byte[] getProfile() {
		if(profile == null) {
			decompressProfile();
		}
		return profile;
	}
	
	/**
	 * Parses data.
	 */
	private void parseData() {
		profileName			= parseString();
		compressionMethod	= parseByte();
		compressedProfile	= parseFinalBytes();
	}
	
	/**
	 * Decompresses profile data.
	 */
	private void decompressProfile() {
		ZlibDecompresser zd = new ZlibDecompresser(compressedProfile);
		profile = zd.decompress();
	}
}