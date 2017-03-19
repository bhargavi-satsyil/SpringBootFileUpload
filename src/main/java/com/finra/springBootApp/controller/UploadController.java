package com.finra.springBootApp.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.finra.springBootApp.service.FileUploadService;

/**
 * @author BhargavI
 *
 */
@Controller
public class UploadController {
	@Autowired
	FileUploadService uploadService;

	List<String> files = new ArrayList<String>();

	@GetMapping("/")
	public String initUpload(Model model) {
		try {
			uploadService.init();
			return "uploadForm";
		} catch (Exception e) {
			model.addAttribute("message", e.getMessage());
			return "errorPage";
		}

	}
	@PostMapping("/")
	public String uploadFile(@RequestParam("file") MultipartFile file, Model model) {

		try {
			if (file.isEmpty()) {
				model.addAttribute("message", "Please select a file to upload");
				return "errorPage";
			} else {
				uploadService.store(file);
				model.addAttribute("message", "You successfully uploaded " + file.getOriginalFilename() + "!");
				files.add(file.getOriginalFilename());
			}
		} catch (Exception e) {
			model.addAttribute("message", "FAIL to upload " + file.getOriginalFilename() + "!");
			return "errorPage";
		}
		return "uploadForm";
	}

	@GetMapping("/gellallfiles")
	public String getAllFiles(Model model) {
		try {
			model.addAttribute("files",
					files.stream()
							.map(fileName -> MvcUriComponentsBuilder
									.fromMethodName(UploadController.class, "getFile", fileName).build().toString())
							.collect(Collectors.toList()));
			model.addAttribute("totalFiles", "TotalFiles: " + files.size());
			return "listFiles";
		} catch (Exception e) {
			model.addAttribute("message", e.getLocalizedMessage());
			return "errorPage";
		}
	}

	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> getFile(@PathVariable String filename) {
		Resource file = uploadService.loadFile(filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}

	@GetMapping("/error")
	public String errorData() {
		return "errorPage";
	}

}
