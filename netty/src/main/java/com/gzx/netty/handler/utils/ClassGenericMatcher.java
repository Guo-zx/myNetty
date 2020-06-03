package com.gzx.netty.handler.utils;

import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * @Author guozhixuan
 * @Description
 * @Date 2020/6/1 18:09
 * @Version V1.0
 */
public class ClassGenericMatcher {

    public static boolean isAcceptClass(Object object, Class<?> parametrizedSuperclass, String typeParamName, Object msg, ChannelHandlerContext ctx) {
        final Class<?> thisClass = object.getClass();
        Class<?> currentClass = thisClass;
        for (; ; ) {
            if (currentClass.getSuperclass() == parametrizedSuperclass) {

                try {
                    int typeParamIndex = -1;
                    TypeVariable<?>[] typeParams = currentClass.getSuperclass().getTypeParameters();
                    for (int i = 0; i < typeParams.length; i++) {
                        if (typeParamName.equals(typeParams[i].getName())) {
                            typeParamIndex = i;
                            break;
                        }
                    }

                    Type genericSuperType = currentClass.getGenericSuperclass();

                    Type[] actualTypeParams = ((ParameterizedType) genericSuperType).getActualTypeArguments();

                    Type actualTypeParam = actualTypeParams[typeParamIndex];

                    Class headerClass = Class.forName(actualTypeParam.getTypeName());

                    if (headerClass.isInstance(msg)) {
                        return true;
                    }
                } catch (ClassNotFoundException e) {
                    ctx.fireExceptionCaught(
                            new RuntimeException("Class.forName(actualTypeParam.getTypeName()) ClassNotFoundException " + e));
                }
                return false;
            } else {
                currentClass = currentClass.getSuperclass();
            }
        }

    }
}
