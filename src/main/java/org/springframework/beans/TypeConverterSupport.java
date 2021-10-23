/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.beans;

import java.lang.reflect.Field;

import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Base implementation of the {@link TypeConverter} interface, using a package-private delegate.
 * Mainly serves as base class for {@link BeanWrapperImpl}.
 *
 * @author Juergen Hoeller
 * @since 3.2
 * @see SimpleTypeConverter
 *
 * 父类 PropertyEditorRegistrySupport  &&  TypeConverter
 * 本质是一个转换器 上层接口是 TypeConverter
 * 这个是一个抽象方法
 *
 *
 */
public abstract class TypeConverterSupport extends PropertyEditorRegistrySupport implements TypeConverter {

	/**
	 * 内部的一个转化器
	 * 虽然没有实现 TypeConverter接口
	 * 但是内部方法均有 ，本类相关的转换方法 均 委托这个对象去实际执行操作
	 *
	 */
	@Nullable
	TypeConverterDelegate typeConverterDelegate;


	/** 将Object value   转为特定的类型 并返回
	 * 其他几个 均调用的 doConvert 方法
	 *  */
	@Override
	@Nullable
	public <T> T convertIfNecessary(@Nullable Object value, @Nullable Class<T> requiredType) throws TypeMismatchException {
		//  本质调用的是 doConvert
		return doConvert(value, requiredType, null, null);
	}

	@Override
	@Nullable
	public <T> T convertIfNecessary(@Nullable Object value, @Nullable Class<T> requiredType, @Nullable MethodParameter methodParam)
			throws TypeMismatchException {

		return doConvert(value, requiredType, methodParam, null);
	}

	@Override
	@Nullable
	public <T> T convertIfNecessary(@Nullable Object value, @Nullable Class<T> requiredType, @Nullable Field field)
			throws TypeMismatchException {

		return doConvert(value, requiredType, null, field);
	}

	/**
	 *
	 * 直接进行值转换时
	 * methodParam  及 field  的值均为 null
	 *
	 * 核心方法
	 * 内容全部委托给 typeConverterDelegate 去执行 相关操作
	 *
	 * @param value
	 * @param requiredType
	 * @param methodParam
	 * @param field
	 * @param <T>
	 * @return
	 * @throws TypeMismatchException
	 */
	@Nullable
	private <T> T doConvert(@Nullable Object value,@Nullable Class<T> requiredType,
			@Nullable MethodParameter methodParam, @Nullable Field field) throws TypeMismatchException {


		// 进行基础断言 非空 判断
		Assert.state(this.typeConverterDelegate != null, "No TypeConverterDelegate");
		try {
			if (field != null) {
				return this.typeConverterDelegate.convertIfNecessary(value, requiredType, field);
			}
			else {
				return this.typeConverterDelegate.convertIfNecessary(value, requiredType, methodParam);
			}
		}
		catch (ConverterNotFoundException | IllegalStateException ex) {
			throw new ConversionNotSupportedException(value, requiredType, ex);
		}
		catch (ConversionException | IllegalArgumentException ex) {
			throw new TypeMismatchException(value, requiredType, ex);
		}
	}

}
