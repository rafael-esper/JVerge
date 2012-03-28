package persist;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;

/**
 * All integers in Java are signed and big-endian So this class extends the
 * capabilities of DataOutputStream to write unsigned and/or little endians
 * integers
 * 
 * 
 */
public class ExtendedDataOutputStream extends DataOutputStream {

	public ExtendedDataOutputStream(OutputStream os) {
		super(os);
	}

	public void writeChar(char c) throws IOException {
		this.writeByte((byte) c);
	}

	public void writeString(String s) throws IOException {
		for (int i = 0; i < s.length(); i++)
			this.writeChar(s.charAt(i));
		this.writeChar('\0');
	}

	public void writeFixedString(String s, int size) throws IOException {
		int i;
		for (i = 0; i < s.length(); i++)
			this.writeChar(s.charAt(i));
		this.writeChar('\0');
		i++;
		for (; i < size; i++) {
			this.writeByte(0); //Char(' ');
		}
	}

	// Big and little endian byte data are identical.
	// But it needs to receive a short/int and write a byte
	public void writeUnsignedByte(int i) throws IOException {
		this.writeByte(i);
		// this.writeByte((byte) (i % 128));
	}

	// Precisa receber um int, gravar um unsigned short
	public void writeUnsignedShortLittleEndian(int i) throws IOException {
		short s = (short) (i & 0x0000ffff);
		this.writeShort(Short.reverseBytes(s));
	}

	// http://mindprod.com/jgloss/endian.html
	public void writeSignedIntegerLittleEndian(int i) throws IOException {
		this.writeInt(Integer.reverseBytes(i));
	}

	// http://www.javafaq.nu/java-example-code-1078.html
	public void writeCompressedUnsignedShorts(int[] data) throws IOException {
		byte[] ret = new byte[data.length * 2];

		for (int i = 0; i < data.length; i++) {
			byte b1 = (byte) (data[i] & 0xff); // (data[i] % 128);
			byte b2 = (byte) ((data[i] >>> 8) & 0xff);
			ret[i * 2] = b1;
			ret[i * 2 + 1] = b2;
		}

		writeCompressedBytes(ret);
	}

	// http://java.sun.com/developer/technicalArticles/Programming/compression/
	// http://download.oracle.com/javase/1.4.2/docs/api/java/util/zip/Deflater.html
	public void writeCompressedBytes(byte[] data) throws IOException { // ZLIB
																		// Compression
		byte[] output = new byte[data.length];
		Deflater compresser = new Deflater();
		compresser.setInput(data);
		compresser.finish();
		int compressedDataLength = compresser.deflate(output);
		byte[] compressedOutput = new byte[compressedDataLength];
		for (int i = 0; i < compressedDataLength; i++)
			compressedOutput[i] = output[i];

		this.writeSignedIntegerLittleEndian(data.length);
		this.writeSignedIntegerLittleEndian(compressedDataLength);
		this.write(compressedOutput);
		System.out.println("Uncompressed: " + data.length + " Compressed: "
				+ compressedDataLength + " " + compressedOutput.length);
	}

	// Based on http://www.javafaq.nu/java-example-code-1078.html
	public void writeDoubleLittleEndian(double d) throws IOException {
		long l = Double.doubleToLongBits(d);
		this.write((int) l & 0xFF);
		this.write((int) (l >>> 8) & 0xFF);
		this.write((int) (l >>> 16) & 0xFF);
		this.write((int) (l >>> 24) & 0xFF);
		this.write((int) (l >>> 32) & 0xFF);
		this.write((int) (l >>> 40) & 0xFF);
		this.write((int) (l >>> 48) & 0xFF);
		this.write((int) (l >>> 56) & 0xFF);
	}

	// 2-byte number
	int SHORT_little_endian_TO_big_endian(int i) {
		return ((i >> 8) & 0xff) + ((i << 8) & 0xff00);
	}

	// 4-byte number
	int INT_little_endian_TO_big_endian(int i) {
		return ((i & 0xff) << 24) + ((i & 0xff00) << 8) + ((i & 0xff0000) >> 8)
				+ ((i >> 24) & 0xff);
	}

}