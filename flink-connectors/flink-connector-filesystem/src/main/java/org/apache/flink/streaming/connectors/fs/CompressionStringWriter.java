package org.apache.flink.streaming.connectors.fs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.compress.CodecPool;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.io.compress.CompressionOutputStream;
import org.apache.hadoop.io.compress.Compressor;

import java.io.IOException;

/**
 * A base class that compresses the input element and write them to the filesystem. Default serialization is to
 * write events separates by newline.
 * Extends the class and override write() to make custom writing
 * @param <T>
 */
public class CompressionStringWriter<T> extends StreamWriterBase<T> implements Writer<T>{

	private String codecName;

	private transient CompressionOutputStream compressedOutputStream;

	public CompressionStringWriter(String codecName) {
		this.codecName = codecName;
	}

	protected CompressionStringWriter(CompressionStringWriter<T> other) {
		super(other);
		this.codecName = other.codecName;
	}

	@Override
	public void open(FileSystem fs, Path path) throws IOException {
		super.open(fs, path);
		Configuration conf = fs.getConf();
		if (!codecName.equals("None")) {
			CompressionCodecFactory codecFactory = new CompressionCodecFactory(conf);
			CompressionCodec codec = codecFactory.getCodecByName(codecName);
			if (codec == null) {
				throw new RuntimeException("Codec " + codecName + " not found");
			}
			Compressor compressor = CodecPool.getCompressor(codec, conf);
			compressedOutputStream = codec.createOutputStream(getStream(), compressor);

		}
	}

	@Override
	public void close() throws IOException {
		if (compressedOutputStream != null) {
			compressedOutputStream.close();
			compressedOutputStream = null;
		} else {
			super.close();
		}
	}

	@Override
	public void write(Object element) throws IOException {
		getStream();
		compressedOutputStream.write(element.toString().getBytes());
		compressedOutputStream.write('\n');
	}

	@Override
	public CompressionStringWriter<T> duplicate() {
		return new CompressionStringWriter<>(this);
	}
}
