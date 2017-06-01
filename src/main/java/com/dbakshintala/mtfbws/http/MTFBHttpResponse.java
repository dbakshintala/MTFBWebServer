package com.dbakshintala.mtfbws.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

/**
*	This class creates a response for the client's request.
*/
public class MTFBHttpResponse {
	
	 private static final Logger logger = Logger.getLogger(MTFBHttpResponse.class);
	 private static final String protocol = "HTTP/1.0";

		private String status;
		private NavigableMap<String, String> headers = new TreeMap<String, String>();
		private byte[] body = null;

		public MTFBHttpResponse(String status) {
			this.status = status;
			setDate(new Date());
		}

		/*
		 * This method returns response with File.
		 */
		public MTFBHttpResponse getResponseWithFile(File file) {
			if (file.isFile()) {
				try {
					FileInputStream reader = new FileInputStream(file);
					int length = reader.available();
					body = new byte[length];
					reader.read(body);
					reader.close();
					
					setContentLength(length);
					if (file.getName().endsWith(".htm") || file.getName().endsWith(".html")) {
						setContentType(ContentType.HTML);
					} else {
						setContentType(ContentType.TEXT);
					}
				} catch (IOException e) {
					logger.error("Error while reading " + file);
				}
				return this;
			} else {
				return new MTFBHttpResponse(StatusCode.NOT_FOUND)
					.getResponseWithHtmlBody("<html><body>File " + file + " not found.</body></html>");
			}
		}

		/*
		 * method returns response with body.
		 */
		public MTFBHttpResponse getResponseWithHtmlBody(String msg) {
			setContentLength(msg.getBytes().length);
			setContentType(ContentType.HTML);
			body = msg.getBytes();
			return this;
		}

		public void setDate(Date date) {
			headers.put("Date", date.toString());
		}

		public void setContentLength(long value) {
			headers.put("Content-Length", String.valueOf(value));
		}

		public void setContentType(String value) {
			headers.put("Content-Type", value);
		}

		public void removeBody() {
			body = null;
		}

		@Override
		public String toString() {
			String result = protocol + " " + status +"\n";
			for (String key : headers.descendingKeySet()) {
				result += key + ": " + headers.get(key) + "\n";
			}
			result += "\r\n";
			if (body != null) {
				result += new String(body);
			}
			return result;
		}

		/**
		 * Inner class for status codes.
		 * @author DurgaAkshintala
		 *
		 */
		public static class StatusCode {
			public static final String OK = "200 OK";
			public static final String NOT_FOUND = "404 Not Found";
			public static final String NOT_IMPLEMENTED = "501 Not Implemented";
		}

		/**
		 * Inner class for content types
		 * @author DurgaAkshintala
		 *
		 */
		public static class ContentType {
			public static final String TEXT = "text/plain";
			public static final String HTML = "text/html";
		}

}
