package com.dekarrin.file.png;

import com.dekarrin.zip.*;
import com.dekarrin.util.ByteComposer;

/**
 * Chunk for an embedded ICC color profile.
 */
public class EmbeddedColorProfileChunk extends AncillaryChunk {
	
	/**
	 * The name of the ICC Profile.
	 */
	private String profileName;
	
	/**
	 * The compression method of the profile.
	 */
	private int compressionMethod;
	
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
	 */
	public EmbeddedColorProfileChunk(byte[] data) {
		super(Chunk.iCCP, data);
		parseData();
	}
	
	/**
	 * Creates a new EmbeddedColorProfileChunk from data.
	 *
	 * @param name
	 * The name of the profile.
	 *
	 * @parma data
	 * The profile data.
	 */
	public EmbeddedColorProfileChunk(String name, byte[] data) {
		super(Chunk.iCCP);
		setProperties(name, data, null, 0);
		setChunkData(createDataBytes());
	}
	
	/**
	 * Gets the profile name.
	 *
	 * @return
	 * The name.
	 */
	public String getProfileName() {
		return profileName;
	}
	
	/**
	 * Gets the profile compression method.
	 *
	 * @return
	 * The compression method.
	 */
	public int getCompressionMethod() {
		return compressionMethod;
	}
	
	/**
	 * Gets the compressed profile.
	 *
	 * @return
	 * The compressed profile.
	 */
	public byte[] getCompressedProfile() {
		return compressedProfile;
	}
	
	/**
	 * Gets the uncompressed profile.
	 *
	 * @return
	 * The uncompressed profile.
	 */
	public byte[] getProfile() {
		if(profile == null) {
			decompressProfile();
		}
		return profile;
	}
	
	/**
	 * Sets the internal properties of this chunk.
	 *
	 * @param name
	 * The name of the profile.
	 *
	 * @param data
	 * The data in the profile. If this is null, it will be
	 * generated from the compressed data. This cannot be null
	 * if compressedData is also null.
	 *
	 * @param compressedData
	 * The compressed data in the profile. If this is null,
	 * it will be generated from the data. This cannot be null
	 * if data is also null.
	 *
	 * @param compressionMethod
	 * The compression method to use for compressing the data.
	 */
	private void setProperties(String name, byte[] data, byte[] compressedData, int compressionMethod) {
		this.profileName = name;
		this.compressionMethod = compressionMethod;
		if(data != null) {
			this.profile = data;
		}
		if(compressedData != null) {
			this.compressedProfile = compressedData;
		}
		if(data == null) {
			decompressProfile();
		}
		if(compressedData == null) {
			compressProfile();
		}
	}
	
	/**
	 * Creates the actual data bytes from the internal properties.
	 *
	 * @return
	 * The data bytes.
	 */
	private byte[] createDataBytes() {
		int dataLength = 2 + profileName.length() + compressedProfile.length;
		ByteComposer bytes = new ByteComposer(dataLength);
		bytes.composeString(profileName, true);
		bytes.composeInt(compressionMethod, 1);
		bytes.composeBytes(compressedProfile);
		return bytes.toArray();
	}
	
	/**
	 * Parses data.
	 */
	private void parseData() {
		String profileName			= parser.parseString();
		int compressionMethod		= parser.parseInt(1);
		byte[] compressedProfile	= parser.parseRemainingBytes();
		setProperties(profileName, null, compressedProfile, compressionMethod);
	}
	
	/**
	 * Decompresses profile data.
	 */
	private void decompressProfile() {
		ZlibDecompresser zd = new ZlibDecompresser(compressedProfile);
		profile = zd.decompress();
	}
	
	/**
	 * Compresses profile data.
	 */
	private void compressProfile() {
		ZlibCompresser zc = new ZlibCompresser(profile);
		compressedProfile = zc.compress();
	}
}