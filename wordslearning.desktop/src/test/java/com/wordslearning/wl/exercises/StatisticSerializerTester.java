package com.wordslearning.wl.exercises;


public class StatisticSerializerTester {

	private final String testStatistic = "<learn_statistic>" +
			"<ar_stat>" +
			"	<ar_id>-343772158</ar_id>" +
			"	<learn_date/>" +
			"	<last_repeat_date/>" +
			"	<last_repeat_interv/>" +
			"	<cur_points>0</cur_points>" +
			"</ar_stat>" +
			"<ar_stat>" +
			"	<ar_id>-343772157</ar_id>" +
			"	<learn_date>31.10.2010</learn_date>" +
			"	<last_repeat_date/>" +
			"	<last_repeat_interv/>" +
			"	<cur_points>0</cur_points>" +
			"</ar_stat>" +
			 "<ar_stat>" +
			 "	<ar_id>-1943850743</ar_id>" +
			 "	<learn_date>31.10.2010</learn_date>" +
			 "	<last_repeat_date>17.10.2011</last_repeat_date>" +
			 "	<last_repeat_interv>1</last_repeat_interv>" +
			 "	<cur_points>0</cur_points>" +
			 "</ar_stat>" +
			 "<ar_stat>" +
			 "	<ar_id>-898652264</ar_id>" +
			 "	<learn_date>31.10.2010</learn_date>" +
			 "	<last_repeat_date>17.10.2011</last_repeat_date>" +
			 "	<last_repeat_interv>1</last_repeat_interv>" +
			 "	<cur_points>0</cur_points>" +
			 "</ar_stat>" +
			"</learn_statistic>";

		
//	@Test
//	public void testExtractRelevantStatistic_allWords() {
//		ByteArrayInputStream bais = new ByteArrayInputStream(
//				testStatistic.getBytes());
//		List<Integer> list = new ArrayList<Integer>();
//		list.add(1);
//		list.add(5);
//		list.add(10);
//		Map<Integer, WLWord> res = StatisticSerializer
//				.extractRelevantStatistic(bais, list);
//		Assert.assertEquals(4, res.size());
//	}
//
//	@Test
//	public void testExtractRelevantStatistic_noWords() {
//		ByteArrayInputStream bais = new ByteArrayInputStream(
//				testStatistic.getBytes());
//		List<Integer> list = new ArrayList<Integer>();
//		list.add(1000);
//		list.add(1001);
//		list.add(1002);
//		Map<Integer, WLWord> res = StatisticSerializer
//				.extractRelevantStatistic(bais, list);
//		Assert.assertEquals(1, res.size());
//	}
	
	
}
