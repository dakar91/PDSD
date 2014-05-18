package com.example.mywapp;

public class Comment {
	public boolean left;
	public String comment;
	public MessageType type;

	public Comment(boolean left, String comment, MessageType type) {
		super();
		this.left = left;
		this.comment = comment;
		this.type = type;
	}

}