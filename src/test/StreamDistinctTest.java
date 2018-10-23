package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import lombok.AllArgsConstructor;
import lombok.Data;

public class StreamDistinctTest {

	@Data
	@AllArgsConstructor
	class testClass {
		int num;
		DateTime date;
	}

	static Function<testClass, Object> func = d -> d.getDate().dayOfMonth().roundFloorCopy();

	static Predicate<testClass> distinctByKey(Function<testClass, Object> func) {
		Map<Object, Boolean> map = new HashMap<>();

		return t -> map.putIfAbsent(func.apply(t), Boolean.TRUE) == null;
	}

	@Test
	public void test_distinct() {
		List<Integer> list = Arrays.asList(1, 1, 2, 2, 2, 3, 4, 5, 5);
		list = list.stream().distinct().collect(Collectors.toList());
		Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5), list);
	}

	@Test
	public void test_distinct2() {
		List<testClass> list = new ArrayList<>();

		list.add(new testClass(1, new DateTime(2018, 10, 23, 0, 0, 0)));
		list.add(new testClass(2, new DateTime(2018, 10, 23, 0, 0, 0)));
		list.add(new testClass(3, new DateTime(2018, 10, 24, 0, 0, 0)));
		list.add(new testClass(3, new DateTime(2018, 10, 25, 0, 0, 0)));
		list.add(new testClass(4, new DateTime(2018, 10, 25, 0, 0, 0)));
		list.add(new testClass(4, new DateTime(2018, 10, 26, 0, 0, 0)));

		Assert.assertEquals(6, list.stream().distinct().count());

		List<testClass> distinctByNum = list.stream().filter(distinctByKey(t -> t.getNum()))
				.collect(Collectors.toList());

		Assert.assertEquals(Arrays.asList(1, 2, 3, 4),
				distinctByNum.stream().map(testClass::getNum).collect(Collectors.toList()));

		List<testClass> distinctByDate = list.stream().filter(distinctByKey(func)).collect(Collectors.toList());
		
		List<DateTime>expected = Arrays.asList(new DateTime(2018, 10, 23, 0, 0, 0), new DateTime(2018, 10, 24, 0, 0, 0),
				new DateTime(2018, 10, 25, 0, 0, 0), new DateTime(2018, 10, 26, 0, 0, 0));
		List<DateTime> actual = distinctByDate.stream().map(testClass::getDate).collect(Collectors.toList());
		
		Assert.assertEquals(expected, actual);
	}
}
