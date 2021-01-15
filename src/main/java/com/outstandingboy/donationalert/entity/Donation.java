package com.outstandingboy.donationalert.entity;

public class Donation {
	private String id;
	private String nickName;
	private String comment;
	private long amount;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getComment() {
		if(comment == null) return "";
		if(comment.startsWith("video://") || comment.startsWith("[yt:")) return "[영상 후원]";
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "Donation [id=" + id + ", nickName=" + nickName + ", comment=" + comment + ", amount=" + amount + "]";
	}
}
