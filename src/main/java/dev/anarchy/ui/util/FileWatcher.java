package dev.anarchy.ui.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FileWatcher {
	private Map<File, String> fileHash = new HashMap<>();
	
	private List<File> files = new ArrayList<>();
	
	private Thread thread;
	
	private int pollRateMillis = 500;
	
	public FileWatcher() {
		this.thread = new Thread(getRunnable());
		this.thread.setDaemon(true);
		this.thread.start();
	}

	/**
	 * Get runnable used by internal FileWatcher thread.
	 */
	private Runnable getRunnable() {
		return new Runnable() {
			@Override
			public void run() {
				logic();
			}
		};
	}

	/**
	 * Core logic for FileWatcher
	 */
	private void logic() {
		while(true) {
			sleep(pollRateMillis);
			
			List<File> toRemove = new ArrayList<>();
			
			synchronized(files) {
				for (int i = 0; i < files.size(); i++) {
					if ( i >= files.size() )
						continue;
					
					File file = files.get(i);
					if ( file == null )
						continue;
					
					try {
						boolean same = compare(file);
						if ( !same ) {
							fileHash.put(file, md5(file));
							onFileChanged(file);
						}
					} catch(Exception e) {
						e.printStackTrace();
						toRemove.add(file);
					}
				}
				
				files.removeAll(toRemove);
			}
		}
	}
	
	/**
	 * Compares file hash with already-stored file hash.
	 */
	private boolean compare(File file) {
		String originalHash = fileHash.get(file);
		if ( originalHash == null || "".equals(originalHash) )
			throw new RuntimeException("Cannot get hash from file. No cached data.");
		
		String newHash = md5(file);
		if ( originalHash == null || "".equals(originalHash) )
			throw new RuntimeException("Cannot get hash from file. Invalid file: " + file.getAbsolutePath());
		
		return originalHash.equals(newHash);
	}

	/**
	 * Sleep current thread.
	 */
	private void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			//
		}
	}
	
	/**
	 * callback function when a file is changed.
	 */
	protected abstract void onFileChanged(File changedFile);
	
	/**
	 * Rate at which the files are checked for changes (millis).
	 */
	public void setPollRate(int millis) {
		this.pollRateMillis = millis;
	}

	/**
	 * Mark a file to be tracked.
	 */
	public void track(File file) {
		synchronized(files) {
			if ( files.contains(file) )
				return;
			
			files.add(file);
			
			synchronized(fileHash) {
				fileHash.put(file, md5(file));
			}
		}
	}
	
	/**
	 * Stop tracking a file.
	 */
	public void untrack(File file) {
		synchronized(files) {
			files.remove(file);
		}
	}
	
	/**
	 * get MD5 as string from file
	 */
	private static String md5(File file) {
		try {
			return toHexString(getMD5Checksum(file));
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * Get raw MD5 data from file
	 */
	private static byte[] getMD5Checksum(File filename) throws Exception {
		InputStream fis = new FileInputStream(filename);
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] buffer = new byte[1024];
		int aux;
		do {
			aux = fis.read(buffer);
			if (aux > 0) {
				md.update(buffer, 0, aux);
			}
		} while (aux != -1);
		
		fis.close();
		return md.digest();
	}

	/**
	 * Convert raw MD5 data to hex string
	 */
	private static String toHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes)
			sb.append(String.format("%02x", b & 0xff));
		return sb.toString();
	}
}
