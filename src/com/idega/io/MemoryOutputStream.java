package com.idega.io;

import java.io.OutputStream;
/**
 * A utility class to use for temporary buffering and connecting streams in memory.
 *
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class MemoryOutputStream extends OutputStream {

	MemoryFileBuffer buffer;
	private boolean isClosed = false;
	private int position = 0;

	public MemoryOutputStream(MemoryFileBuffer buffer) {
		this.buffer = buffer;
	}

	public void close() {
		//System.out.println("Calling close on MemoryOutputStream");
		isClosed = true;
	}

	/**
	 * @todo IMPLEMENT
	 */
	public void flush() {
	}

	public void write(byte[] b) {
		//System.out.println("Calling write0 on MemoryOutputStream");
		if (!isClosed) {
			int oldPos = position;
			position += b.length;
			write(b, oldPos, b.length);
		}
	}

	public void write(byte[] b, int off, int len) {
		//System.out.println("Calling write1 on MemoryOutputStream");
		if (!isClosed) {
			buffer.write(b, off, len);
		}
	}

	public void write(int b) {
		//System.out.println("Calling write2 on MemoryOutputStream");
		if (!isClosed) {
			byte[] myByte = new byte[1];
			myByte[0] = (byte) b;
			write(myByte);
		}
	}
}