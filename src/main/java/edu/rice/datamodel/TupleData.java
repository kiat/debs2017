package edu.rice.datamodel;

public class TupleData {
	private long id;
	private byte[] payload;

	public TupleData(long id, byte[] payload) {
		this.id = id;
		this.payload = payload;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
}