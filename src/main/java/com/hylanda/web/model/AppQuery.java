package com.hylanda.web.model;

import java.io.IOException;
import com.hylanda.web.config.AnnoService;

@AnnoService
public class AppQuery {

	public String queryByString(String para) throws IOException {
		if ("0".equals(para))
			return "hello world!";
		else
			return "nice to meet u!";
	}

}
