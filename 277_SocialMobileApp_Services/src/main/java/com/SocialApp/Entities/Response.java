/**
 * 
 */
package com.SocialApp.Entities;

/**
 * @author rahul
 *
 */
public class Response {
	private int statusCode;

	public Response() {
	}

	public Response(int statusCode) {
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

}
