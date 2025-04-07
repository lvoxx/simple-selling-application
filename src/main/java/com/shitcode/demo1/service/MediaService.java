package com.shitcode.demo1.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.shitcode.demo1.exception.model.FileReadException;
import com.shitcode.demo1.exception.model.FolderNotFoundException;
import com.shitcode.demo1.service.impl.LocalMediaServiceImpl.TypeOfMedia;

/**
 * Generic interface for media file operations, supporting both local and cloud
 * storage systems.
 * This service provides operations for saving, retrieving, and managing media
 * files
 * regardless of the underlying storage implementation.
 *
 * <p>
 * Implementations of this interface should handle:
 * </p>
 * <ul>
 * <li>File storage (local filesystem, cloud storage, etc.)</li>
 * <li>File compression and optimization</li>
 * <li>Directory structure management</li>
 * <li>Error handling and logging</li>
 * </ul>
 */
public interface MediaService {

    /**
     * Saves multiple image files to the storage system and returns their access
     * URLs.
     * The implementation should handle:
     * <ul>
     * <li>File validation and MIME type checking</li>
     * <li>Image compression and optimization</li>
     * <li>Storage path generation</li>
     * <li>Duplicate handling</li>
     * </ul>
     *
     * @param images List of image files to be saved
     * @return List of URLs or paths to access the saved images
     * @throws Exception if validation fails or storage operations encounter errors
     */
    List<String> saveImagesFile(List<MultipartFile> images) throws Exception;

    /**
     * Saves a video file to the storage system and returns its access URL.
     * The implementation should handle:
     * <ul>
     * <li>Video format validation</li>
     * <li>Video compression and transcoding</li>
     * <li>Storage path generation</li>
     * <li>Large file handling</li>
     * </ul>
     *
     * @param video The video file to be saved
     * @return URL or path to access the saved video
     * @throws Exception if validation fails or storage operations encounter errors
     */
    String saveVideoFile(MultipartFile video) throws Exception;

    /**
     * Retrieves a media file from the storage system.
     * The implementation should handle:
     * <ul>
     * <li>Path resolution and validation</li>
     * <li>Access control and permissions</li>
     * <li>Resource streaming</li>
     * <li>Not found scenarios</li>
     * </ul>
     *
     * @param filePathAndNameWithExtension The path to the file, including filename
     *                                     and extension
     * @return Resource object representing the media file
     * @throws FileNotFoundException if the file doesn't exist in storage
     * @throws FileReadException     if the file exists but cannot be read
     */
    Resource findFile(String filePathAndNameWithExtension) throws FileNotFoundException, FileReadException;

    /**
     * Saves a single media file to the storage system with specified type and
     * compression preferences.
     * The implementation should handle:
     * <ul>
     * <li>Storage location determination</li>
     * <li>File name generation</li>
     * <li>Directory creation if needed</li>
     * <li>Compression based on the isCompressed flag</li>
     * </ul>
     *
     * @param file         The file to save
     * @param type         The type of media (e.g., image, video)
     * @param isCompressed Whether to store in compressed format
     * @return The complete path or identifier for the saved file
     * @throws IOException if the file cannot be saved
     */
    String saveFileToServer(MultipartFile file, TypeOfMedia type, boolean isCompressed) throws IOException;

    /**
     * Deletes a single media file from the storage system.
     * The implementation should handle:
     * <ul>
     * <li>Path validation</li>
     * <li>Permission checking</li>
     * <li>Resource cleanup</li>
     * <li>Associated metadata removal</li>
     * </ul>
     *
     * @param filePathAndNameWithExtension The path to the file to delete
     * @throws IOException           if deletion operation fails
     * @throws FileNotFoundException if the file doesn't exist
     */
    void deleteFile(String filePathAndNameWithExtension) throws IOException, FileNotFoundException;

    /**
     * Batch deletes multiple media files from the storage system.
     * The implementation should handle:
     * <ul>
     * <li>Batch operation optimization</li>
     * <li>Partial success scenarios</li>
     * <li>Transaction management if applicable</li>
     * <li>Bulk resource cleanup</li>
     * </ul>
     *
     * @param filePathsAndNamesWithExtensions List of paths to files to delete
     * @throws IOException           if deletion operations fail
     * @throws FileNotFoundException if any file doesn't exist
     */
    void deleteFiles(List<String> filePathsAndNamesWithExtensions) throws IOException, FileNotFoundException;

    /**
     * Deletes a directory and all its contents from the storage system.
     * Handles recursive deletion and resource cleanup.
     *
     * @param folderName Path or identifier of the folder to delete
     * @throws FolderNotFoundException if the folder cannot be deleted or doesn't exist
     */
    void deleteFolder(String folderName) throws FolderNotFoundException;

    /**
     * Deletes multiple directories and their contents from the storage system.
     * Attempts to delete all folders even if some operations fail.
     *
     * @param folderNames List of folder paths or identifiers to delete
     * @throws FolderNotFoundException if any folder cannot be deleted
     */
    void deleteFolder(List<String> folderNames) throws FolderNotFoundException;
}
