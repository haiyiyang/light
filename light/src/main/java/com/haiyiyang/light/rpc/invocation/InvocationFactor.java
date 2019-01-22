package com.haiyiyang.light.rpc.invocation;

public class InvocationFactor {
	private Class<?> clazz;
	private byte invokeMode;

	public InvocationFactor(Class<?> clazz, byte invokeMode) {
		this.clazz = clazz;
		this.invokeMode = invokeMode;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public byte getInvokeMode() {
		return invokeMode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
		result = prime * result + invokeMode;
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
		InvocationFactor other = (InvocationFactor) obj;
		if (clazz == null) {
			if (other.clazz != null)
				return false;
		} else if (!clazz.equals(other.clazz))
			return false;
		if (invokeMode != other.invokeMode)
			return false;
		return true;
	}
}