import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DataCheckAll {
	// file_data.csv 파일 안에 있는 내용을 모두 읽어온다.
	public static List<List<String>> readCSV(String folderUrl) {
		List<List<String>> csvList = new ArrayList<List<String>>();
		File csv = new File(folderUrl+"\\file_data.csv");
		//File csv = new File(folderUrl+"file_data.csv");
        BufferedReader br = null;
        String line = "";

        try {
            br = new BufferedReader(new FileReader(csv));
            // readLine()은 파일에서 개행된 한 줄의 데이터를 읽어온다.
            while ((line = br.readLine()) != null) { 
                List<String> aLine = new ArrayList<String>();
                // 파일의 한 줄을 ,로 나누어 배열에 저장 후 리스트로 변환한다.
                String[] lineArr = line.split(","); 
                aLine = Arrays.asList(lineArr);
                csvList.add(aLine);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                	// 사용 후 BufferedReader를 닫아준다.
                    br.close(); 
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        return csvList;
	}	
	//해당 경로에 저장된 파일들의 이름만 가져옴 (메타데이터는 제외)
	public static List<String> readFileName(String folderUrl) {
		// file_data.csv에서 'id'가 존재하는 위치를 저장하기 위한 변수
		int id_start_num = 0;
		//file_data.csv의 내용 저장 List
		List<List<String>> FileData = readCSV(folderUrl);
		//file_data.csv의 내용(FileData)에서 dataset_id만 저장하는 List
		List<String> FileId = new ArrayList<String>();
		// csv 파일 내용에서
		for(int i = 0; i < FileData.size(); i++) {
			if(FileData.get(i) != null) {
				// 'id'가 저장되어 있는 위치를 찾아서
				if(FileData.get(i).get(0).equals("id")) {
					// 변수에 저장
					id_start_num = i;
					break;
				}
			}
		}
		// id_start_num은 'id' 컬럼의 위치이기 때문에 +1부터 반복문 시작
		for(int i = id_start_num + 1; i < FileData.size(); i++) {
			// i번째 행이 null 값이 아니면
			if(FileData.get(i) != null && !FileData.get(i).get(0).contains("Column1") && !FileData.get(i).get(0).contains("id")) {
				// FileId 리스트(FileData에서 파일 id만 저장하는 리스트)에 각 파일 id 저장
				FileId.add(FileData.get(i).get(0));

			}
		}
		// 입력받은 경로에 있는 모든 파일을 가져옴 (폴더안의 파일)
		File folder = new File(folderUrl);
		String[] AllFiles = folder.list();
		List<String> Files = new ArrayList<String>();
		
		for(int i = 0; i < AllFiles.length; i++) {
			for(int j = 0; j < FileId.size(); j++) {
				// file_data.csv 파일에 dataset_id가 존재하면
				if(AllFiles[i].contains(FileId.get(j))) {
					// Files 리스트에 저장
					Files.add(AllFiles[i]);
				}
			}
		}
		// file_data.csv의 dataset_id와 일치하는 폴더안의 파일들을 저장한 리스트를 반환
		return Files;
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub	
		new FileRowCheck(args[0]);
		new FileListAndDataCheck(args[0]);
		new ColumnDataCheck(args[0]);
		
		System.out.println("모든 검증이 완료되었습니다.");
	}
}
