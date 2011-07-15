package com.dekarrin.file.png;

/**
 * Represents an object capable of receiving PNG
 * chunks from a PNG chunk reader.
 */
public interface ChunkListener {
	
	/**
	 * Called whenever a chunk whose type is unknown
	 * is read from the stream.
	 * 
	 * @param chunk
	 * The chunk read from the stream.
	 */
	public void unknownChunkProcessed(Chunk chunk) throws UnknownChunkException;
	
	/**
	 * Called whenever a header chunk
	 * is read from the stream.
	 * 
	 * @param chunk
	 * The chunk read from the stream.
	 */
	public void headerChunkProcessed(HeaderChunk chunk);
	
	/**
	 * Called whenever a palette chunk
	 * is read from the stream.
	 * 
	 * @param chunk
	 * The chunk read from the stream.
	 */
	public void paletteChunkProcessed(PaletteChunk chunk);
	
	/**
	 * Called whenever an image data chunk
	 * is read from the stream.
	 * 
	 * @param chunk
	 * The chunk read from the stream.
	 */
	public void imageDataChunkProcessed(ImageDataChunk chunk);
	
	/**
	 * Called whenever a transparency chunk
	 * is read from the stream.
	 * 
	 * @param chunk
	 * The chunk read from the stream.
	 */
	public void transparencyChunkProcessed(TransparencyChunk chunk);
	
	/**
	 * Called whenever a gamma chunk
	 * is read from the stream.
	 * 
	 * @param chunk
	 * The chunk read from the stream.
	 */
	public void gammaChunkProcessed(GammaChunk chunk);
	
	/**
	 * Called whenever a chromaticities chunk
	 * is read from the stream.
	 * 
	 * @param chunk
	 * The chunk read from the stream.
	 */
	public void chromaticitiesChunkProcessed(ChromaticitiesChunk chunk);
	
	/**
	 * Called whenever a standard RGB color space chunk
	 * is read from the stream.
	 * 
	 * @param chunk
	 * The chunk read from the stream.
	 */
	public void standardRgbColorSpaceChunkProcessed(StandardRgbColorSpaceChunk chunk);
	
	/**
	 * Called whenever a embedded color profile chunk
	 * is read from the stream.
	 * 
	 * @param chunk
	 * The chunk read from the stream.
	 */
	public void embeddedColorProfileChunkProcessed(EmbeddedColorProfileChunk chunk);
	
	/**
	 * Called whenever a text data chunk
	 * is read from the stream.
	 * 
	 * @param chunk
	 * The chunk read from the stream.
	 */
	public void textDataChunkProcessed(TextDataChunk chunk);
	
	/**
	 * Called whenever a compressed text data chunk
	 * is read from the stream.
	 * 
	 * @param chunk
	 * The chunk read from the stream.
	 */
	public void compressedTextDataChunkProcessed(CompressedTextDataChunk chunk);
	
	/**
	 * Called whenever an international text data chunk
	 * is read from the stream.
	 * 
	 * @param chunk
	 * The chunk read from the stream.
	 */
	public void internationalTextDataChunkProcessed(InternationalTextDataChunk chunk);
	
	/**
	 * Called whenever a background color chunk
	 * is read from the stream.
	 * 
	 * @param chunk
	 * The chunk read from the stream.
	 */
	public void backgroundColorChunkProcessed(BackgroundColorChunk chunk);
	
	/**
	 * Called whenever a physical pixel dimensions chunk
	 * is read from the stream.
	 * 
	 * @param chunk
	 * The chunk read from the stream.
	 */
	public void physicalPixelDimensionsChunkProcessed(PhysicalPixelDimensionsChunk chunk);
	
	/**
	 * Called whenever a significant bits chunk
	 * is read from the stream.
	 * 
	 * @param chunk
	 * The chunk read from the stream.
	 */
	public void significantBitsChunkProcessed(SignificantBitsChunk chunk);
	
	/**
	 * Called whenever a suggested palette chunk
	 * is read from the stream.
	 * 
	 * @param chunk
	 * The chunk read from the stream.
	 */
	public void suggestedPaletteChunkProcessed(SuggestedPaletteChunk chunk);
	
	/**
	 * Called whenever a palette histogram chunk
	 * is read from the stream.
	 * 
	 * @param chunk
	 * The chunk read from the stream.
	 */
	public void paletteHistogramChunkProcessed(PaletteHistogramChunk chunk);
	
	/**
	 * Called whenever a modification time chunk
	 * is read from the stream.
	 * 
	 * @param chunk
	 * The chunk read from the stream.
	 */
	public void modificationTimeChunkProcessed(ModificationTimeChunk chunk);
	
	/**
	 * Called whenever a trailer chunk is read from
	 * the stream.
	 * 
	 * @param chunk
	 * The chunk read from the stream.
	 */
	public void trailerChunkProcessed(TrailerChunk chunk);
}
