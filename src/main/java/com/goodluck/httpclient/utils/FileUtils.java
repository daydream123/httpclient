package com.goodluck.httpclient.utils;

import android.content.Context;
import android.text.format.Formatter;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
	private static final long FILE_COPY_BUFFER_SIZE = 1024 * 1024 * 30;
	
	public static void touchNoMediaFile(final File parentDir) {
		final File file = new File(parentDir, ".nomedia");
		try {
			if (!file.exists()) {
				file.createNewFile();
			} else {
				file.setLastModified(System.currentTimeMillis());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean canFileBeDeleted(File sourceFile) {
		File desFile = new File(sourceFile.getAbsoluteFile().getPath());
		// if file exist and can be renamed, so it is closeQuietly state
        return sourceFile.exists() && sourceFile.renameTo(desFile);
    }
	
	/**
	 * Create file, will create parent folder if not exist.
	 */
	public static boolean createFile(File fileToCreate) throws IOException {
        if (!fileToCreate.getParentFile().exists()) {
            fileToCreate.getParentFile().mkdirs();
        }
        return !fileToCreate.exists() && fileToCreate.createNewFile();
    }
	
	/**
	 * Create file, like {@link #createFile(File)}
	 */
	public static boolean createFile(String fileToCreate) throws IOException{
		return createFile(new File(fileToCreate));
	}
	
	/**
	 * Create folders, will create parent folders if not exist
	 */
	public static boolean createFolders(File folder){
		return folder.mkdirs();
	}
	
	/**
	 * Create folder, be the same as {@link #createFolders(File)}
	 */
	public static boolean createFolders(String folder){
		return createFolders(new File(folder));
	}

	/**
	 * Delete file or directory.
	 * 
	 * @param path
	 *            file path or directory
	 * @return delete successfully or faild
	 */
	public static boolean deleteFiles(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return false;
		} else {
			
			// remove latest "/"
			if(path.endsWith(File.separator)){
				int index = path.lastIndexOf(File.separator);
				path = path.substring(0, index);
			}
			
			// handling delete error, some device may throw error: "open failed: EBUSY (device or resource busy)"
			final File renamedFile = new File(path + System.currentTimeMillis());
			new File(path).renameTo(renamedFile);
			
			if (renamedFile.isFile()) {
				return deleteFile(path);
			} else {
				return deleteDirectory(renamedFile.getAbsolutePath());
			}
		}
	}
	
	/**
	 * Delete file or directory, be same as {@link #deleteFiles(String)()}
	 */
	public static boolean deleteFiles(File fileOrDir){
		return deleteFile(fileOrDir.getAbsolutePath());
	}

	/**
	 * Delete file with file path.
	 */
	private static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return file.isFile() && file.exists() && file.delete();
    }

	/**
	 * Delete directory with directory path.
	 */
	private static boolean deleteDirectory(String directory) {
		if (!directory.endsWith(File.separator)) {
			directory = directory + File.separator;
		}
		File dirFile = new File(directory);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean deleted = true;
		File[] files = dirFile.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                deleted = deleteFile(file.getAbsolutePath());
                if (!deleted) {
                    break;
                }
            } else {
                deleted = deleteDirectory(file.getAbsolutePath());
                if (!deleted) {
                    break;
                }
            }
        }
		if (!deleted){
			return false;
		}
        return dirFile.delete();
	}

	/**
	 * Opens a {@link FileInputStream} for the specified file, providing
	 * better error messages than simply calling
	 * <code>new FileInputStream(file)</code>.
	 * 
	 * @param file
	 *            the file to open for input, must not be {@code null}
	 * @return a new {@link FileInputStream} for the specified file
	 * @throws IOException io exception
	 */
    public static FileInputStream openInputStream(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory.");
            }
            if (!file.canRead()) {
                throw new IOException("File '" + file + "' cannot be read.");
            }
        } else {
            throw new FileNotFoundException("File '" + file + "' does not exist.");
        }
        return new FileInputStream(file);
    }
	
    /**
     * Opens a {@link FileOutputStream} for the specified file, checking and
     * creating the parent directory if it does not exist.
     */
    public static FileOutputStream openOutputStream(File file) throws IOException {
        return openOutputStream(file, false);
    }
    
    /**
     * Opens a {@link FileOutputStream} for the specified file, checking and
     * creating the parent directory if it does not exist.
     * <p/>
     * At the end of the method either the stream will be successfully opened,
     * or an exception will have been thrown.
     * <p/>
     * 
     * @param file the file to open for output, must not be {@code null}
     * @param append if {@code true}, then bytes will be added to the
     *               end of the file rather than overwriting
     * @return a new {@link FileOutputStream} for the specified file
     * @throws IOException io exception
     */
    public static FileOutputStream openOutputStream(File file, boolean append) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (!file.canWrite()) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null) {
                if (!parent.mkdirs() && !parent.isDirectory()) {
                    throw new IOException("Directory '" + parent + "' could not be created");
                }
            }
        }
        return new FileOutputStream(file, append);
    }
    
    public static String byteSizeToDisplaySize(Context context, long size){
    	return Formatter.formatFileSize(context, size);
    }
    
	/**
	 * Returns the size of the specified file or directory. If the provided
	 * {@link File} is a regular file, then the file's length is
	 * returned. If the argument is a directory, then the size of the directory
	 * is calculated recursively. If a directory or sub directory is security
	 * restricted, its size will not be included.
	 */
    public static long sizeOf(File file) {
        if (!file.exists()) {
            String message = file + " does not exist";
            throw new IllegalArgumentException(message);
        }

        if (file.isDirectory()) {
            return sizeOfDirectory(file);
        } else {
            return file.length();
        }
    }
    
    /**
     * Counts the size of a directory recursively (sum of the length of all files).
     */
    private static long sizeOfDirectory(File directory) {
        checkDirectory(directory);

        final File[] files = directory.listFiles();
        if (files == null) {  // null if security restricted
            return 0L;
        }
        
        long size = 0;
        for (final File file : files) {
            try {
                if (!isSymlink(file)) {
                    size += sizeOf(file);
                    if (size < 0) {
                        break;
                    }
                }
            } catch (IOException ioe) {
                // Ignore exceptions caught when asking if a File is a symlink.
            }
        }
        return size;
    }
    
    /**
     * Determines whether the specified file is a Symbolic Link rather than an actual file.
     * <p/>
     * Will not return true if there is a Symbolic Link anywhere in the path,
     * only if the specific file is.
     * <p/>
     * <b>Note:</b> the current implementation always returns {@code false} if the system
     * is detected as Windows using {@link FileNameUtils#isSystemWindows()}
     *
     * @param file the file to check
     * @return true if the file is a Symbolic Link
     * @throws IOException if an IO error occurs while checking the file
     */
    public static boolean isSymlink(File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("File must not be null");
        }
        
        if (FileNameUtils.isSystemWindows()) {
            return false;
        }
        
        File fileInCanonicalDir;
        if (file.getParent() == null) {
            fileInCanonicalDir = file;
        } else {
            File canonicalDir = file.getParentFile().getCanonicalFile();
            fileInCanonicalDir = new File(canonicalDir, file.getName());
        }

        return !fileInCanonicalDir.getCanonicalFile().equals(fileInCanonicalDir.getAbsoluteFile());
    }

    /**
     * Checks that the given {@code File} exists and is a directory.
     *
     * @param directory The {@code File} to check.
     * @throws IllegalArgumentException if the given {@code File} does not exist or is not a directory.
     */
    private static void checkDirectory(File directory) {
        if (!directory.exists()) {
            throw new IllegalArgumentException(directory + " does not exist");
        }
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException(directory + " is not a directory");
        }
    }
    
	public static void copyFile(File srcFile, File destFile) throws IOException {
        copyFile(srcFile, destFile, true);
    }
	
	public static void copyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
        if (srcFile == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (destFile == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (!srcFile.exists()) {
            throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
        }
        if (srcFile.isDirectory()) {
            throw new IOException("Source '" + srcFile + "' exists but is a directory");
        }
        if (srcFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
            throw new IOException("Source '" + srcFile + "' and destination '" + destFile + "' are the same");
        }
        File parentFile = destFile.getParentFile();
        if (parentFile != null) {
            if (!parentFile.mkdirs() && !parentFile.isDirectory()) {
                throw new IOException("Destination '" + parentFile + "' directory cannot be created");
            }
        }
        if (destFile.exists() && !destFile.canWrite()) {
            throw new IOException("Destination '" + destFile + "' exists but is read-only");
        }
        doCopyFile(srcFile, destFile, preserveFileDate);
    }
	
	public static void copyDirectory(File srcDir, File destDir) throws IOException{
		copyDirectory(srcDir, destDir, null, true);
	}
	
	public static void copyDirectory(File srcDir, File destDir,
            FileFilter filter, boolean preserveFileDate) throws IOException {
        if (srcDir == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (destDir == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (!srcDir.exists()) {
            throw new FileNotFoundException("Source '" + srcDir + "' does not exist");
        }
        if (!srcDir.isDirectory()) {
            throw new IOException("Source '" + srcDir + "' exists but is not a directory");
        }
        if (srcDir.getCanonicalPath().equals(destDir.getCanonicalPath())) {
            throw new IOException("Source '" + srcDir + "' and destination '" + destDir + "' are the same");
        }

        // Cater for destination being directory within the source directory (see IO-141)
        List<String> exclusionList = null;
        if (destDir.getCanonicalPath().startsWith(srcDir.getCanonicalPath())) {
            File[] srcFiles = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);
            if (srcFiles != null && srcFiles.length > 0) {
                exclusionList = new ArrayList<>(srcFiles.length);
                for (File srcFile : srcFiles) {
                    File copiedFile = new File(destDir, srcFile.getName());
                    exclusionList.add(copiedFile.getCanonicalPath());
                }
            }
        }
        doCopyDirectory(srcDir, destDir, filter, preserveFileDate, exclusionList);
    }
	
	private static void doCopyDirectory(File srcDir, File destDir, FileFilter filter,
            boolean preserveFileDate, List<String> exclusionList) throws IOException {
        // recurse
        File[] srcFiles = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);
        if (srcFiles == null) {  // null if abstract pathname does not denote a directory, or if an I/O error occurs
            throw new IOException("Failed to list contents of " + srcDir);
        }
        if (destDir.exists()) {
            if (!destDir.isDirectory()) {
                throw new IOException("Destination '" + destDir + "' exists but is not a directory");
            }
        } else {
            if (!destDir.mkdirs() && !destDir.isDirectory()) {
                throw new IOException("Destination '" + destDir + "' directory cannot be created");
            }
        }
        if (!destDir.canWrite()) {
            throw new IOException("Destination '" + destDir + "' cannot be written to");
        }
        for (File srcFile : srcFiles) {
            File dstFile = new File(destDir, srcFile.getName());
            if (exclusionList == null || !exclusionList.contains(srcFile.getCanonicalPath())) {
                if (srcFile.isDirectory()) {
                    doCopyDirectory(srcFile, dstFile, filter, preserveFileDate, exclusionList);
                } else {
                    doCopyFile(srcFile, dstFile, preserveFileDate);
                }
            }
        }

        // Do this last, as the above has probably affected directory metadata
        if (preserveFileDate) {
            destDir.setLastModified(srcDir.lastModified());
        }
    }
	
	private static void doCopyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
        if (destFile.exists() && destFile.isDirectory()) {
            throw new IOException("Destination '" + destFile + "' exists but is a directory");
        }

        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel input = null;
        FileChannel output = null;
        try {
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(destFile);
            input  = fis.getChannel();
            output = fos.getChannel();
            long size = input.size();
            long pos = 0;
            long count = 0;
            while (pos < size) {
                count = size - pos > FILE_COPY_BUFFER_SIZE ? FILE_COPY_BUFFER_SIZE : size - pos;
                pos += output.transferFrom(input, pos, count);
            }
        } finally {
            if(output != null){
            	output.close();
            }
            if(fos != null){
            	fos.close();
            }
            if(input != null){
            	input.close();
            }
            if(fis != null){
            	fis.close();
            }
        }

        if (srcFile.length() != destFile.length()) {
            throw new IOException("Failed to copy full contents from '" +
                    srcFile + "' to '" + destFile + "'");
        }
        if (preserveFileDate) {
            destFile.setLastModified(srcFile.lastModified());
        }
    }
	
	/**
     * Moves a directory.
     * <p/>
     * When the destination directory is on another file system, do a "copy and delete".
     *
     * @param srcDir  the directory to be moved
     * @param destDir the destination directory
     * @throws NullPointerException if source or destination is {@code null}
     * @throws IOException  if source or destination is invalid
     * @throws IOException  if an IO error occurs moving the file
     */
    public static void moveDirectory(File srcDir, File destDir) throws IOException {
        if (srcDir == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (destDir == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (!srcDir.exists()) {
            throw new FileNotFoundException("Source '" + srcDir + "' does not exist");
        }
        if (!srcDir.isDirectory()) {
            throw new IOException("Source '" + srcDir + "' is not a directory");
        }
        if (destDir.exists()) {
            throw new FileNotFoundException("Destination '" + destDir + "' already exists");
        }
        boolean rename = srcDir.renameTo(destDir);
        if (!rename) {
            if (destDir.getCanonicalPath().startsWith(srcDir.getCanonicalPath())) {
                throw new IOException("Cannot move directory: " + srcDir + " to a subdirectory of itself: " + destDir);
            }
            copyDirectory(srcDir, destDir);
            deleteFiles(srcDir);
            if (srcDir.exists()) {
                throw new IOException("Failed to delete original directory '" + srcDir +
                        "' after copy to '" + destDir + "'");
            }
        }
    }
}
