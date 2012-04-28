package persist;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;


public class ExtendedDataInputStream extends DataInputStream {
	
	public ExtendedDataInputStream(InputStream is) {
		super(is);
	}
	
	public String readFixedString(int size) throws IOException {
		char c;
		StringBuffer s = new StringBuffer("");
		boolean reading = true;
		for(int i=0; i<size; i++) {
			c = (char) this.readByte();
			if(c=='\0')
				reading = false;
			if(reading)
				s.append(c);
		}
		return s.toString();
	}

	public int readUnsignedShortLittleEndian() throws IOException {
		short s = Short.reverseBytes(this.readShort());
		return s & 0xffff;
	}
	
	public int readUnsignedIntegerLittleEndian() throws IOException {
		int i = Integer.reverseBytes(this.readInt());
		return i & 0xffffffff;
	}
	
	public int readSignedIntegerLittleEndian() throws IOException {
		return Integer.reverseBytes(this.readInt());
	}
	
	// http://mindprod.com/jgloss/endian.html
	public double readDoubleLittleEndian() throws IOException
	   {
	    // get the 8 unsigned raw bytes, accumulate to a long and then
	    // convert the 64-bit pattern to a double.
	   long accum = 0;
	   for ( int shiftBy=0; shiftBy<64; shiftBy+=8 )
	      {
	      // must cast to long or the shift would be done modulo 32
	      accum |= ( (long)( this.readByte() & 0xff ) ) << shiftBy;
	      }
	   return Double.longBitsToDouble( accum );

	   // there is no such method as Double.reverseBytes( d );
   }		
	
    // http://mindprod.com/jgloss/unsigned.html
	public int[] readCompressedUnsignedShorts() throws IOException {

		byte[] b = readCompressedBytes();
    	
		int[] ret = new int[b.length/2];
    	for(int i=0; i<b.length/2; i++) {
    		byte b1 = b[i*2];
    		byte b2 = b[i*2 + 1];
    		ret[i] = (b2 & 0xff) << 8 | (b1 & 0xff);
    	}
		
    	return ret;
    }

	public byte[] readCompressedUnsignedShortsIntoBytes() throws IOException {

		byte[] b = readCompressedBytes();
		byte[] ret = new byte[b.length];
		for(int i=0;i<b.length; i++)
			ret[i] = (byte) (b[i] & 0xff);
		
    	return ret;	
	}

	// Create an array of BufferedImages based on an array of pixels
	public BufferedImage[] getBufferedImageArrayFromPixels(byte data[], int arraysize,
			int xsize, int ysize) {

		BufferedImage[] ret = new BufferedImage[arraysize];
		WritableRaster wr;
		for(int t=0; t<arraysize; t++) {
			ret[t] = new BufferedImage(xsize, ysize, BufferedImage.TYPE_INT_ARGB);
			wr = ret[t].getRaster();
           int[] a = new int[4];
	        for(int j = 0; j<ysize; j++) {
	        	for(int i = 0; i<xsize; i++) {
		            a[0] = data[t*xsize*ysize*3 + j*xsize*3 + i*3];
		            a[1] = data[t*xsize*ysize*3 + j*xsize*3 + i*3 + 1];
		            a[2] = data[t*xsize*ysize*3 + j*xsize*3 + i*3 + 2];
		            a[3] = 255;
		            if(a[0] == -1 && a[1] == 0 && a[2] == -1) {
		            	a[3] = 0; // transparent
		            	//a[0] = 0; a[2] = 0;
		            }
		            //System.out.println(a[0] + ";" + a[1] + ";" + a[2] + ";" + a[3]);
		            wr.setPixel(i, j, a);
	            }
	        }
		}			
		
		return ret;
	}	
	
	
	//http://download.oracle.com/javase/1.4.2/docs/api/java/util/zip/Deflater.html
	public byte[] readCompressedBytes () throws IOException {
		
		int uncompSize = this.readSignedIntegerLittleEndian();
		int compSize = this.readSignedIntegerLittleEndian();
		
        byte input[] = new byte[compSize];
        for(int j=0; j<compSize; j++)
        	input[j] = this.readByte();
        
        System.out.println("Reading compressed: " + compSize + " into " + uncompSize);
    	Inflater decompresser = new Inflater();
   	 	decompresser.setInput(input, 0, compSize);
   	 	byte[] output = new byte[uncompSize];
   	 	try {
			decompresser.inflate(output);
		} catch (DataFormatException e) {

			e.printStackTrace();
		}
   	 	decompresser.end();

   	 	return output;
    }
 	
}