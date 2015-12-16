package com.mapbar.info.collection.event;
/**
 * 枚举类型 事件
 * @author miaowei
 *
 * @param <E>
 */
public interface EventInfo<E extends Enum<?>> {

	/**
	 * @return 事件标识
	 */
	E getEvent();

	/**
	 * 设置事件标识
	 * 
	 * @param event
	 *            事件标识
	 */
	void setEvent(E event);

}
