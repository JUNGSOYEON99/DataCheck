import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

//csv파일 row 수 체크 (20줄 이상인지)
public class FileRowCheck extends DataCheckAll{
	// 7번 검증 _ 파일별 라인 수 체크 (20줄 이상인지)
	public static int countLine(String file, String folderUrl) {
		int lines = 0;
		String fileName = folderUrl + "\\" + file;
		//String fileName = folderUrl + file;
		try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
	      while (reader.readLine() != null) lines++;
		} catch (IOException e) {
	      e.printStackTrace();
		}
		// 각 파일의 첫 번째 내용은 컬럼 내용이기 때문에 제외함.
		return lines - 1;
	}	
	
	// 검증 결과 출력
	public FileRowCheck(String folderUrl) throws IOException {
		List<String> filesName = readFileName(folderUrl);
		int FileLine = 0;
		File folder = new File(folderUrl);
		String[] etcFiles = folder.list();
		int etcFiles_length = etcFiles.length;
		for(int i = 0; i < etcFiles_length; i++) {
			File f = new File(folderUrl + "\\" + etcFiles[i]);
			//File f = new File(folderUrl + etcFiles[i]);
			if(f.isDirectory()) {
				etcFiles[i] = "";
			}
		}
		
		int check = 0;
		int filesName_size = filesName.size();
		for(int i = 0; i < filesName_size; i++) {
			if(filesName.get(i) != null && filesName.get(i) != "") {
				FileLine = countLine(filesName.get(i),folderUrl);
				if(FileLine < 20) {
					System.out.println(filesName.get(i) + "은 20줄 미만입니다.");
					
					check++;
				} 
			}
		}
		// file_data.csv에는 없지만 폴더에는 있는 파일의 row수를 검증하기 위해
		for(int j = 0; j < etcFiles_length; j++) {
			if(etcFiles[j].contains("column_data") || etcFiles[j].contains("datasets") || etcFiles[j].contains("file_data") || etcFiles[j].contains("keywords")) {
				etcFiles[j] = "";
			}
			// filesName에 있는 파일은 ""로 변환
			for(int z = 0; z < filesName_size; z++) {
				if(etcFiles[j].contains(filesName.get(z))) {
					etcFiles[j] = "";
				}
			}
		}
		// file_data.csv에는 없지만 폴더에는 있는 파일의 row수를 검증
		for(int i = 0; i < etcFiles_length; i++) {
			if(etcFiles[i] != null && etcFiles[i] != "") {
				FileLine = countLine(etcFiles[i],folderUrl);
				if(FileLine < 20) {
					System.out.println(etcFiles[i] + "은 20줄 미만입니다.");
					check++;
				} 
			}
		}
		// 만약 해당 경로의 모든 파일이 20줄 이상이면 이를 출력
		if(check == 0) {
			System.out.println("해당 경로 안의 모든 파일은 20줄 이상입니다.");
		}
		System.out.println("\nFileRow 검사 완료!");
		System.out.println("==============================================================================");
		System.out.println("\n");
	}
}
