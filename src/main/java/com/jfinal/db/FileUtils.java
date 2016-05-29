package com.jfinal.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.lucene.util.IOUtils;

public class FileUtils {
	public static final long ONE_KB = 1024;
	public static final long ONE_MB = ONE_KB * ONE_KB;
	public static final long ONE_GB = ONE_KB * ONE_MB;

	public FileUtils() {
	}

	public static String byteCountToDisplaySize(long size) {
		String displaySize;

		if (size / ONE_GB > 0) {
			displaySize = String.valueOf(size / ONE_GB) + " GB";
		} else if (size / ONE_MB > 0) {
			displaySize = String.valueOf(size / ONE_MB) + " MB";
		} else if (size / ONE_KB > 0) {
			displaySize = String.valueOf(size / ONE_KB) + " KB";
		} else {
			displaySize = String.valueOf(size) + " bytes";
		}

		return displaySize;
	}

	public static void touch(File file) throws IOException {
		OutputStream out = new java.io.FileOutputStream(file, true);
		IOUtils.closeWhileHandlingException(out);
	}

	public static String readFileToString(File file, String encoding)
			throws IOException {
		
		StringBuilder builder = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				new java.io.FileInputStream(file), encoding))) {
			
			String line;
			while( (line = reader.readLine()) != null) {
				builder.append(line).append('\n');
			}

		}

		return builder.toString();
	}

}
