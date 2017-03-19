package com.finra.springBootApp.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author BhargavI
 *
 */
@Service
public class FileUploadService {

	Logger log = LoggerFactory.getLogger(this.getClass().getName());

	private final Path rootLocation = Paths.get("C://temp//");

	public void store(MultipartFile file) throws FileAlreadyExistsException {
		try {
			Files.copy(file.getInputStream(), rootLocation.resolve(file.getOriginalFilename()));
		} catch (FileAlreadyExistsException fae) {
			throw new RuntimeException("File Already Exists..");
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RuntimeException("FAIL!");
		}
	}

	public Resource loadFile(String filename) {
		try {
			Path file = rootLocation.resolve(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new RuntimeException("FAIL!");
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException("FAIL!");
		} catch (Exception e) {
			throw new RuntimeException("FAIL! " + e.getLocalizedMessage());
		}
	}

	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}

	public void init() {
		log.info("File System Creation::");
		try {
			if (!Files.exists(rootLocation)) {
				Files.createDirectory(rootLocation);
			}

		} catch (NoSuchFileException nsfe) {
			throw new RuntimeException("Please check the Storage Files path at Properties file ..");
		} catch (IOException e) {
			throw new RuntimeException("Could not initialize storage!");
		} catch (Exception e) {
			throw new RuntimeException("FAIL! " + e.getLocalizedMessage());
		}
	}
}
