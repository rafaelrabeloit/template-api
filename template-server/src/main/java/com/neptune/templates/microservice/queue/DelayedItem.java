package com.neptune.templates.microservice.queue;

import javax.validation.constraints.NotNull;

public class DelayedItem implements Comparable<DelayedItem> {

	private Long time;
	private Object data;
	private Object id;

	public DelayedItem(@NotNull Long time, Object id, Object data) {
		super();
		this.time = time;
		this.data = data;
		this.id = id;
	}

	public DelayedItem(@NotNull Long time, Object id) {
		this(time, id, null);
	}
	
	public DelayedItem(@NotNull Long time) {
		this(time, null, null);
	}

	public DelayedItem(Object id) {
		this(Long.MAX_VALUE, id, null);
	}
	
	public Long getTime() {
		return time;
	}

	public Object getData() {
		return data;
	}

	public Object getId() {
		return id;
	}

	@Override
	public int compareTo(DelayedItem o) {
		return this.getTime().compareTo(o.getTime());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DelayedItem other = (DelayedItem) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DelayedItem [time=" + time + ", data=" + data + ", id=" + id
				+ "]";
	}

	
}
