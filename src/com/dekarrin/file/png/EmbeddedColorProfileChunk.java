package com.dekarrin.file.png;

import com.dekarrin.io.InvalidFormatException;
import com.dekarrin.util.ByteComposer;

/**
 * Chunk for an embedded ICC color profile.
 */
class EmbeddedColorProfileChunk extends Chunk {
	
	/**
	 * The name of the ICC Profile.
	 */
	private String profileName;
	
	/**
	 * The compression method of the profile.
	 */
	private CompressionEngine compressionEngine;
	
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
	 * @throws InvalidFormatException
	 * If an invalid compression method is specified.
	 */
	public EmbeddedColorProfileChunk(byte[] data) throws InvalidFormatException {
		super(Chunk.iCCP, data);
		parseData();
	}
	
	/**
	 * Creates a new EmbeddedColorProfileChunk from data.
	 *
	 * @param name
	 * The name of the profile.
	 *
	 * @param data
	 * The profile data.
	 * 
	 * @param cm
	 * The compression method to use on the data.
	 * 
	 * @throws InvalidFormatException
	 * If an invalid compression method is specified.
	 */
	public EmbeddedColorProfileChunk(String name, byte[] data, CompressionEngine cm) throws InvalidFormatException {
		super(Chunk.iCCP);
		setProperties(name, data, null, cm);
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
	public CompressionEngine getCompressionEngine() {
		return compressionEngine;
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
	 * 
	 * @throws InvalidFormatException
	 * If an invalid compression method is specified.
	 */
	private void setProperties(String name, byte[] data, byte[] compressedData, CompressionEngine cm) throws InvalidFormatException {
		this.profileName = name;
		this.compressionEngine = cm;
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
		bytes.composeInt(compressionEngine.dataValue(), 1);
		bytes.composeBytes(compressedProfile);
		return bytes.toArray();
	}
	
	/**
	 * Parses data.
	 * 
	 * @throws InvalidFormatException
	 * If an invalid compression method is specified.
	 */
	private void parseData() throws InvalidFormatException {
		String profileName			= parser.parseString();
		CompressionEngine cm		= CompressionEngine.fromData(parser.parseInt(1));
		byte[] compressedProfile	= parser.parseRemainingBytes();
		setProperties(profileName, null, compressedProfile, cm);
	}
	
	/**
	 * Decompresses profile data.
	 * 
	 * @throws InvalidFormatException
	 * If an invalid compression method is specified.
	 */
	private void decompressProfile() throws InvalidFormatException {
		compressionEngine.setContents(compressedProfile);
		profile = compressionEngine.decompress();
	}
	
	/**
	 * Compresses profile data.
	 * 
	 * @throws InvalidFormatException
	 * If an invalid compression method is specified.
	 */
	private void compressProfile() throws InvalidFormatException {
		compressionEngine.setContents(profile);
		compressedProfile = compressionEngine.compress();
	}
}