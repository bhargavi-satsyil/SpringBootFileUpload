package com.finra.springBootApp.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;

/**
 * @author BhargavI
 *
 */
@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler
	public String handleError(MultipartException e, Model model) {
		model.addAttribute("message", e.getLocalizedMessage());
		return "errorPage";

	}

}
