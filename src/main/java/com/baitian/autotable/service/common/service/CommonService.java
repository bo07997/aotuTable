package com.baitian.autotable.service.common.service;

/**
 * @author ldb
 * @date 2020/5/28 14:25
 */
//@Service
public class CommonService {

	//	@Value("${com.baitian.autotable.jar.location}")
	//	public String jarLocation;
	//	@Value("${com.baitian.autotable.dir.location}")
	//	public String dirLocation;
	//	@Autowired
	//	private TableService tableService;
	//	@Autowired
	//	private JdbcTemplate jdbcTemplate;

	//		private static List<String> NAMES = null;
	//		private static final int TIME = 120000;
	//		private static long mills = 0;
	/**
	 * 查询脚本
	 *
	 * @return
	 */
	//	public synchronized List<String> selectAll() {
	//		long now = System.currentTimeMillis();
	//		if (now - mills < TIME) {
	//			return NAMES;
	//		}
	//		mills = now;
	//		String xmlTail = ".xml";
	//		ArrayList<String> names = getFiles(dirLocation);
	//		NAMES = names.stream()
	//				.filter(str -> xmlTail.equals(str.substring(str.length() - xmlTail.length())))
	//				.map(str -> str.substring(0, str.length() - xmlTail.length()))
	//				.sorted(Comparator.comparing(str -> -tableService.getCount(str))).collect(Collectors.toList());
	//		return NAMES;
	//	}

	//	public static ArrayList<String> getFiles(String path) {
	//		ArrayList<String> files = new ArrayList<String>();
	//		File file = new File(path);
	//		File[] tempList = file.listFiles();
	//
	//		assert tempList != null;
	//		for (int i = 0; i < tempList.length; i++) {
	//			if (tempList[i].isFile()) {
	//				files.add(tempList[i].getName());
	//			}
	//		}
	//		return files;
	//	}


}
