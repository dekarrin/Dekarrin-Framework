package com.dekarrin.file.png;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.CRC32;

import com.dekarrin.io.StreamFailureException;

class ChunkOutputStream extends DataOutputStream {
	
	/**
	 * Whether the magic number has already been written.
	 */
	private boolean magicNumberWritten = false;

	/**
	 * The values in the header of a valid PNG file.
	 */
	public static final int[] MAGIC_NUMBER = {137, 80, 78, 71, 13, 10, 26, 10};
	
	/**
	 * Creates a new ChunkOutputStream for an OutputStream.
	 * 
	 * @param oStream
	 * The output stream.
	 */
	public ChunkOutputStream(OutputStream oStream) {
		super(oStream);
	}
	
	/**
	 * Writes the magic number to the stream.
	 */
	public void writeMagicNumber() throws StreamFailureException {
		if(!magicNumberWritten) {
			magicNumberWritten = true;
			for(int i = 0; i < MAGIC_NUMBER.length; i++) {
				try {
					byte nextNumber = (byte)(MAGIC_NUMBER[i] & 0xff);
					write(new byte[]{nextNumber}, 0, 1);
				} catch(IOException e) {
					throw new StreamFailureException(e.getMessage());
				}
			}
		}
	}
	
	/**
	 * Writes the next Chunk.
	 * 
	 * @param chunk
	 * The next Chunk in the stream.
	 */
	public void writeChunk(Chunk chunk) throws StreamFailureException {
		if(!magicNumberWritten) {
			writeMagicNumber();
		}
		try {
			writeInt(chunk.getLength());
			writeInt(chunk.getType());
			write(chunk.getData(), 0, chunk.getData().length);
			writeInt(generateChecksum(chunk.getType(), chunk.getData()));
			flush();
		} catch(IOException e) {
			throw new StreamFailureException(e.getMessage());
		}
	}
	
	/**
	 * Generates the checksum for the chunk.
	 * 
	 * @param type
	 * The type code of the chunk whose checksum is being generated.
	 * 
	 * @param data
	 * The data from the chunk whose checksum is being generated.
	 * 
	 * @return
	 * The generated checksum.
	 */
	private int generateChecksum(int type, byte[] data) {
		CRC32 crcGenerator = new CRC32();
		crcGenerator.update(Chunk.typeToBytes(type));
		crcGenerator.update(data);
		long calculatedChecksum = crcGenerator.getValue();
		return (int)calculatedChecksum;
	}
}
