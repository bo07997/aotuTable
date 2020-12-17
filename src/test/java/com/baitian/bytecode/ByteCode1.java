package com.baitian.bytecode;

import org.junit.Test;

/**
 * @author ldb
 * @Package com.baitian.bytecode
 * @date 2020/11/27 15:58
 */
public class ByteCode1 {
	/**
	 * 值传递测试
	 */
	@Test
	public void test1() {
		ListNode listNode = new ListNode(0);
		ByteCode1 byteCode1 = new ByteCode1();
		byteCode1.set(listNode);
		System.out.println(listNode.val);
		byteCode1.set2(listNode);
		System.out.println(listNode.val);
	}

	private void set(ListNode listNode) {
		listNode = new ListNode(100);
	}

	private void set2(ListNode listNode) {
		listNode.val = 2;
	}

	/**
	 * 赋值操作测试
	 */
	@Test
	public void test2() {
		Integer a = 1;
		Integer b = 2;
		Integer c = 3;
		Integer d = 3;
		Integer e = 128;
		Integer f = 128;
		Long g = 3L;
		System.out.println(c == d);
		System.out.println(e == f);
		System.out.println(c == (a + b));
		System.out.println(c.equals(a + b));
		System.out.println(g == (a + b));
		System.out.println(g.equals(a + b));
	}
}
