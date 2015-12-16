package com.mapbar.info.collection.event;
/**
 * 枚举类型 事件基类
 * @author miaowei
 *
 * @param <E>
 */
public class EventInfoBase<E extends Enum<?>> implements EventInfo<E> {

	private E e;
	/**
	 * 获取事件标识
	 */
	@Override
	public E getEvent() {
		
		return e;
	}

	/**
	 * 设置事件标识
	 */
	@Override
	public  void setEvent(E event) {
		
		e = event;
	}

}
