package com.socket.jinanaxle;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * 
 *<br><b>类描述:</b>
 *<pre>所示PO的父类</pre>
 *@see
 *@since
 */
public class BaseDomain implements Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1929265361936456295L;
	
	private static short __id = 0;
	
	public static Long createId()
	{
		String t = null;
		String r = null;
		synchronized (BaseDomain.class) {
			if(++__id >= 1000)
				__id = 0;
			r = StringUtils.leftPad(Short.toString(__id), 3, '0');
			t = String.valueOf(System.currentTimeMillis());
		}
		return Long.valueOf(t + r);
	}

	public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
