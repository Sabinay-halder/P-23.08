package com.widevision.prayergrid.util;

public interface AsyncCallback<T>
{
	public void onOperationCompleted(T result, Exception e);
}
