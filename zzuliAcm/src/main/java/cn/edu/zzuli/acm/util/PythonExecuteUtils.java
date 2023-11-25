package cn.edu.zzuli.acm.util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
/**
 * Python 脚本工具类, hyh
 */
@Component
@Slf4j
public class PythonExecuteUtils {

	private static String[] SHELL = {
			"python3",    	  						  // 0 执行 python命令
			"/root/zzuliAcm/py/pta_spider.py",      // 1 py脚本所在的位置 linux 下 /usr/acm-ranking/py/pta_spider.py
//			"python",
//			"C:\\Users\\86184\\AppData\\Local\\Programs\\Python\\Python39\\python.exe",
//			"C:\\Users\\86184\\Desktop\\acm\\zzuliAcm\\src\\main\\resources\\py\\pta_spider.py",   // 2 sessionID
			"1601181376764964864",                    // 3 比赛的题目集id  老：1331498712576077824
			"not",                                    // 4 向前查询还是向后查询，还是默认查询
			"no id",                                  // 5 向该 id 的前或后进行查询, 如果是默认情况，该值必须为 no id
			"not limit"                               // 6 limit的个数,经由测试，就算是not limit的情况下，爬取的数据最多也是100条
	};

	private static String[] PROBLEM_INFO_SHELL = {
			"python3",								  // 0 执行 python命令
			"/root/zzuliAcm/py/pta_problem_id.py",  // 1 py脚本所在的位置
//			"C:\\Users\\86184\\AppData\\Local\\Programs\\Python\\Python39\\python.exe",
//			"C:\\Users\\86184\\Desktop\\acm\\zzuliAcm\\src\\main\\resources\\py\\pta_problem_id.py",
			"3a9e6672-5bbe-4297-b84f-1df68935e03f",	  // 2 sessionID
			"1601181376764964864"					  // 3 比赛的题目集id
	};

	private static String[] RANKING_SHELL = {
			"python3",								  // 0 执行 python命令
			"/root/zzuliAcm/py/pta_ranking.py",     // 1 py脚本所在的位置
//			"C:\\Users\\86184\\AppData\\Local\\Programs\\Python\\Python39\\python.exe",
//			"C:\\Users\\86184\\Desktop\\acm\\zzuliAcm\\src\\main\\resources\\py\\pta_ranking.py",
			"3a9e6672-5bbe-4297-b84f-1df68935e03f",	  // 2 sessionID
			"1601181376764964864"							  // 3 比赛的题目集id
	};

	public static String getProblemId() {
		return SHELL[3];
	}

	/**
	 * 执行  爬取排行榜的 python脚本
	 *
	 * 在 python 脚本里会将排行榜的信息放进redis key为：ranking_json
	 * @param problems_id
	 * @return
	 */
	public static boolean doRankingPython(String problems_id) {
		if (problems_id != null) {
			RANKING_SHELL[3] = problems_id;
		}

		try {
			//开辟一个子进程去执行 python 脚本
			Process	process = Runtime.getRuntime().exec(RANKING_SHELL);
			BufferedReader reader =
					new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8")); //windows下编码GBK防止java读取乱码　　　　　　　　
			String line;
			while((line=reader.readLine())!=null) {
				System.out.println(line);
			}

			BufferedReader isError =
					new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8"));

			//读取错误的日志
			while((line=isError.readLine())!=null) {
				System.out.println(line);
			}

			reader.close();
			isError.close();
			//hyh
			process.waitFor();
			System.out.println(process.isAlive());
			while (process.isAlive());
			process.destroy();
			return true;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 修改当前的 sessionID
	 * @param sessionID
	 */
	public static void updateTheSessionId(String sessionID) {
		SHELL[2] = sessionID;
		PROBLEM_INFO_SHELL[2] = sessionID;
		RANKING_SHELL[2] = sessionID;
	}

	/**
	 * 更改我们要 爬取的 题目集id
	 * @param problemsId
	 */
	public static void updateTheProblemsId(String problemsId) {
		SHELL[3] = problemsId;
		PROBLEM_INFO_SHELL[3] = problemsId;
		RANKING_SHELL[3] = problemsId;
	}

	/**
	 * 向该 id 前查询
	 * @param id
	 * @param limit
	 */
	public static void selectBeforeTheId(String id, String limit) {
		SHELL[4] = "before";
		SHELL[5] = id;
		SHELL[6] = limit;
		ExecutePython();
		init_shell();
	}

	/**
	 * 向该 id 后查询
	 * @param id
	 * @param limit
	 */
	public static void selectAfterTheId(String id, String limit) {
		SHELL[4] = "after";
		SHELL[5] = id;
		SHELL[6] = limit;
		ExecutePython();
		init_shell();
	}

	//毕竟是一个静态变量，为了防止每次修改后，忘记恢复，所以留了一个备份,用来变成原来的 SHELL
	public static void init_shell() {
		SHELL[4] = "not";
		SHELL[5] = "no id";
		SHELL[6] = "not limit";
	}

	public static boolean getInfoByPy(String problems_id) {
		if (problems_id != null) {
			PROBLEM_INFO_SHELL[3] = problems_id;
		}

//        System.out.println(PROBLEM_INFO_SHELL);

		try {
			File file = new File("problem_set_label.txt");
			if (file.exists())
			    file.delete();
			//开辟一个子进程去执行 python 脚本
            System.out.println("WIN1");
			Process	process = Runtime.getRuntime().exec(PROBLEM_INFO_SHELL);
            System.out.println("WIN2");
			BufferedReader reader =
					new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8")); //windows下编码GBK防止java读取乱码　　　　　　　　
			String line;
			while((line=reader.readLine())!=null) {
				System.out.println(line);
			}

			BufferedReader isError =
					new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8"));

			//读取错误的日志
			while((line=isError.readLine())!=null) {
				System.out.println(line);
			}

			reader.close();
			isError.close();
			//hyh
			process.waitFor();
//			System.out.println(process.isAlive());
//			while (process.isAlive());
			process.destroy();
			return true;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 执行 python 脚本
	 * @returne
	 */
	public static boolean ExecutePython() {
		try {
			String os = System.getProperty("os.name");
			System.out.println(os.toLowerCase().startsWith("win"));

			//开辟一个子进程去执行 python 脚本
			Process	process = Runtime.getRuntime().exec(SHELL);

			BufferedReader reader =
					new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8")); //windows下编码GBK防止java读取乱码　　　　　　　　
			String line;
			while((line=reader.readLine())!=null) {
				System.out.println(line);
			}

			BufferedReader isError =
					new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8"));

			//读取错误的日志
			while((line=isError.readLine())!=null) {
				System.out.println(line);
			}

			reader.close();
			isError.close();
			//hyh
			process.waitFor();
//			System.out.println(process.isAlive());
//			while (process.isAlive());
			process.destroy();
			return true;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}
}
