package org.qbapi.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by chazz on 6/10/2015.
 */
public class QBDialog {

    private String id;

	private String rawInfo;

	private Date createdAt;

	private Date updatedAt;

	private long userId;

	private List<Long> occupantsIds = new ArrayList<>();

	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

	public String getRawInfo() {
		return rawInfo;
	}

	public void setRawInfo(String rawInfo) {
		this.rawInfo = rawInfo;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getUserId() {
		return userId;
	}

	public List<Long> getOccupantsIds() {
		return occupantsIds;
	}

	public void setOccupantsIds(List<Long> occupantsIds) {
		this.occupantsIds = occupantsIds;
	}

	public void addOccupantsId(Long userId) {
		this.occupantsIds.add(userId);
	}
}
