package test;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.BeforeClass;
import org.junit.Test;

public class JavaStreamTest {
	static Stream<Integer> stream;
	static ArrayList<String> list;
	static List<String> list2;
	int counter;

	void wasCalled() {
		System.out.println("wasCalled");
		counter++;
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		stream = Arrays.asList(1, 2, 3, 4, 5, 6).stream();

		list = new ArrayList<>();
		list.add("One");
		list.add("OneAndOnly");
		list.add("Derek");
		list.add("Change");
		list.add("factory");
		list.add("justBefore");
		list.add("Italy");
		list.add("Italy");
		list.add("Thursday");
		list.add("");
		list.add("");

		list2 = Arrays.asList("abc1", "abc2", "abc5", "abc3", "abcdef5");
	}

	@Test
	public void test_filter() throws Exception {
		assertEquals(Arrays.asList(1, 3, 5), stream.filter(i -> i % 2 == 1).collect(toList()));

		assertEquals(Arrays.asList("OneAndOnly", "Thursday"),
				list.stream().filter(str -> str.contains("d")).collect(toList()));
	}

	@Test
	public void test_limit() throws Exception {
		assertEquals(Arrays.asList(1, 2, 3, 4), stream.limit(4).collect(toList()));
	}

	@Test
	public void test_map() throws Exception {
		assertEquals(Arrays.asList(2, 4, 6, 8), stream.limit(4).map(n -> n * 2).collect(toList()));
	}

	@Test
	public void test_skip() throws Exception {
		assertEquals(Arrays.asList(3, 4, 5, 6), stream.skip(2).collect(toList()));
	}

	@Test
	public void test_builder() throws Exception {
		assertEquals(Arrays.asList("aa", "bb", "cc"),
				Stream.<String>builder().add("aa").add("bb").add("cc").build().collect(toList()));
	}

	@Test
	public void test_iterate() throws Exception {
		assertEquals(Arrays.asList(40, 42, 44, 46), Stream.iterate(40, n -> n + 2).limit(4).collect(toList()));
	}

	@Test
	public void test_generate() throws Exception {
		assertEquals(Arrays.asList(1, 1, 1, 1, 1), Stream.generate(() -> 1).limit(5).collect(toList()));
		assertEquals(Arrays.asList("a", "a", "a", "a"), Stream.generate(() -> "a").limit(4).collect(toList()));
	}

	@Test
	public void test_StreamOfArray() throws Exception {
		String[] arr = new String[] { "a", "b", "c" };
		assertEquals(Arrays.asList("a", "b", "c"), Arrays.stream(arr).collect(toList()));
		assertEquals(Arrays.asList("b", "c"), Arrays.stream(arr, 1, 3).collect(toList()));
	}

	@Test
	public void test_findAny() throws Exception {
		assertEquals("a", Stream.of("a", "b", "c").findAny().get());
	}

	@Test
	public void test_findFirst() throws Exception {
		// findFirst는 병렬적으로 실행되도 첫 번째 원소를 선택할 수 있다.
		assertEquals("a", Stream.of("a", "b", "c").parallel().findFirst().get());
	}

	@Test
	public void test_lazyInvocation() throws Exception {
		counter = 0;
		Stream<String> stream2 = list2.stream().filter(n -> {
			wasCalled();
			return n.contains("5");
		});
		assertEquals(0, counter);
		assertEquals(Arrays.asList("abc5", "abcdef5"), stream2.collect(toList()));
		assertEquals(5, counter);

		counter = 0;
		Stream<String> stream3 = list2.stream().filter(n -> {
			wasCalled();
			return n.contains("5");
		}).map(n -> {
			System.out.println("map");
			return n.toUpperCase();
		});

		// assertEquals("ABC5", stream3.findFirst().get());
		// assertEquals(3, counter);
		assertEquals(Arrays.asList("ABC5", "ABCDEF5"), stream3.collect(toList()));
		assertEquals(5, counter);

		counter = 0;
		long size = list2.stream().map(element -> {
			wasCalled();
			return element.substring(0, 3);
		}).skip(2).count();
		System.out.println("size = " + size);
	}

	@Test
	public void test_orderOfExecution() throws Exception {
		// skip()의 위치에 따라 연산의 수가 줄어든다.
		counter = 0;
		long size = list2.stream().map(element -> {
			wasCalled();
			return element.substring(0, 3);
		}).skip(2).count();

		assertEquals(5, counter);
		assertEquals(3, size);

		counter = 0;
		size = list2.stream().skip(2).map(element -> {
			wasCalled();
			return element.substring(0, 3);
		}).count();

		assertEquals(3, counter);
		assertEquals(3, size);
	}

	@Test
	public void test_reduce() throws Exception {
		OptionalInt reduced = IntStream.range(1, 4).reduce((a, b) -> a + b);
		assertEquals(6, reduced.getAsInt());

		int reducedTwoParams = IntStream.range(1, 4).reduce(10, (a, b) -> a + b);
		assertEquals(16, reducedTwoParams);

		int reducedParallel = Arrays.asList(1, 2, 3).parallelStream().reduce(10, (a, b) -> a + b, (a, b) -> {
			System.out.println("combiner was called");
			return a + b;
		});
		// 모든 요소를 병렬적으로 실행하여 11 + 12 + 13이 된다.
		assertEquals(36, reducedParallel);
	}
	
	

}