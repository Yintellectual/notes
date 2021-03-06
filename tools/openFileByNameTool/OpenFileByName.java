import java.nio.file.*;
import java.util.*;
import java.util.stream.*;


public class OpenFileByName{
	
	public static void main(String ... args)throws Exception{

		Path path1 = Paths.get(args[0]);
		//Path path2 = Paths.get("C:","SpringWorkSpace","zhiyuninfo","deviceservice");
		
		
		
		//searchDirectory(path2, "@RequestMapping").stream().forEach(System.out::println);
		searchDirectory(path1, args[1]).stream().forEach(System.out::println);
		
		
		
		

		
	}
	
	private static List<String> searchDirectory(Path start, String targetString)throws Exception{
			
		final String TARGET_STRING_IN_LOWER_CASE = targetString.toLowerCase();
	
	
	return 
		Files.walk(start, FileVisitOption.FOLLOW_LINKS).filter(path->(!Files.isDirectory(path,LinkOption.NOFOLLOW_LINKS))).map(path->path.toString())
		//.parallel()
		.filter(str->str.toLowerCase().contains(TARGET_STRING_IN_LOWER_CASE))
		.filter(str->!(str.trim().isEmpty()))
		.filter(str-> str!=null)
		.collect(Collectors.toList());
	}
}